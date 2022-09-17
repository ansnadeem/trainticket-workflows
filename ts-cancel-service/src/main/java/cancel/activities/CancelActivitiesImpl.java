package cancel.activities;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cancel.async.AsyncTask;
import cancel.domain.CalculateRefundResult;
import cancel.domain.CancelOrderInfo;
import cancel.domain.ChangeOrderInfo;
import cancel.domain.ChangeOrderResult;
import cancel.domain.DrawBackInfo;
import cancel.domain.GetAccountByIdInfo;
import cancel.domain.GetAccountByIdResult;
import cancel.domain.GetOrderByIdInfo;
import cancel.domain.GetOrderResult;
import cancel.domain.NotifyInfo;
import cancel.domain.Order;
import cancel.domain.OrderStatus;
import cancel.service.CancelService;
import cancel.service.CancelServiceImpl;

@Service
public class CancelActivitiesImpl implements CancelActivities {
    @Autowired
    private AsyncTask asyncTask = new AsyncTask();
    
    @Autowired
    private CancelService cancelService = new CancelServiceImpl();
    
	@Override
	public boolean sendEmail(NotifyInfo notifyInfo) {
		return cancelService.sendEmail(notifyInfo);
	}
	@Override
    public CalculateRefundResult calculateRefund(CancelOrderInfo info) {
		return cancelService.calculateRefund(info);
	}
	@Override
    public String calculateRefundFromOrder(Order order) {
		return cancelService.calculateRefund(order);
	}
	@Override
    public ChangeOrderResult cancelFromOrder(ChangeOrderInfo info) {
		return cancelService.cancelFromOrder(info);
	}
	@Override
    public ChangeOrderResult cancelFromOtherOrder(ChangeOrderInfo info) {
		return cancelService.cancelFromOtherOrder(info);
	}
	@Override
    public boolean drawbackMoney(String money,String userId) {
		return cancelService.drawbackMoney(money, userId);
	}
	@Override
    public GetAccountByIdResult getAccount(GetAccountByIdInfo info) {
		return cancelService.getAccount(info);
	}
	@Override
    public GetOrderResult getOrderByIdFromOrder(GetOrderByIdInfo info) {
		return cancelService.getOrderByIdFromOrder(info);
	}
	@Override
    public GetOrderResult getOrderByIdFromOrderOther(GetOrderByIdInfo info) {
		return cancelService.getOrderByIdFromOrderOther(info);
	}
	@Override
    public ChangeOrderResult updateOtherOrderStatusToCancelAsync(ChangeOrderInfo info) throws InterruptedException {
		return cancelService.updateOtherOrderStatusToCancelAsync(info);
	}
	@Override
    public Boolean drawBackMoneyForOrderCancelAsync(String money, String userId,String orderId, String loginToken) throws InterruptedException {
		return cancelService.drawBackMoneyForOrderCancelAsync(money, userId, orderId, loginToken);
	}
	
}
