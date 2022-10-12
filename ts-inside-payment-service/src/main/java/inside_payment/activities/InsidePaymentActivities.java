package inside_payment.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import inside_payment.domain.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ActivityInterface
public interface InsidePaymentActivities {

    @ActivityMethod    
    boolean pay(PaymentInfo info, HttpServletRequest request);
    @ActivityMethod  
    boolean createAccount(CreateAccountInfo info);
    @ActivityMethod   
    boolean addMoney(AddMoneyInfo info);
    @ActivityMethod   
    List<Payment> queryPayment();
    @ActivityMethod   
    List<Balance> queryAccount();
    @ActivityMethod   
    boolean drawBack(DrawBackInfo info);
    @ActivityMethod   
    boolean payDifference(PaymentDifferenceInfo info, HttpServletRequest request);
    @ActivityMethod   
    List<AddMoney> queryAddMoney();
    @ActivityMethod   
    void initPayment(Payment payment);
}