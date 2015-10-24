package de.slaertz.trading.backtesting;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.slaertz.trading.Account;
import de.slaertz.trading.Position;
import de.slaertz.trading.data.SCMagicTickerDataImporter;
import de.slaertz.trading.data.SimpleDate;
import de.slaertz.trading.data.SimpleTimestamp;
import de.slaertz.trading.data.TickerData;
import de.slaertz.trading.data.TickerDataImporter;
import de.slaertz.trading.data.TicksWindow;
import de.slaertz.trading.decision.ClosePostionDecision;
import de.slaertz.trading.decision.OpenPositionDecision;
import de.slaertz.trading.decision.impl.LastTrendHalfOpen;
import de.slaertz.trading.decision.impl.LastTrendOpen;
import de.slaertz.trading.decision.impl.PercentageClose;
import de.slaertz.trading.decision.impl.PointClose;
import de.slaertz.trading.decision.impl.RandomOpen;
import de.slaertz.trading.decision.impl.StartTrendOpen;

public class StrategyTester {

	private final TickerData tickerData;

	private final OpenPositionDecision openDecision;

	private final ClosePostionDecision closeDecision;

	private final Account account;

	private final int slotsPerHour;

	public StrategyTester(final String dataFile, final String symbol,
			final SimpleDate startDate, final SimpleDate endDate,
			final OpenPositionDecision openDecision,
			final ClosePostionDecision closeDecision, final Account account,
			final int slotsPerHour) throws IOException {
		this.openDecision = openDecision;
		this.closeDecision = closeDecision;
		this.account = account;
		this.slotsPerHour = slotsPerHour;
		TickerDataImporter importer = new SCMagicTickerDataImporter(dataFile,
				symbol, startDate, endDate);
		tickerData = importer.read();
	}

	public static void main(String[] args) {
		try {
			StrategyTester tester = new StrategyTester(
					"D:\\Code\\Trading\\resources\\FDAX_20050104_2007103.txt",
					"FDAX", new SimpleDate(2006, 1, 1), null,
					new LastTrendOpen(5) /*new RandomOpen()*/,
					new PercentageClose(0.01f),
					new Account(2000, 40, 0.3f, 1f), 10);
			tester.runTest(System.out);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public void runTest(final PrintStream out) {
		// get all available days of trading
		Iterator<SimpleDate> tradingDays = this.tickerData.getTradingDays()
				.iterator();
		// cut the day in 15 min slots starting from 9.00 - 20.00
		// init all slots
		List<Account> slots = new ArrayList<Account>(11 * slotsPerHour);
		for (int slot = 0; slot < 11 * slotsPerHour; slot++)
			slots.add(this.account.clone());
		while (tradingDays.hasNext()) {
			SimpleDate day = tradingDays.next();
			TicksWindow dayTicks = this.tickerData.getAllTicksForDay(day);
			SimpleTimestamp now = new SimpleTimestamp(day, 9, 0, 0);
			for (int hour = now.hour; hour < 20; hour++) {
				now.hour = hour;
				for (int slot = 0; slot < slotsPerHour; slot++) {
					// for each slot open a position and calculate the outcome
					// for each minute in this slot
					Account slotAccount = slots.get(((hour - 9) * slotsPerHour)
							+ slot);
					int numberOfBuys = 0;
					float initialBalance = slotAccount.balance;
					if (initialBalance > 0) {
						// calculate the average outcome for this slot
						float slotBalance = 0f;
						int slotSize = 60 / slotsPerHour;
						for (int minute = 0; minute < slotSize; minute++) {
							now.minute = (slot * slotSize) + minute;
							slotAccount.balance = initialBalance;
							Position position = this.openDecision
									.doOpen(this.tickerData.getPrevTicks(
											dayTicks, now), slotAccount, now);
							// check if position could be opened at all
							if (position != null) {
								numberOfBuys++;
								// for each position check the outcome
								// add some seconds delay to now
								now.second += 5;
								this.closeDecision.doClose(position,
										this.tickerData.getFutureTicks(
												dayTicks, now), slotAccount);
								// remove delay
								now.second -= 5;
								// update balance
								slotBalance += slotAccount.balance;
							}
						}
						// set average balance for this slot
						if (numberOfBuys > 0) {
							slotAccount.balance = slotBalance
									/ ((float) numberOfBuys);
						}
					}
				}
			}
		}
		// print result
		for (int slot = 0; slot < 11 * slotsPerHour; slot++) {
			Collections.sort(slots, new Comparator<Account>() {
				public int compare(Account o1, Account o2) {
					return (int) Math.floor(o2.balance - o1.balance);
				}
			});
			Account account = slots.get(slot);
			System.out.print("Slot [");
			System.out.print(slot);
			System.out.print("]:");
			System.out.println(account.balance);
		}

	}
}
