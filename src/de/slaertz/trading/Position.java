package de.slaertz.trading;

import de.slaertz.trading.data.SimpleTimestamp;

/**
 * A trading position
 */
public class Position {

	public final SimpleTimestamp timestamp;

	public final boolean isShort;

	public final int number;

	public final float price;	

	public Position(final SimpleTimestamp timestamp, final boolean isShort,
			final int number, final float price) {
		this.timestamp = timestamp;		
		this.isShort = isShort;
		this.number = number;
		this.price = price;
	}

	public float getProfit(final Account account, final float price) {
		float closePrice = this.isShort ? price + account.spread : price
				- account.spread;
		float profit = ((float) this.number) * (closePrice - this.price);
		if (this.isShort)
			profit = -profit;
		return profit;
	}
}
