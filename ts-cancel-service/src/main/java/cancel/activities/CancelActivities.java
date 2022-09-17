package cancel.activities;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

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
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CancelActivities {
	
	public boolean sendEmail(NotifyInfo notifyInfo);

    public CalculateRefundResult calculateRefund(CancelOrderInfo info);

    public String calculateRefundFromOrder(Order order);

    public ChangeOrderResult cancelFromOrder(ChangeOrderInfo info);

    public ChangeOrderResult cancelFromOtherOrder(ChangeOrderInfo info);

    public boolean drawbackMoney(String money,String userId);

    public GetAccountByIdResult getAccount(GetAccountByIdInfo info);

    public GetOrderResult getOrderByIdFromOrder(GetOrderByIdInfo info);

    public GetOrderResult getOrderByIdFromOrderOther(GetOrderByIdInfo info);
    
    public ChangeOrderResult updateOtherOrderStatusToCancelAsync(ChangeOrderInfo info) throws InterruptedException;

    public Boolean drawBackMoneyForOrderCancelAsync(String money, String userId,String orderId, String loginToken) throws InterruptedException;
}
