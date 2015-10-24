package de.slaertz.trading;

import de.slaertz.trading.data.SimpleDate;
import de.slaertz.trading.data.SimpleTimestamp;

public class Account {

	public float balance;

	public final int lever;

	public final float closeOut;

	public final float spread;

	public Account(final float balance, final int lever, final float closeOut,
			final float spread) {
		this.balance = balance;
		this.lever = lever;
		this.closeOut = closeOut;
		this.spread = spread;
	}

	public static void main(String[] args) {
		Account acc = new Account(2000f, 40, 0.3f, 1);
		System.out.println("Before :" + acc.balance);
		int number = acc.buyWithAll(8001);
		System.out.println("Buy :" + number);
		System.out.println(acc.balance);
		Position pos = new Position(new SimpleTimestamp(new SimpleDate(2007,
				10, 9), 9, 0, 0), false, number, 8001f);
		acc.closePosition(pos, 8002f);
		System.out.println("Close :" + acc.balance);
	}

	public int buy(final float price, final int number) {
		float costs = getCosts(price, number);
		if (costs <= balance) {
			balance -= costs;
			return number;
		} else
			return 0;
	}

	public int buyWithAll(final float price) {
		int all = (int) Math
				.floor((double) (balance / (price / ((float) this.lever))));
		return buy(price, all);
	}

	public float closePosition(final Position position, final float price) {
		float costs = getCosts(position.price, position.number);
		float profit = position.getProfit(this, price);
		balance = balance + costs + profit;
		return balance;
	}

	public float getCosts(final float price, final int number) {
		return (price / ((float) this.lever)) * ((float) number);
	}

	@Override
	public Account clone() {
		return new Account(this.balance, this.lever, this.closeOut, this.spread);
	}
}
