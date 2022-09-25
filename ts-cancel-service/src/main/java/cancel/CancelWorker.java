package cancel;

import cancel.activities.CancelActivitiesImpl;
import cancel.workflow.CancelWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

// @@@SNIPSTART money-transfer-project-template-java-worker
public class CancelWorker {

    public static void main(String[] args) {

        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        // Worker factory is used to create Workers that poll specific Task Queues.
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("default");
        // This Worker hosts both Workflow and Activity implementations.
        // Workflows are stateful so a type is needed to create instances.
        worker.registerWorkflowImplementationTypes(CancelWorkflowImpl.class);
        // Activities are stateless and thread safe so a shared instance is used.
        worker.registerActivitiesImplementations(new CancelActivitiesImpl());
        
        // Start listening to the Task Queue.
        factory.start();
    }
}
// @@@SNIPEND
