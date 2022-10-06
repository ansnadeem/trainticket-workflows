package preserve.activities;

import io.temporal.activity.ActivityInterface;

import preserve.domain.OrderTicketsInfo;
import preserve.domain.OrderTicketsResult;
import preserve.service.PreserveService;
import preserve.service.PreserveServiceImpl;


@ActivityInterface
public class PreserveActivityImpl implements PreserveActivity {
	public OrderTicketsResult preserve(OrderTicketsInfo oti, String accountId, String loginToken) {
		PreserveService service = new PreserveServiceImpl();
		return service.preserve(oti, accountId, loginToken);
	}
}