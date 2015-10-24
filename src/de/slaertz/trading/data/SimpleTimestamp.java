package de.slaertz.trading.data;

public class SimpleTimestamp {

	private final SimpleDate date;

	public int hour;

	public int minute;

	public int second;		

	public SimpleTimestamp(final SimpleDate date, int hour, int minute,
			int second) {
		this.date = date;
		if (hour < 0 || hour > 23)
			throw new IllegalArgumentException("Hour not in range 0-23 " + hour);
		this.hour = hour;
		if (minute < 0 || minute > 59)
			throw new IllegalArgumentException("Minute not in range 0-59 "
					+ minute);
		this.minute = minute;
		if (second < 0 || second > 59)
			throw new IllegalArgumentException("Second not in range 0-59 "
					+ second);
		this.second = second;
	}	

	/**
	 * Compares two Dates for ordering.
	 * 
	 * @param anotherDate
	 *            the <code>Date</code> to be compared.
	 * @return the value <code>0</code> if the argument Date is equal to this
	 *         Date; a value less than <code>0</code> if this Date is before
	 *         the Date argument; and a value greater than <code>0</code> if
	 *         this Date is after the Date argument.
	 * 
	 * @exception NullPointerException
	 *                if <code>anotherDate</code> is null.
	 */
	public int compareTo(final SimpleTimestamp anotherTimestamp) {
		int dateComp = this.date.compareTo(anotherTimestamp.getDate());
		if (dateComp == 0) {
			// check hour in same day, month and year
			if (hour < anotherTimestamp.hour)
				return -1;
			else if (hour > anotherTimestamp.hour)
				return 1;
			// check minute in same hour, day, month and year
			if (minute < anotherTimestamp.minute)
				return -1;
			else if (minute > anotherTimestamp.minute)
				return 1;
			// check second in same minute, hour, day, month and year
			if (second < anotherTimestamp.second)
				return -1;
			else if (second > anotherTimestamp.second)
				return 1;
			return 0;
		} else
			return dateComp;
	}
	
	public void addMinutes(final int minutes) {
		int hours = minutes / 60;
		int min = minutes % 60;
		if(this.minute + min < 0) {
			hours--;
			min = 60 + min;
		}			
		this.hour += hours;		
		this.minute += min;
		
	}

	@Override
	public String toString() {
		String dateString = date.toString();
		StringBuilder tmp = new StringBuilder(dateString.length() + 9);
		tmp.append(dateString);
		tmp.append(' ');
		if (hour < 10)
			tmp.append('0');
		tmp.append(hour);
		tmp.append(':');
		if (minute < 10)
			tmp.append('0');
		tmp.append(minute);
		tmp.append(':');
		if (second < 10)
			tmp.append('0');
		tmp.append(second);
		return tmp.toString();
	}

	@Override
	public int hashCode() {
		return (this.date.hashCode() << 17) | (hour << 12) | (minute << 6)
				| second;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof SimpleTimestamp)
			return this.hashCode() == ((SimpleTimestamp) o).hashCode();
		return false;
	}

	public SimpleDate getDate() {
		return date;
	}
}
