package fdse.microservice.activities;

import fdse.microservice.domain.PriceConfig;
import fdse.microservice.domain.QueryStation;
import fdse.microservice.domain.Route;
import fdse.microservice.domain.TrainType;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface BasicActivities {

    @ActivityMethod
    String queryForStationId(QueryStation info);

    @ActivityMethod
    boolean checkStationExists(String stationName);

    @ActivityMethod
    TrainType queryTrainType(String trainTypeId);

    @ActivityMethod
    Route getRouteByRouteId(String routeId);
    
    @ActivityMethod
    PriceConfig queryPriceConfigByRouteIdAndTrainType(String routeId,String trainType);
}
