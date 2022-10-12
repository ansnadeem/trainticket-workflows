package inside_payment.workflow;

import inside_payment.activities.InsidePaymentActivities;
import inside_payment.domain.*;
import inside_payment.service.InsidePaymentService;
import inside_payment.service.InsidePaymentServiceImpl;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class InsidePaymentWorkflowImpl implements InsidePaymentWorkflow {
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
    private final InsidePaymentActivities activities = Workflow.newActivityStub(InsidePaymentActivities.class, options);

    InsidePaymentService service = new InsidePaymentServiceImpl();

    public boolean pay(PaymentInfo info, HttpServletRequest request) {
        return activities.pay(info, request);
    }
    public boolean createAccount(CreateAccountInfo info) {
        return activities.createAccount(info);
    }
    public boolean addMoney(AddMoneyInfo info) {
        return activities.addMoney(info);
    }
    public List<Payment> queryPayment() {
        return activities.queryPayment();
    }
    public List<Balance> queryAccount() {
        return activities.queryAccount();
    }
    public boolean drawBack(DrawBackInfo info) {
        return activities.drawBack(info);
    }
    public boolean payDifference(PaymentDifferenceInfo info, HttpServletRequest request) {
        return activities.payDifference(info, request);
    }
    public List<AddMoney> queryAddMoney() {
        return activities.queryAddMoney();
    }
    public void initPayment(Payment payment) {
        activities.initPayment(payment);
    }
}