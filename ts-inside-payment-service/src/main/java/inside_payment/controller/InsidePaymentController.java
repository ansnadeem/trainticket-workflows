package inside_payment.controller;

import inside_payment.domain.*;
import inside_payment.service.InsidePaymentService;
import inside_payment.workflow.InsidePaymentWorkflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;


@RestController
public class InsidePaymentController {
    
    private InsidePaymentWorkflow workflow;

    private String hostname = "134.129.91.178";

    public InsidePaymentController() {
        WorkflowServiceStubsOptions stubOptions = WorkflowServiceStubsOptions.newBuilder().setTarget(hostname+":7233")
				.build();
		WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubOptions);
		WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue("default")
				// A WorkflowId prevents this it from having duplicate instances, remove it to
				// duplicate.
				.setWorkflowId("preserve-order-workflow").build();
		// WorkflowClient can be used to start, signal, query, cancel, and terminate
		// Workflows.
		WorkflowClient client = WorkflowClient.newInstance(service);
		// WorkflowStubs enable calls to methods as if the Workflow object is local, but
		// actually perform an RPC.
		workflow = client.newWorkflowStub(InsidePaymentWorkflow.class, options);
    }

    @Autowired
    public InsidePaymentService service;

    @RequestMapping(value="/inside_payment/pay", method = RequestMethod.POST)
    public boolean pay(@RequestBody PaymentInfo info, HttpServletRequest request){
        System.out.println("[Inside Payment Service][Pay] Pay for:" + info.getOrderId());
        return workflow.pay(info, request);
    }

    @RequestMapping(value="/inside_payment/createAccount", method = RequestMethod.POST)
    public boolean createAccount(@RequestBody CreateAccountInfo info){
        return workflow.createAccount(info);
    }

    @RequestMapping(value="/inside_payment/addMoney", method = RequestMethod.POST)
    public boolean addMoney(@RequestBody AddMoneyInfo info){
        return workflow.addMoney(info);
    }

    @RequestMapping(value="/inside_payment/queryPayment", method = RequestMethod.GET)
    public List<Payment> queryPayment(){
        return workflow.queryPayment();
    }

    @RequestMapping(value="/inside_payment/queryAccount", method = RequestMethod.GET)
    public List<Balance> queryAccount(){
        return workflow.queryAccount();
    }

    @RequestMapping(value="/inside_payment/drawBack", method = RequestMethod.POST)
    public boolean drawBack(@RequestBody DrawBackInfo info){
        return workflow.drawBack(info);
    }

    @RequestMapping(value="/inside_payment/payDifference", method = RequestMethod.POST)
    public boolean payDifference(@RequestBody PaymentDifferenceInfo info, HttpServletRequest request){
        return workflow.payDifference(info, request);
    }

    @RequestMapping(value="/inside_payment/queryAddMoney", method = RequestMethod.GET)
    public List<AddMoney> queryAddMoney(){
        return workflow.queryAddMoney();
    }

    @RequestMapping("/hello1_callback")
    public String hello1_callback(@RequestParam(value="result", defaultValue="satan") String cal_back) {

        System.out.println("Call Back Result:" + cal_back);
        System.out.println("-------------external call back-------------");
        return "-------call back end-------";

    }
}
