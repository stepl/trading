package de.slaertz.trading.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TickerData {

	private final String symbol;

	private SimpleTimestamp startDate;

	private SimpleTimestamp endDate;

	private List<Tick> ticks;

	private final Map<SimpleDate, DataPartition> dayIndex;

	private SimpleTimestamp lastTimeStamp;

	public TickerData(final String symbol, final int approxSize) {
		this.symbol = symbol;
		this.ticks = new ArrayList<Tick>(approxSize);
		this.dayIndex = new LinkedHashMap<SimpleDate, DataPartition>(
				approxSize / 31000);
	}

	// some testing
	public static void main(String[] args) {
		TickerData data = new TickerData("FDAX", 5);
		SimpleDate today = new SimpleDate(2007, 10, 7);
		for (int i = 0; i < 5; i++)
			data.addTick(new Tick(new SimpleTimestamp(today, 9, i * 10, 0),
					i + 0.5f));
		TicksWindow ticksToday = data.getAllTicksForDay(today);
		TicksWindow prev = data.getPrevTicks(ticksToday, new SimpleTimestamp(
				today, 9, 35, 0));
		System.out.println("Before 9:35");
		for (int i = 0; i < prev.size(); i++)
			System.out.println(prev.getTick(i));
		TicksWindow after = data.getFutureTicks(ticksToday,
				new SimpleTimestamp(today, 9, 35, 0));
		System.out.println("After 9:35");
		for (int i = 0; i < after.size(); i++)
			System.out.println(after.getTick(i));
	}

	public SimpleTimestamp getEndDate() {
		return endDate;
	}

	public SimpleTimestamp getStartDate() {
		return startDate;
	}

	public String getSymbol() {
		return symbol;
	}

	public List<Tick> getAllTicks() {
		return ticks;
	}

	public boolean addTick(final Tick tick) {
		// update day index
		SimpleTimestamp timestamp = tick.getTimestamp();
		if ((lastTimeStamp == null) || timestamp != null
				&& (timestamp.getDate().compareTo(lastTimeStamp.getDate()) > 0)) {
			// end of previous index entry
			int end = this.ticks.size() - 1;
			if (lastTimeStamp != null) {
				DataPartition lastPart = this.dayIndex.get(lastTimeStamp
						.getDate());
				if (lastPart != null)
					lastPart.end = end;
			}
			// new start
			this.dayIndex.put(timestamp.getDate(), new DataPartition(end + 1,
					this.ticks.size()));
		} else {
			// tick belongs to same day
			DataPartition lastPart = this.dayIndex.get(lastTimeStamp.getDate());
			lastPart.end = this.ticks.size();
		}
		this.lastTimeStamp = timestamp;
		// add tick
		return this.ticks.add(tick);
	}

	public void setEndDate(SimpleTimestamp endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(SimpleTimestamp startDate) {
		this.startDate = startDate;
	}

	public Set<SimpleDate> getTradingDays() {
		return this.dayIndex.keySet();
	}

	public TicksWindow getAllTicksForDay(final SimpleDate date) {
		DataPartition part = this.dayIndex.get(date);
		if (part != null) {
			return new TicksWindow(this.ticks, part);
		}
		return null;
	}

	public TicksWindow getFutureTicks(final TicksWindow window,
			final SimpleTimestamp now) {
		DataPartition part = window.partition;
		int start = part.start;
		for (int i = 0; i < window.size(); i++) {
			Tick tick = window.getTick(i);
			if (tick.getTimestamp().compareTo(now) < 0)
				start++;
			else
				break;
		}
		if (start > part.end)
			return null;
		else
			return new TicksWindow(window.ticks, new DataPartition(start,
					part.end));
	}

	public TicksWindow getPrevTicks(final TicksWindow window,
			final SimpleTimestamp now) {
		DataPartition part = window.partition;
		int end = part.end;
		for (int i = window.size() - 1; i >= 0; i--) {
			Tick tick = window.getTick(i);
			if (tick.getTimestamp().compareTo(now) > 0)
				end--;
			else
				break;
		}
		if (end < part.start)
			return null;
		else
			return new TicksWindow(window.ticks, new DataPartition(part.start,
					end));
	}
}
