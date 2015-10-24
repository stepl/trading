package de.slaertz.trading.data;

import java.util.List;

public class TicksWindow {

	final List<Tick> ticks;

	final DataPartition partition;

	public TicksWindow(final List<Tick> ticks, final DataPartition partition) {
		this.ticks = ticks;
		this.partition = partition;
	}

	public Tick getTick(int index) {
		if (partition.start + index > partition.end)
			return null;
		return this.ticks.get(partition.start + index);
	}			
	
	public int size() {
		return partition.end-partition.start+1;
	}	
}
