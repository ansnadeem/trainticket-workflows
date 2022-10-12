package inside_payment.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import inside_payment.domain.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@WorkflowInterface
public interface InsidePaymentWorkflow {

    @WorkflowMethod    
    boolean pay(PaymentInfo info, HttpServletRequest request);
    @WorkflowMethod  
    boolean createAccount(CreateAccountInfo info);
    @WorkflowMethod   
    boolean addMoney(AddMoneyInfo info);
    @WorkflowMethod   
    List<Payment> queryPayment();
    @WorkflowMethod   
    List<Balance> queryAccount();
    @WorkflowMethod   
    boolean drawBack(DrawBackInfo info);
    @WorkflowMethod   
    boolean payDifference(PaymentDifferenceInfo info, HttpServletRequest request);
    @WorkflowMethod   
    List<AddMoney> queryAddMoney();
    @WorkflowMethod   
    void initPayment(Payment payment);
}