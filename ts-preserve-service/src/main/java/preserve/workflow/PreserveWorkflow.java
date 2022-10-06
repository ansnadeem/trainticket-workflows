package preserve.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import preserve.domain.OrderTicketsInfo;
import preserve.domain.OrderTicketsInfoPlus;
import preserve.domain.OrderTicketsResult;

@WorkflowInterface
public interface PreserveWorkflow {
    @WorkflowMethod
    OrderTicketsResult Preserve(OrderTicketsInfoPlus otiPlus);
}