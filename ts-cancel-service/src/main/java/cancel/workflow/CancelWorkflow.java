package cancel.workflow;

import java.util.concurrent.ExecutionException;

import cancel.domain.CancelOrderInfo;
import cancel.domain.CancelOrderResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CancelWorkflow {
	@WorkflowMethod
	CancelOrderResult cancel(CancelOrderInfo info,String loginToken,String loginId) throws InterruptedException, ExecutionException;
}
