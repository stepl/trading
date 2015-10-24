package de.slaertz.trading.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SCMagicTickerDataImporter extends TickerDataImporter {

	private static final long AVERAGE_ENTRY_SIZE = 23; // bytes

	private final String symbol;

	public SCMagicTickerDataImporter(String dataFile, String symbol) {
		this(dataFile, symbol, null, null);
	}

	public SCMagicTickerDataImporter(String dataFile, String symbol,
			SimpleDate startDate, SimpleDate endDate) {
		super(dataFile, startDate, endDate);
		this.symbol = symbol;
	}

	public static void main(String[] args) {
		SimpleDate startDate = new SimpleDate(2007, 1, 1);
		SimpleDate endDate = new SimpleDate(2007, 1, 11);
		TickerDataImporter importer = new SCMagicTickerDataImporter(
				"D:\\Code\\Trading\\resources\\FDAX_20050104_2007103.txt",
				"FDAX"
		/* ,startDate, endDate */);
		TickerData data;
		try {
			data = importer.read();
			// System.out.println(data.getSymbol());
			// Iterator<Tick> ticks = data.getAllTicks().iterator();
			// int size = 0;
			// while (ticks.hasNext()) {
			// Tick tick = ticks.next();
			// if (size < 20)
			// System.out.println(tick);
			// // ticks.next();
			// size++;
			// }
			// System.out.println(size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public TickerData read() throws IOException {
		if (data == null) {
			File file = new File(this.dataFile);
			BufferedReader in = new BufferedReader(new FileReader(file));
			// first line are the headers
			String line = in.readLine();
			// set up ticker data
			data = new TickerData(this.symbol, guessNumberOfEntries(file
					.length()));
			boolean isFirst = true;
			SimpleTimestamp lastDate = null;
			float previousTickValue = Float.MIN_VALUE;
			while ((line = in.readLine()) != null) {
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
			}
			in.close();
			if (lastDate != null)
				data.setEndDate(lastDate);
		}
		return data;
	}

	private static SimpleTimestamp getTimestamp(final String line) {
		int month = optimizedParseInt(line, 0, 2);
		int day = optimizedParseInt(line, 2, 4);
		int year = optimizedParseInt(line, 4, 8);
		int hour = optimizedParseInt(line, 9, 11);
		int minute = optimizedParseInt(line, 11, 13);
		int second = optimizedParseInt(line, 13, 15);
		return new SimpleTimestamp(new SimpleDate(year, month, day), hour,
				minute, second);
	}

	private static float getValue(final String line) {
		return optimizedParseFloat(line, 16, line.length());
	}

	protected int guessNumberOfEntries(final long fileSize) {
		return (int) (fileSize / AVERAGE_ENTRY_SIZE);
	}
}
