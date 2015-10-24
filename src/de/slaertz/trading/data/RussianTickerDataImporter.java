package de.slaertz.trading.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RussianTickerDataImporter extends TickerDataImporter {

	private static final long AVERAGE_ENTRY_SIZE = 37;

	public static final int HOURS_ADJUSTMENT = -2;

	public static final int MINUTES_ADJUSTMENT = 0;

	public static final int SECONDS_ADJUSTMENT = -5;

	public static void main(String[] args) {
		SimpleDate startDate = new SimpleDate(2007, 1, 1);
		SimpleDate endDate = new SimpleDate(2007, 1, 11);
		TickerDataImporter importer = new RussianTickerDataImporter(
				"D:\\Code\\Trading\\resources\\DAX_text.txt"
		/* ,startDate, endDate */);
		TickerData data;
		try {
			data = importer.read();
			System.out.println(data.getSymbol());
			// Iterator<Entry<SimpleDate, Partition>> tradingDays = data
			// .getDayIndex().entrySet().iterator();
			// while (tradingDays.hasNext()) {
			// Entry<SimpleDate, Partition> entry = tradingDays.next();
			// Partition part = entry.getValue();
			// }
			// Iterator<Tick> ticks = data.getAllTicks().iterator();
			// int size = 0;
			// while (ticks.hasNext()) {
			// System.out.println(ticks.next());
			// //ticks.next();
			// size++;
			// }
			// System.out.println(size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RussianTickerDataImporter(final String dataFile) {
		this(dataFile, null, null);
	}

	public RussianTickerDataImporter(final String dataFile,
			final SimpleDate startDate, final SimpleDate endDate) {
		super(dataFile, startDate, endDate);
	}

	public TickerData read() throws IOException {
		if (data == null) {
			File file = new File(this.dataFile);
			BufferedReader in = new BufferedReader(new FileReader(file));
			// first line are the headers
			in.readLine();
			// first data line
			String line = in.readLine();
			// get ticker symbol
			String symbol = line.substring(0, line.indexOf(','));
			// set up ticker data
			data = new TickerData(symbol, guessNumberOfEntries(file.length()));
			boolean isFirst = true;
			SimpleTimestamp lastDate = null;
			float previousTickValue = Float.MIN_VALUE;
			while (line != null) {
				SimpleTimestamp timestamp = getTimestamp(line);
				boolean afterStart = false;
				if (this.startDate != null)
					afterStart = timestamp.getDate().compareTo(this.startDate) >= 0;
				else
					afterStart = true;
				boolean beforeEnd = false;
				if (this.endDate != null)
					beforeEnd = timestamp.getDate().compareTo(this.endDate) <= 0;
				else
					beforeEnd = true;
				if (afterStart && beforeEnd) {
					if (isFirst) {
						data.setStartDate(timestamp);
						isFirst = false;
					}
					float tickValue = getValue(line);
					// check if the value change and eliminate duplicate entries
					// but never cut out the first tick of the day
					if ((tickValue != previousTickValue)
							|| (lastDate != null && timestamp.getDate()
									.compareTo(lastDate.getDate()) > 0))
						data.addTick(new Tick(timestamp, tickValue));
					previousTickValue = tickValue;
					lastDate = timestamp;
				}
				line = in.readLine();
			}
			in.close();
			if (lastDate != null)
				data.setEndDate(lastDate);
		}
		return data;
	}

	private static SimpleTimestamp getTimestamp(final String line) {
		int start = line.indexOf(',');
		if (start <= 0)
			throw new IllegalArgumentException("No valid ticker data found :"
					+ line);
		start++;
		start = line.indexOf(',', start);
		start++;
		int year = optimizedParseInt(line, start, start + 4);
		int month = optimizedParseInt(line, start += 4, start + 2);
		int day = optimizedParseInt(line, start += 2, start + 2);
		start += 3;
		int hour = optimizedParseInt(line, start, start + 2) + HOURS_ADJUSTMENT;
		if (hour < 0) {
			hour = 24 + hour;
			day--;
		} else if (hour > 23) {
			hour = hour - 24;
			day++;
		}
		int minute = optimizedParseInt(line, start += 2, start + 2)
				+ MINUTES_ADJUSTMENT;
		if (minute < 0) {
			minute = 60 + minute;
			hour--;
			if (hour < 0) {
				hour = 24 + hour;
				day--;
			}
		} else if (minute > 59) {
			minute = minute - 60;
			hour++;
			if (hour > 23) {
				hour = hour - 24;
				day++;
			}
		}
		int second = optimizedParseInt(line, start += 2, start + 2)
				+ SECONDS_ADJUSTMENT;
		if (second < 0) {
			second = 60 + second;
			minute--;
			if (minute < 0) {
				minute = 60 + minute;
				hour--;
				if (hour < 0) {
					hour = 24 + hour;
					day--;
				}
			}
		} else if (second > 59) {
			second = second - 610;
			minute++;
			if (minute > 59) {
				minute = minute - 60;
				hour++;
				if (hour > 23) {
					hour = hour - 24;
					day++;
				}
			}
		}
		return new SimpleTimestamp(new SimpleDate(year, month, day), hour,
				minute, second);
	}

	private static float getValue(final String line) {
		int end = line.lastIndexOf(',');
		int start = line.indexOf(',', line.lastIndexOf('.') - 7);
		return optimizedParseFloat(line, start + 1, end);
	}

	protected int guessNumberOfEntries(final long fileSize) {
		return (int) (fileSize / AVERAGE_ENTRY_SIZE);
	}
}
