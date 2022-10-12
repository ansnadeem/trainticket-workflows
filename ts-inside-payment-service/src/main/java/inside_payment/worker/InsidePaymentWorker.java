package inside_payment.worker;

import inside_payment.activities.InsidePaymentActivities;
import inside_payment.activities.InsidePaymentActivitiesImpl;
import inside_payment.workflow.InsidePaymentWorkflow;
import inside_payment.workflow.InsidePaymentWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class InsidePaymentWorker {

    public static void main(String[] args) {

        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        // Worker factory is used to create Workers t;hat poll specific Task Queues.
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("default");
        // This Worker hosts both Workflow and Activity implementations.
        // Workflows are stateful so a type is needed to create instances.
        worker.registerWorkflowImplementationTypes(InsidePaymentWorkflow.class);
        // Activities are stateless and thread safe so a shared instance is used.
        worker.registerActivitiesImplementations(new InsidePaymentActivitiesImpl());

        // Start listening to the Task Queue.
        factory.start();
    }
}