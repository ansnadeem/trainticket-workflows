package fdse.microservice.service;

import fdse.microservice.domain.*;


public interface BasicService {
    
    String queryForStationId(QueryStation info);

    boolean checkStationExists(String stationName);

    TrainType queryTrainType(String trainTypeId);

    Route getRouteByRouteId(String routeId);

    PriceConfig queryPriceConfigByRouteIdAndTrainType(String routeId,String trainType);

}
