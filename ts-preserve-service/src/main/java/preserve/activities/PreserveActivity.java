package preserve.activities;

import io.temporal.activity.ActivityInterface;

import org.springframework.scheduling.annotation.Async;
import preserve.domain.OrderTicketsInfo;
import preserve.domain.OrderTicketsResult;

@ActivityInterface
public interface PreserveActivity {
	OrderTicketsResult preserve(OrderTicketsInfo oti, String accountId, String loginToken);
}