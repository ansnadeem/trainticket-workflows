package preserve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import preserve.domain.*;

import preserve.workflow.PreserveWorkflow;

@RestController
public class PreserveController {

    private String hostname = "134.129.91.178";

    @CrossOrigin(origins = "*")
    @RequestMapping(value="/preserve", method = RequestMethod.POST)
    public OrderTicketsResult preserve(@RequestBody OrderTicketsInfoPlus otiPlus) throws Exception {
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
		PreserveWorkflow workflow = client.newWorkflowStub(PreserveWorkflow.class, options);
		return workflow.Preserve(otiPlus);
    }


}
