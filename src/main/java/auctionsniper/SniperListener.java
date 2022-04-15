package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperBidding(SniperSnapshot sniperSnapshot);

    void sniperLost();

    void sniperWinning();

    void sniperWon();
}
