package de.slaertz.trading.data;

public class SimpleDate {

	protected final int year;

	protected final int month;

	protected final int day;

	public SimpleDate(int year, int month, int day) {
		if (year < 1900)
			throw new IllegalArgumentException("Year must be at least 1900 "
					+ year);
		this.year = year;
		if (month < 1 || month > 12)
			throw new IllegalArgumentException("Month not in range 1-12 "
					+ month);
		this.month = month;
		if (day < 1 || day > 31)
			throw new IllegalArgumentException("Day not in range 1-31 " + day);
		this.day = day;
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
	public int compareTo(SimpleDate anotherDate) {
		// check year first
		if (year < anotherDate.year)
			return -1;
		else if (year > anotherDate.year)
			return 1;
		// check month in same year
		if (month < anotherDate.month)
			return -1;
		else if (month > anotherDate.month)
			return 1;
		// check day in same month and year
		if (day < anotherDate.day)
			return -1;
		else if (day > anotherDate.day)
			return 1;
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder tmp = new StringBuilder(10);
		tmp.append(year);
		tmp.append('/');
		if (month < 10)
			tmp.append('0');
		tmp.append(month);
		tmp.append('/');
		if (day < 10)
			tmp.append('0');
		tmp.append(day);
		return tmp.toString();
	}

	@Override
	public int hashCode() {		
		return (year << 9) | (month << 5) | day;
	}

	@Override
	public boolean equals(Object o) {		
		if(o == null) return false;
		if(o instanceof SimpleDate) 
			return this.hashCode() == ((SimpleDate)o).hashCode(); 
		return false;
	}			
}
