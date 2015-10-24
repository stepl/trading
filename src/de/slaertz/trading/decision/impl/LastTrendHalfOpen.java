package de.slaertz.trading.decision.impl;

import de.slaertz.trading.Account;
import de.slaertz.trading.Position;
import de.slaertz.trading.data.SimpleTimestamp;
import de.slaertz.trading.data.Tick;
import de.slaertz.trading.data.TicksWindow;
import de.slaertz.trading.decision.OpenPositionDecision;

public class LastTrendHalfOpen implements OpenPositionDecision {

	/** number of minutes to look back */
	private final int minutes;

	public LastTrendHalfOpen(final int minutes) {
		this.minutes = minutes;
	}

	public Position doOpen(TicksWindow ticks, Account account,
			SimpleTimestamp now) {
		// determine direction of last minutes
		float price = ticks.getTick(ticks.size() - 1).getValue();
		// find tick 5 minutes ago
		SimpleTimestamp minAgo = new SimpleTimestamp(now.getDate(), now.hour,
				now.minute, now.second);
		minAgo.addMinutes(-minutes);
		float priceAgo = price;
		for (int i = ticks.size() - 1; i >= 0; i--) {
			Tick tick = ticks.getTick(i);
			if (tick.getTimestamp().compareTo(minAgo) <= 0 || i == 0) {
				priceAgo = tick.getValue();
				break;
			}
		}
		int number = (int) Math
				.floor((double) ((account.balance / 2) / (price / ((float) account.lever))));
		int actual = account.buy(price, number);
		if (actual > 0)
			return new Position(now, priceAgo > price, actual, price);
		else
			return null;
	}

}
