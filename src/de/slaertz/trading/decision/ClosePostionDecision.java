package de.slaertz.trading.decision;

import de.slaertz.trading.Account;
import de.slaertz.trading.Position;
import de.slaertz.trading.data.TicksWindow;

public interface ClosePostionDecision {

	boolean doClose(final Position position, final TicksWindow ticks,
			final Account account);
}
