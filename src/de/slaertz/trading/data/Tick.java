package de.slaertz.trading.data;

public class Tick {

	private final SimpleTimestamp timestamp;

	private final float value;

	public Tick(final SimpleTimestamp timestamp, final float value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public SimpleTimestamp getTimestamp() {
		return timestamp;
	}

	public float getValue() {
		return value;
	}

	@Override
	public String toString() {
		return timestamp.toString() + "_" + String.valueOf(value);
	}
}
