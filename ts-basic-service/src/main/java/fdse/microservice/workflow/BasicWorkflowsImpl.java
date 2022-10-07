package fdse.microservice.workflow;

import fdse.microservice.activities.BasicActivities;
import fdse.microservice.domain.PriceConfig;
import fdse.microservice.domain.QueryForTravel;
import fdse.microservice.domain.QueryStation;
import fdse.microservice.domain.ResultForTravel;
import fdse.microservice.domain.Route;
import fdse.microservice.domain.TrainType;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.HashMap;

public class BasicWorkflowsImpl implements BasicWorkflows {

    private final RetryOptions retryoptions = RetryOptions.newBuilder()
        .setInitialInterval(Duration.ofSeconds(1))
        .setMaximumInterval(Duration.ofSeconds(100))
        .setBackoffCoefficient(2)
        .setMaximumAttempts(500)
        .build();
    private final ActivityOptions options = ActivityOptions.newBuilder()
        // Timeout options specify when to automatically timeout Activities if the process is taking too long.
        .setStartToCloseTimeout(Duration.ofSeconds(10))
        // Optionally provide customized RetryOptions.
        // Temporal retries failures by default, this is simply an example.
        .setRetryOptions(retryoptions)
        .build();
    // ActivityStubs enable calls to methods as if the Activity object is local, but actually perform an RPC.
    private final BasicActivities activities = Workflow.newActivityStub(BasicActivities.class, options);

    @Override
    public ResultForTravel QueryForTravelWorkflow(QueryForTravel info) {

        ResultForTravel result = new ResultForTravel();
        result.setStatus(true);
//        boolean startingPlaceExist = restTemplate.postForObject(
//                "http://ts-station-service:12345/station/exist", new QueryStation(info.getStartingPlace()), Boolean.class);
//        boolean endPlaceExist = restTemplate.postForObject(
//                "http://ts-station-service:12345/station/exist", new QueryStation(info.getEndPlace()),  Boolean.class);
        boolean startingPlaceExist = activities.checkStationExists(info.getStartingPlace());
        boolean endPlaceExist = activities.checkStationExists(info.getEndPlace());
        if(!startingPlaceExist || !endPlaceExist){
            result.setStatus(false);
        }

//        String startingPlaceId = restTemplate.postForObject(
//                "http://ts-station-service:12345/station/queryForId", new QueryStation(info.getStartingPlace()), String.class);
//        String endPlaceId = restTemplate.postForObject(
//                "http://ts-station-service:12345/station/queryForId", new QueryStation(info.getEndPlace()),  String.class);



//        String proportion = restTemplate.postForObject("http://ts-config-service:15679/config/query",
//                new QueryConfig("DirectTicketAllocationProportion"), String.class
//        );
//        double percent = 1.0;
//        if(proportion.contains("%")) {
//            proportion = proportion.replaceAll("%", "");
//            percent = Double.valueOf(proportion)/100;
//            result.setPercent(percent);
//        }else{
//            result.setStatus(false);
//        }


//        TrainType trainType = restTemplate.postForObject(
//                "http://ts-train-service:14567/train/retrieve", new QueryTrainType(info.getTrip().getTrainTypeId()), TrainType.class
//        );
        TrainType trainType = activities.queryTrainType(info.getTrip().getTrainTypeId());
        if(trainType == null){
            System.out.println("traintype doesn't exist");
            result.setStatus(false);
        }else{
            result.setTrainType(trainType);
        }

//        QueryPriceInfo queryPriceInfo = new QueryPriceInfo();
//        queryPriceInfo.setStartingPlaceId(startingPlaceId);
//        queryPriceInfo.setEndPlaceId(endPlaceId);
//        queryPriceInfo.setTrainTypeId(trainType.getId());
//        queryPriceInfo.setSeatClass("economyClass");
//        String priceForEconomyClass = restTemplate.postForObject(
//                "http://ts-price-service:16579/price/query",queryPriceInfo , String.class
//        );
//
//        queryPriceInfo.setSeatClass("confortClass");
//        String priceForConfortClass = restTemplate.postForObject(
//                "http://ts-price-service:16579/price/query", queryPriceInfo, String.class
//        );

        String routeId = info.getTrip().getRouteId();
        String trainTypeString = trainType.getId();
        Route route = activities.getRouteByRouteId(routeId);
        PriceConfig priceConfig = activities.queryPriceConfigByRouteIdAndTrainType(routeId,trainTypeString);

        String startingPlaceId = queryForStationId(new QueryStation(info.getStartingPlace()));
        String endPlaceId = queryForStationId(new QueryStation(info.getEndPlace()));
        int indexStart = route.getStations().indexOf(startingPlaceId);
        int indexEnd = route.getStations().indexOf(endPlaceId);

        int distance = route.getDistances().get(indexEnd) - route.getDistances().get(indexStart);

        double priceForEconomyClass = distance * priceConfig.getBasicPriceRate();
        double priceForConfortClass= distance * priceConfig.getFirstClassPriceRate();

        HashMap<String,String> prices = new HashMap<String,String>();
        prices.put("economyClass","" + priceForEconomyClass);
        prices.put("confortClass","" + priceForConfortClass);
        result.setPrices(prices);

        result.setPercent(1.0);

        return result;
    }

    @Override
    public String queryForStationId(QueryStation info) {
        return activities.queryForStationId(info);
    }
    
}
