package inside_payment.activities;

import inside_payment.domain.*;
import inside_payment.service.InsidePaymentService;
import inside_payment.service.InsidePaymentServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class InsidePaymentActivitiesImpl implements InsidePaymentActivities {

    InsidePaymentService service = new InsidePaymentServiceImpl();

    public boolean pay(PaymentInfo info, HttpServletRequest request) {
        return service.pay(info, request);
    }
    public boolean createAccount(CreateAccountInfo info) {
        return service.createAccount(info);
    }
    public boolean addMoney(AddMoneyInfo info) {
        return service.addMoney(info);
    }
    public List<Payment> queryPayment() {
        return service.queryPayment();
    }
    public List<Balance> queryAccount() {
        return service.queryAccount();
    }
    public boolean drawBack(DrawBackInfo info) {
        return service.drawBack(info);
    }
    public boolean payDifference(PaymentDifferenceInfo info, HttpServletRequest request) {
        return service.payDifference(info, request);
    }
    public List<AddMoney> queryAddMoney() {
        return queryAddMoney();
    }
    public void initPayment(Payment payment) {
        initPayment(payment);
    }
}