package cancel.service;

import cancel.async.AsyncTask;
import cancel.domain.*;
import cancel.workflow.CancelWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class CancelServiceImpl implements CancelService {

	private String hostname = "172.25.234.77";
	@Autowired
	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public CancelOrderResult cancelOrder(CancelOrderInfo info, String loginToken, String loginId) throws Exception { // TODO
																														// Workflow
																														// Initiator
		// WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker
		// instance of the Temporal server.
		WorkflowServiceStubsOptions stubOptions = WorkflowServiceStubsOptions.newBuilder().setTarget(hostname+":7233")
				.build();
		WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(stubOptions);
		WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue("DEFAULT")
				// A WorkflowId prevents this it from having duplicate instances, remove it to
				// duplicate.
				.setWorkflowId("cancel-order-workflow").build();
		// WorkflowClient can be used to start, signal, query, cancel, and terminate
		// Workflows.
		WorkflowClient client = WorkflowClient.newInstance(service);
		// WorkflowStubs enable calls to methods as if the Workflow object is local, but
		// actually perform an RPC.
		CancelWorkflow workflow = client.newWorkflowStub(CancelWorkflow.class, options);
		return workflow.cancel(info, loginToken, loginId);
		// Asynchronous execution. This process will exit after making this call.
		// CompletableFuture<CancelOrderResult> we =
		// WorkflowClient.execute(workflow::cancel, info, loginToken, loginId);
		// System.out.printf("\nTransfer of $%f from account %s to account %s is
		// processing\n", amount, fromAccount, toAccount);
		// System.out.printf("\nWorkflowID: %s RunID: %s", we.getWorkflowId(),
		// we.getRunId());
	}

	@Override
	public boolean sendEmail(NotifyInfo notifyInfo) {
		System.out.println("[Cancel Order Service][Send Email]");
		boolean result = restTemplate.postForObject(
				"http://"+hostname+":17853/notification/order_cancel_success", notifyInfo, Boolean.class);
		return result;
	}

	@Override
	public CalculateRefundResult calculateRefund(CancelOrderInfo info) {
		GetOrderByIdInfo getFromOrderInfo = new GetOrderByIdInfo();
		getFromOrderInfo.setOrderId(info.getOrderId());
		GetOrderResult orderResult = getOrderByIdFromOrder(getFromOrderInfo);
		if (orderResult.isStatus() == true) {
			Order order = orderResult.getOrder();
			if (order.getStatus() == OrderStatus.NOTPAID.getCode() || order.getStatus() == OrderStatus.PAID.getCode()) {
				if (order.getStatus() == OrderStatus.NOTPAID.getCode()) {
					CalculateRefundResult result = new CalculateRefundResult();
					result.setStatus(true);
					result.setMessage("Success.Calculate Refund Not paid");
					result.setRefund("0");
					System.out.println("[Cancel Order][Refund Price] From Order Service.Not Paid.");
					return result;
				} else {
					CalculateRefundResult result = new CalculateRefundResult();
					result.setStatus(true);
					result.setMessage("Success.Calculate Refund");
					result.setRefund(calculateRefund(order));
					System.out.println("[Cancel Order][Refund Price] From Order Service.Paid.");
					return result;
				}
			} else {
				CalculateRefundResult result = new CalculateRefundResult();
				result.setStatus(false);
				result.setMessage("Order Status Cancel Not Permitted");
				result.setRefund("error");
				System.out.println("[Cancel Order][Refund Price] Order. Cancel Not Permitted.");

				return result;
			}
		} else {
			GetOrderByIdInfo getFromOtherOrderInfo = new GetOrderByIdInfo();
			getFromOtherOrderInfo.setOrderId(info.getOrderId());
			GetOrderResult orderOtherResult = getOrderByIdFromOrderOther(getFromOtherOrderInfo);
			if (orderOtherResult.isStatus() == true) {
				Order order = orderOtherResult.getOrder();
				if (order.getStatus() == OrderStatus.NOTPAID.getCode()
						|| order.getStatus() == OrderStatus.PAID.getCode()) {
					if (order.getStatus() == OrderStatus.NOTPAID.getCode()) {
						CalculateRefundResult result = new CalculateRefundResult();
						result.setStatus(true);
						result.setMessage("Success.Calculate not pay");
						result.setRefund("0");
						System.out.println("[Cancel Order][Refund Price] From Order Other Service.Not Paid.");
						return result;
					} else {
						CalculateRefundResult result = new CalculateRefundResult();
						result.setStatus(true);
						result.setMessage("Success.Calculate pay");
						result.setRefund(calculateRefund(order));
						System.out.println("[Cancel Order][Refund Price] From Order Other Service.Paid.");
						return result;
					}
				} else {
					CalculateRefundResult result = new CalculateRefundResult();
					result.setStatus(false);
					result.setMessage("Order Status Cancel Not Permitted");
					result.setRefund("error");
					System.out.println("[Cancel Order][Refund Price] Order Other. Cancel Not Permitted.");
					return result;
				}
			} else {
				CalculateRefundResult result = new CalculateRefundResult();
				result.setStatus(false);
				result.setMessage("Order Not Found");
				result.setRefund("error");
				System.out.println("[Cancel Order][Refund Price] Order not found.");
				return result;
			}
		}
	}

	@Override
	public String calculateRefund(Order order) {
		if (order.getStatus() == OrderStatus.NOTPAID.getCode()) {
			return "0.00";
		}
		System.out.println("[Cancel Order] Order Travel Date:" + order.getTravelDate().toString());
		Date nowDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(order.getTravelDate());
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(order.getTravelTime());
		int hour = cal2.get(Calendar.HOUR);
		int minute = cal2.get(Calendar.MINUTE);
		int second = cal2.get(Calendar.SECOND);
		Date startTime = new Date(year, month, day, hour, minute, second);
		System.out.println("[Cancel Order] nowDate  :" + nowDate.toString());
		System.out.println("[Cancel Order] startTime:" + startTime.toString());
		if (nowDate.after(startTime)) {
			System.out.println("[Cancel Order] Ticket expire refund 0");
			return "0";
		} else {
			double totalPrice = Double.parseDouble(order.getPrice());
			double price = totalPrice * 0.8;
			DecimalFormat priceFormat = new java.text.DecimalFormat("0.00");
			String str = priceFormat.format(price);
			System.out.println("[Cancel Order]calculate refund - " + str);
			return str;
		}
	}

	@Override
	public ChangeOrderResult cancelFromOrder(ChangeOrderInfo info) {
		System.out.println("[Cancel Order Service][Change Order Status] Changing....");
		ChangeOrderResult result = restTemplate.postForObject("http://"+hostname+":12031/order/update", info,
				ChangeOrderResult.class);
		return result;
	}

	@Override
	public ChangeOrderResult cancelFromOtherOrder(ChangeOrderInfo info) {
		System.out.println("[Cancel Order Service][Change Order Status] Changing....");
		ChangeOrderResult result = restTemplate.postForObject("http://"+hostname+":12032/orderOther/update",
				info, ChangeOrderResult.class);
		return result;
	}

	@Override
	public boolean drawbackMoney(String money, String userId) {
		System.out.println("[Cancel Order Service][Draw Back Money] Draw back money...");
		DrawBackInfo info = new DrawBackInfo();
		info.setMoney(money);
		info.setUserId(userId);
		String result = restTemplate.postForObject("http://"+hostname+":18673/inside_payment/drawBack",
				info, String.class);
		if (result.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public GetAccountByIdResult getAccount(GetAccountByIdInfo info) {
		System.out.println("[Cancel Order Service][Get By Id]");
		GetAccountByIdResult result = restTemplate.postForObject("http://"+hostname+":12349/account/findById", info,
				GetAccountByIdResult.class);
		return result;
	}

	@Override
	public GetOrderResult getOrderByIdFromOrder(GetOrderByIdInfo info) {
		System.out.println("[Cancel Order Service][Get Order] Getting....");
		GetOrderResult cor = restTemplate.postForObject("http://"+hostname+":12031/order/getById/", info,
				GetOrderResult.class);
		return cor;
	}

	@Override
	public GetOrderResult getOrderByIdFromOrderOther(GetOrderByIdInfo info) {
		System.out.println("[Cancel Order Service][Get Order] Getting....");
		GetOrderResult cor = restTemplate.postForObject("http://"+hostname+":12032/orderOther/getById/", info,
				GetOrderResult.class);
		return cor;
	}
	
    public ChangeOrderResult updateOtherOrderStatusToCancelAsync(ChangeOrderInfo info) throws InterruptedException{

        Thread.sleep(4000);

        System.out.println("[Cancel Order Service][Change Order Status]");
        ChangeOrderResult result = restTemplate.postForObject("http://"+hostname+":12032/orderOther/update",info,ChangeOrderResult.class);
        return result;

    }
    
    public Boolean drawBackMoneyForOrderCancelAsync(String money, String userId,String orderId, String loginToken) throws InterruptedException {
    	//return asyncTask.drawBackMoneyForOrderCancel(money, userId, orderId, loginToken);
    			/*********************** Fault Reproduction - Error Process Seq *************************/
    	        //double op = new Random().nextDouble();
    	        //if(op < 1.0){
    	        //    System.out.println("[Cancel Order Service] Delay Process，Wrong Cancel Process");
    	            //Thread.sleep(8000);
    	        //} else {
    	        //    System.out.println("[Cancel Order Service] Normal Process，Normal Cancel Process");
    	        //}


    	        //1.Search Order Info
    	        System.out.println("[Cancel Order Service][Get Order] Getting....");
    	        GetOrderByIdInfo getOrderInfo = new GetOrderByIdInfo();
    	        getOrderInfo.setOrderId(orderId);
    	        GetOrderResult cor = restTemplate.postForObject(
    	                "http://"+hostname+":12032/orderOther/getById/"
    	                ,getOrderInfo,GetOrderResult.class);
    	        Order order = cor.getOrder();
    	        System.out.println("[Cancel Order Service]Got order successfully");
    	        
    	        //2.Change order status to cancelling
    	        order.setStatus(OrderStatus.Canceling.getCode());
    	        ChangeOrderInfo changeOrderInfo = new ChangeOrderInfo();
    	        changeOrderInfo.setOrder(order);
    	        changeOrderInfo.setLoginToken(loginToken);
    	        ChangeOrderResult changeOrderResult = restTemplate.postForObject("http://"+hostname+":12032/orderOther/update",changeOrderInfo,ChangeOrderResult.class);
    	        if(changeOrderResult.isStatus() == false){
    	            System.out.println("[Cancel Order Service]Unexpected error");
    	        }
    	        //3.do drawback money
    	        System.out.println("[Cancel Order Service][Draw Back Money] Draw back money...");
    	        DrawBackInfo info = new DrawBackInfo();
    	        info.setMoney(money);
    	        info.setUserId(userId);
    	        String result = restTemplate.postForObject("http://"+hostname+":18673/inside_payment/drawBack",info,String.class);
    	        if(result.equals("true")){
    	            return true;
    	        }else{
    	            return false;
    	        }
    	        /*****************************************************************************/
    }

}
