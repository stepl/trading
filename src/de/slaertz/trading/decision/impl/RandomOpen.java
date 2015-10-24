package de.slaertz.trading.decision.impl;

import java.util.Random;

import de.slaertz.trading.Account;
import de.slaertz.trading.Position;
import de.slaertz.trading.data.SimpleTimestamp;
import de.slaertz.trading.data.TicksWindow;
import de.slaertz.trading.decision.OpenPositionDecision;

public class RandomOpen implements OpenPositionDecision {
	
	private static Random random = new Random();

	public Position doOpen(TicksWindow ticks, Account account,
			SimpleTimestamp now) {
		// determine direction of last minutes
		float price = ticks.getTick(ticks.size() - 1).getValue();
		int number = account.buyWithAll(price);		
		if (number > 0) 			
			return new Position(now,random.nextBoolean() , number, price);
		else
			return null;
	}

}
