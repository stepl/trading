package de.slaertz.trading.decision.impl;

import de.slaertz.trading.Account;
import de.slaertz.trading.Position;
import de.slaertz.trading.data.SimpleTimestamp;
import de.slaertz.trading.data.TicksWindow;
import de.slaertz.trading.decision.OpenPositionDecision;

public class StartTrendOpen implements OpenPositionDecision {

	public Position doOpen(TicksWindow ticks, Account account, SimpleTimestamp now) 
	{
		// determine direction of last minutes
		float price = ticks.getTick(ticks.size() - 1).getValue();
		float priceAgo = ticks.getTick(0).getValue();
		int number = account.buyWithAll(price);
		if (number > 0)
			return new Position(now, priceAgo > price, number, price);
		else
			return null;
	}
}