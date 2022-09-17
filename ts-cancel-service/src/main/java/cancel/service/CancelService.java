package cancel.service;

import cancel.domain.CalculateRefundResult;
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

public interface CancelService {

    CancelOrderResult cancelOrder(CancelOrderInfo info,String loginToken,String loginId) throws Exception;

	public boolean sendEmail(NotifyInfo notifyInfo);

    public CalculateRefundResult calculateRefund(CancelOrderInfo info);

    public String calculateRefund(Order order);

    public ChangeOrderResult cancelFromOrder(ChangeOrderInfo info);

    public ChangeOrderResult cancelFromOtherOrder(ChangeOrderInfo info);

    public boolean drawbackMoney(String money,String userId);

    public GetAccountByIdResult getAccount(GetAccountByIdInfo info);

    public GetOrderResult getOrderByIdFromOrder(GetOrderByIdInfo info);

    public GetOrderResult getOrderByIdFromOrderOther(GetOrderByIdInfo info);
    
    public ChangeOrderResult updateOtherOrderStatusToCancelAsync(ChangeOrderInfo info) throws InterruptedException;
    
    public Boolean drawBackMoneyForOrderCancelAsync(String money, String userId,String orderId, String loginToken) throws InterruptedException;
	
}
