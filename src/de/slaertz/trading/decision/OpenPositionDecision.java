package de.slaertz.trading.decision;

import de.slaertz.trading.Account;
import de.slaertz.trading.Position;
import de.slaertz.trading.data.SimpleTimestamp;
import de.slaertz.trading.data.TicksWindow;

public interface OpenPositionDecision {

	/**
	 * Open or trading position
	 * 
	 * @param ticks
	 *            The available tick data
	 * @param account
	 *            The account used to open the position
	 * @return a new trading position or <code>null</code> if you cannot open
	 *         a new position
	 */
	Position doOpen(final TicksWindow ticks, final Account account, final SimpleTimestamp now);
}
