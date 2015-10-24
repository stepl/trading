package de.slaertz.trading.data;

public class DataPartition {

	public int start;

	public int end;

	public DataPartition(final int start) {
		this(start, 0);
	}

	public DataPartition(final int start, final int end) {
		this.start = start;
		this.end = end;
	}
}
