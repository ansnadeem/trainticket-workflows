package cancel.workflow;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import cancel.activities.CancelActivities;
import cancel.domain.CancelOrderInfo;
import cancel.domain.CancelOrderResult;
import cancel.domain.ChangeOrderInfo;
import cancel.domain.ChangeOrderResult;
import cancel.domain.GetAccountByIdInfo;
import cancel.domain.GetAccountByIdResult;
import cancel.domain.GetOrderByIdInfo;
import cancel.domain.GetOrderResult;
import cancel.domain.NotifyInfo;
import cancel.domain.Order;
import cancel.domain.OrderStatus;
import cancel.domain.SeatClass;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;

public class CancelWorkflowImpl implements CancelWorkflow {
    private final RetryOptions retryoptions = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1))
            .setMaximumInterval(Duration.ofSeconds(100))
            .setBackoffCoefficient(2)
            .setMaximumAttempts(500)
            .build();
    private final ActivityOptions options = ActivityOptions.newBuilder()
            // Timeout options specify when to automatically timeout Activities if the process is taking too long.
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            // Optionally provide customized RetryOptions.
            // Temporal retries failures by default, this is simply an example.
            .setRetryOptions(retryoptions)
            .build();
    // ActivityStubs enable calls to methods as if the Activity object is local, but actually perform an RPC.
    private final CancelActivities activities = Workflow.newActivityStub(CancelActivities.class, options);
	@Override
	public CancelOrderResult cancel(CancelOrderInfo info,String loginToken,String loginId) throws InterruptedException, ExecutionException {
		GetOrderByIdInfo getFromOrderInfo = new GetOrderByIdInfo();
        getFromOrderInfo.setOrderId(info.getOrderId());
        GetOrderResult orderResult = activities.getOrderByIdFromOrder(getFromOrderInfo);
        if(orderResult.isStatus() == true){
            System.out.println("[Cancel Order Service][Cancel Order] Order found G|H");
            Order order = orderResult.getOrder();
            if(order.getStatus() == OrderStatus.NOTPAID.getCode()
                    || order.getStatus() == OrderStatus.PAID.getCode() || order.getStatus() == OrderStatus.CHANGE.getCode()){

                order.setStatus(OrderStatus.CANCEL.getCode());
                ChangeOrderInfo changeOrderInfo = new ChangeOrderInfo();
                changeOrderInfo.setLoginToken(loginToken);
                changeOrderInfo.setOrder(order);
                ChangeOrderResult changeOrderResult = activities.cancelFromOrder(changeOrderInfo);
                if(changeOrderResult.isStatus() == true){
                    CancelOrderResult finalResult = new CancelOrderResult();
                    finalResult.setStatus(true);
                    finalResult.setMessage("Success.Cancel Order");
                    System.out.println("[Cancel Order Service][Cancel Order] Success.");
                    //Draw back money
                    String money = activities.calculateRefundFromOrder(order);
                    boolean status = activities.drawbackMoney(money,loginId);
                    if(status == true){
                        System.out.println("[Cancel Order Service][Draw Back Money] Success.");

                        GetAccountByIdInfo getAccountByIdInfo = new GetAccountByIdInfo();
                        getAccountByIdInfo.setAccountId(order.getAccountId().toString());
                        GetAccountByIdResult result = activities.getAccount(getAccountByIdInfo);
                        if(result.isStatus() == false){
                            return null;
                        }

                        NotifyInfo notifyInfo = new NotifyInfo();
                        notifyInfo.setDate(new Date().toString());


                        notifyInfo.setEmail(result.getAccount().getEmail());
                        notifyInfo.setStartingPlace(order.getFrom());
                        notifyInfo.setEndPlace(order.getTo());
                        notifyInfo.setUsername(result.getAccount().getName());
                        notifyInfo.setSeatNumber(order.getSeatNumber());
                        notifyInfo.setOrderNumber(order.getId().toString());
                        notifyInfo.setPrice(order.getPrice());
                        notifyInfo.setSeatClass(SeatClass.getNameByCode(order.getSeatClass()));
                        notifyInfo.setStartingTime(order.getTravelTime().toString());

                        activities.sendEmail(notifyInfo);

                    }else{
                        System.out.println("[Cancel Order Service][Draw Back Money] Fail.");
                    }



                    return finalResult;
                }else{
                    CancelOrderResult finalResult = new CancelOrderResult();
                    finalResult.setStatus(false);
                    finalResult.setMessage(changeOrderResult.getMessage());
                    System.out.println("[Cancel Order Service][Cancel Order] Fail.Reason:" + changeOrderResult.getMessage());
                    return finalResult;
                }

            }else{
                CancelOrderResult result = new CancelOrderResult();
                result.setStatus(false);
                result.setMessage("Order Status Cancel Not Permitted");
                System.out.println("[Cancel Order Service][Cancel Order] Order Status Not Permitted.");
                return result;
            }
        }else{
            GetOrderByIdInfo getFromOtherOrderInfo = new GetOrderByIdInfo();
            getFromOtherOrderInfo.setOrderId(info.getOrderId());
            GetOrderResult orderOtherResult = activities.getOrderByIdFromOrderOther(getFromOtherOrderInfo);
            if(orderOtherResult.isStatus() == true){
                System.out.println("[Cancel Order Service][Cancel Order] Order found Z|K|Other");

                Order order = orderOtherResult.getOrder();
                if(order.getStatus() == OrderStatus.NOTPAID.getCode()
                        || order.getStatus() == OrderStatus.PAID.getCode() || order.getStatus() == OrderStatus.CHANGE.getCode()){

                    System.out.println("[Cancel Order Service][Cancel Order] Order status ok");

                    order.setStatus(OrderStatus.CANCEL.getCode());
                    ChangeOrderInfo changeOrderInfo = new ChangeOrderInfo();
                    changeOrderInfo.setLoginToken(loginToken);
                    changeOrderInfo.setOrder(order);
//                    ChangeOrderResult changeOrderResult = cancelFromOtherOrder(changeOrderInfo);


                    /*********************** Fault Reproduction - Error Process Seq *************************/
                    //1.return money
                    String money = activities.calculateRefundFromOrder(order);
                    //Future<Boolean> taskDrawBackMoney = asyncTask.drawBackMoneyForOrderCancel(money,loginId,order.getId().toString(),loginToken);
                    Promise<Boolean> taskDrawBackMoney = Async.function((t1, t2, t3, t4) -> {
						try {
							return activities.drawBackMoneyForOrderCancelAsync(t1, t2, t3, t4);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}, money, loginId, money, loginToken);
                    //2.change status to [canceled]
                    //Future<ChangeOrderResult> taskCancelOrder = asyncTask.updateOtherOrderStatusToCancel(changeOrderInfo);
                    Promise<ChangeOrderResult> taskCancelOrder = Async.function(t1 -> {
						try {
							return activities.updateOtherOrderStatusToCancelAsync(t1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					},changeOrderInfo);
                    ChangeOrderResult changeOrderResult = taskCancelOrder.get();
                    boolean drawBackMoneyStatus = taskDrawBackMoney.get();

//                    boolean status = true;
                    //while(!taskCancelOrder.isDone() || !taskDrawBackMoney.isDone()) {

//                        if(!taskDrawBackMoney.isDone() && taskCancelOrder.isDone()){
//                            status = false;
//                        }
                    //}

                    //drawBackMoneyStatus = taskDrawBackMoney.get();
                    //changeOrderResult = taskCancelOrder.get();
                    System.out.println("[Cancel Order Service][Cancel Order] Two Process Done");


                    /********************************************************************************/
                    GetOrderByIdInfo localInfo = new GetOrderByIdInfo();
                    localInfo.setOrderId(order.getId().toString());
                    GetOrderResult gor = activities.getOrderByIdFromOrderOther(localInfo);
                    boolean status;
                    if(gor.getOrder().getStatus() != OrderStatus.CANCEL.getCode()){
                        System.out.println("订单状态不对");
                        status = false;
                    }else{
                        System.out.println("订单状态正确");
                        status = true;
                    }


                    if(changeOrderResult.isStatus() == true && drawBackMoneyStatus == true){
                        if(status == false) {
                            System.out.println("[Cancel Order Service]Fail. Processes Seq");
                            throw new RuntimeException("[Error Process Seq]");
                        }else{
                            CancelOrderResult finalResult = new CancelOrderResult();
                            finalResult.setStatus(true);
                            finalResult.setMessage("Success.Processes Seq.");
                            System.out.println("[Cancel Order Service]Success.Processes Seq.");
                            return finalResult;
                        }
                    }else if(changeOrderResult.isStatus() == true && drawBackMoneyStatus == false){
                        throw new RuntimeException("[????] Draw Back Money Fail but Cancel Order Success.");

                    }else if(changeOrderResult.isStatus() == false && drawBackMoneyStatus == true){
                        throw new RuntimeException("[???????] Draw Back Money Success but Cancel Order Fail.");
                    }else{
                        throw new RuntimeException("[???????] All Fail");
                    }

                }else{
                    CancelOrderResult result = new CancelOrderResult();
                    result.setStatus(false);
                    result.setMessage("Order Status Cancel Not Permitted");
                    System.out.println("[Cancel Order Service][Cancel Order] Order Status Not Permitted.");
                    return result;
                }
            }else{
                CancelOrderResult result = new CancelOrderResult();
                result.setStatus(false);
                result.setMessage("Order Not Found");
                System.out.println("[Cancel Order Service][Cancel Order] Order Not Found.");
                return result;
            }
        }
	}
}
