package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperBidding(SniperState sniperState);

    void sniperLost();

    void sniperWinning();

    void sniperWon();
}
