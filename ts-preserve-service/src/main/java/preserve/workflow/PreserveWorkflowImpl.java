package preserve.workflow;


import java.time.Duration;
import java.util.UUID;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import preserve.activities.PreserveActivity;
import preserve.domain.OrderTicketsInfo;
import preserve.domain.OrderTicketsInfoPlus;
import preserve.domain.OrderTicketsResult;
import preserve.domain.PreserveNode;
import preserve.domain.StatusBean;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;

public class PreserveWorkflowImpl implements PreserveWorkflow {

    private StatusBean statusBean = new StatusBean();

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
    private final PreserveActivity activities = Workflow.newActivityStub(PreserveActivity.class, options);
    @Override
    public OrderTicketsResult Preserve(OrderTicketsInfoPlus otiPlus) {

        OrderTicketsInfo oti = otiPlus.getInfo();

        String loginId = otiPlus.getLoginId();

        String loginToken = otiPlus.getLoginToken();


        System.out.println("[Preserve Service][Preserve] Account " + loginId + " order from " +
                oti.getFrom() + " -----> " + oti.getTo() + " at " + oti.getDate());

        //add this request to the queue of requests
        UUID uuid = UUID.randomUUID();
        PreserveNode pn = new PreserveNode(uuid, loginId);
        statusBean.chartMsgs.add(pn);

        Promise<OrderTicketsResult> otr = Async.function(activities::preserve, oti, loginId, loginToken);
        //wait the task done
        while(true) {
            if(otr.isCompleted()) {
                System.out.println("------preserveService uuid = " + uuid  +  " done--------");
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        OrderTicketsResult result = otr.get();
        int index = statusBean.chartMsgs.indexOf(pn);
        //some error happened that beyond image
        if(index < 0){
            statusBean.chartMsgs.remove(pn);
            System.out.println("-----cannot find the current preserve node.------");
            try {
                throw new Exception("cannot find the current preserve node.");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //check if there exists any request from the same loginId that haven't return
            for(int i = 0; i < index; i++){
                if(statusBean.chartMsgs.get(i).getLoginId().equals(loginId)){
                    statusBean.chartMsgs.remove(pn);
                    System.out.println("-----This OrderTicketsResult return before the last loginId request.------");
                    result.setStatus(false);
                    result.setMessage("ErrorReportUI");
                    return result;
                    //throw new Exception("This OrderTicketsResult return before the last loginId request.");
                }
            }
        }

        System.out.println("-----This OrderTicketsResult return in the sequence.------");
        statusBean.chartMsgs.remove(pn);

        return result;
    }
	
	
}