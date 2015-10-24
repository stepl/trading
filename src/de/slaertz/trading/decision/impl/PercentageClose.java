package de.slaertz.trading.decision.impl;

import de.slaertz.trading.Account;
import de.slaertz.trading.Position;
import de.slaertz.trading.data.Tick;
import de.slaertz.trading.data.TicksWindow;
import de.slaertz.trading.decision.ClosePostionDecision;

/**
 * This decision closes a position if either the passed percentage of profit was
 * reached or the account balance goes below the account close out level or if
 * the end of the trading day was reached.
 */
public class PercentageClose implements ClosePostionDecision {

	private final float percent;

	public PercentageClose(final float percent) {
		this.percent = percent;
	}

	public boolean doClose(Position position, TicksWindow ticks, Account account) {
		// check each tick
		if (ticks != null) {
			for (int i = 0; i < ticks.size(); i++) {
				Tick tick = ticks.getTick(i);
				float price = tick.getValue();
				// check if this is a valid tick
				// invalid ticks a huge peaks which are usually caused by
				// errors in the tick data and couldn't be traded for real
				boolean isValid = true;
				if (i + 1 < ticks.size()) {
					Tick nextTick = ticks.getTick(i + 1);
					if (Math.abs(nextTick.getValue() - price) > 2.0) {						
						isValid = false;
					}
				}
				if (isValid) {
					// check if we reached the profit
					float profit = position.getProfit(account, price);
					float positionCosts = account.getCosts(position.price,
							position.number);
					if (profit >= (positionCosts * percent)) {						
						account.closePosition(position, price);
						return true;
					} else {
						// check if close out was reached
						float closeOutValue = (account.balance + positionCosts)
								* account.closeOut;
						if (account.balance + positionCosts + profit <= closeOutValue) {
							// close out
							account.balance = closeOutValue;							
							return true;
						}
					}
				}
			}			
			// if we reached this point
			// the end of the trading day was reached
			account.closePosition(position, ticks.getTick(ticks.size() - 1)
					.getValue());						
		}
		return true;
	}
}
