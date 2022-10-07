package fdse.microservice.activities;

import fdse.microservice.domain.PriceConfig;
import fdse.microservice.domain.QueryStation;
import fdse.microservice.domain.Route;
import fdse.microservice.domain.TrainType;
import fdse.microservice.service.BasicService;
import fdse.microservice.service.BasicServiceImpl;

public class BasicActivitiesImpl {

    BasicService service = new BasicServiceImpl();

    public String queryForStationId(QueryStation info) {
        return service.queryForStationId(info);
    }

    public boolean checkStationExists(String stationName) {
        return service.checkStationExists(stationName);
    }

    public TrainType queryTrainType(String trainTypeId) {
        return service.queryTrainType(trainTypeId);
    }

    public Route getRouteByRouteId(String routeId) {
        return getRouteByRouteId(routeId);
    }

    public PriceConfig queryPriceConfigByRouteIdAndTrainType(String routeId,String trainType) {
        return queryPriceConfigByRouteIdAndTrainType(routeId,trainType);
    }
}
