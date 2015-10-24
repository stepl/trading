package de.slaertz.trading.data;

import java.io.IOException;

public abstract class TickerDataImporter {

	protected final String dataFile;

	protected final SimpleDate startDate;

	protected final SimpleDate endDate;
	
	protected TickerData data;

	public TickerDataImporter(final String dataFile,
			final SimpleDate startDate, final SimpleDate endDate) {
		this.dataFile = dataFile;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	protected static int optimizedParseInt(final String s, int start, int end) {
		if (s == null)
			throw new NumberFormatException("null");
		int result = 0;
		boolean negative = false;
		int i = start, max = end;
		int limit;
		int multmin;
		int digit;
		if (max - start > 0) {
			if (s.charAt(start) == '-') {
				negative = true;
				limit = Integer.MIN_VALUE;
				i++;
			} else
				limit = -Integer.MAX_VALUE;
			multmin = limit / 10;
			if (i < max) {
				digit = Character.digit(s.charAt(i++), 10);
				if (digit < 0)
					throw new NumberFormatException(s.substring(start, end));
				else
					result = -digit;
			}
			while (i < max) {
				// Accumulating negatively avoids surprises near MAX_VALUE
				digit = Character.digit(s.charAt(i++), 10);
				if (digit < 0)
					throw new NumberFormatException(s.substring(start, end));
				if (result < multmin)
					throw new NumberFormatException(s.substring(start, end));
				result *= 10;
				if (result < limit + digit)
					throw new NumberFormatException(s.substring(start, end));
				result -= digit;
			}
		} else
			throw new NumberFormatException(s.substring(start, end));
		if (negative) {
			if (i > 1)
				return result;
			else
				/* Only got "-" */
				throw new NumberFormatException(s.substring(start, end));
		} else
			return -result;
	}

	public abstract TickerData read() throws IOException;

	protected static float optimizedParseFloat(final String s, int start,
			int end) {
		if (s == null)
			throw new NumberFormatException("null");
		int comma = s.indexOf('.', start);
		if (comma < 0)
			return optimizedParseInt(s, start, end);
		float result = optimizedParseInt(s, start, comma);
		boolean negative = false;
		if (negative = (result < 0))
			result = -result;
		comma++;
		// check for preceding zeros
		int numberOfZeros = 0;
		for (int i = comma; i < end; i++) {
			if (s.charAt(i) == '0')
				numberOfZeros++;
			else
				break;
		}
		comma += numberOfZeros;
		if (end - comma > 0) {
			double afterComma = optimizedParseInt(s, comma, end);
			while (afterComma >= 1)
				afterComma /= 10d;
			for (int i = 0; i < numberOfZeros; i++)
				afterComma /= 10d;
			result = result + (float) afterComma;
		}
		if (negative)
			result = -result;
		return result;
	}
	
	protected abstract int guessNumberOfEntries(final long fileSize);
}