package fdse.microservice.service;

import fdse.microservice.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BasicServiceImpl implements BasicService{

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String queryForStationId(QueryStation info){
        System.out.println("[Basic Information Service][Query For Station Id] Station Id:" + info.getName());
        String id = restTemplate.postForObject(
                "http://ts-station-service:12345/station/queryForId", info, String.class);
        return id;
    }

    public boolean checkStationExists(String stationName){
        System.out.println("[Basic Information Service][Check Station Exists] Station Name:" + stationName);
        Boolean exist = restTemplate.postForObject(
                "http://ts-station-service:12345/station/exist", new QueryStation(stationName), Boolean.class);
        return exist.booleanValue();
    }

    public TrainType queryTrainType(String trainTypeId){
        System.out.println("[Basic Information Service][Query Train Type] Train Type:" + trainTypeId);
        TrainType trainType = restTemplate.postForObject(
                "http://ts-train-service:14567/train/retrieve", new QueryTrainType(trainTypeId), TrainType.class
        );
        return trainType;
    }

    public Route getRouteByRouteId(String routeId){
        System.out.println("[Basic Information Service][Get Route By Id] Route ID：" + routeId);
        GetRouteByIdResult result = restTemplate.getForObject(
                "http://ts-route-service:11178/route/queryById/" + routeId,
                GetRouteByIdResult.class);
        if(result.isStatus() == false){
            System.out.println("[Basic Information Service][Get Route By Id] Fail." + result.getMessage());
            return null;
        }else{
            System.out.println("[Basic Information Service][Get Route By Id] Success.");
            return result.getRoute();
        }
    }

    public PriceConfig queryPriceConfigByRouteIdAndTrainType(String routeId,String trainType){
        System.out.println("[Basic Information Service][Query For Price Config] RouteId:"
                + routeId + "TrainType:" + trainType);
        QueryPriceConfigByTrainAndRoute info = new QueryPriceConfigByTrainAndRoute();
        info.setRouteId(routeId);
        info.setTrainType(trainType);
        ReturnSinglePriceConfigResult result = restTemplate.postForObject(
                "http://ts-price-service:16579/price/query",
                info,
                ReturnSinglePriceConfigResult.class
        );
        return result.getPriceConfig();
    }

}
