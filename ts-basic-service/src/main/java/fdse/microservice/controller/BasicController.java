package fdse.microservice.controller;

import fdse.microservice.domain.QueryForStationId;
import fdse.microservice.domain.QueryForTravel;
import fdse.microservice.domain.QueryStation;
import fdse.microservice.domain.ResultForTravel;
import fdse.microservice.service.BasicService;
import fdse.microservice.workflow.BasicWorkflows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;


@RestController
public class BasicController {

    private String hostname = "134.129.91.178";

    @Autowired
    BasicService service;

    @RequestMapping(value="/basic/queryForTravel", method= RequestMethod.POST)
    public ResultForTravel queryForTravel(@RequestBody QueryForTravel info){
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
        BasicWorkflows workflow = client.newWorkflowStub(BasicWorkflows.class, options);
        return workflow.QueryForTravelWorkflow(info);
    }

    @RequestMapping(value="/basic/queryForStationId", method= RequestMethod.POST)
    public String queryForStationId(@RequestBody QueryStation info){
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
        BasicWorkflows workflow = client.newWorkflowStub(BasicWorkflows.class, options);
        return workflow.queryForStationId(info);
    }
}
