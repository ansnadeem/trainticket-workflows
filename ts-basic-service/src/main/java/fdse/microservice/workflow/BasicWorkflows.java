package fdse.microservice.workflow;

import fdse.microservice.domain.QueryForTravel;
import fdse.microservice.domain.QueryStation;
import fdse.microservice.domain.ResultForTravel;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface BasicWorkflows {
    @WorkflowMethod
    ResultForTravel QueryForTravelWorkflow(QueryForTravel info);

    @WorkflowMethod
    String queryForStationId(QueryStation info);

}
