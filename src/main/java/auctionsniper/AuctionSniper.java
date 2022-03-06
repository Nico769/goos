package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final SniperListener sniperListener;

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }

    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        if (priceSource == PriceSource.FROM_SNIPER) {
            sniperListener.sniperWinning();
        } else if (priceSource == PriceSource.FROM_OTHER_BIDDER) {
            auction.bid(price + increment);
            sniperListener.sniperBidding();
        }
    }
}
