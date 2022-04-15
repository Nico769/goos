package auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static auctionsniper.AuctionEventListener.PriceSource;

class AuctionSniperTest {

    @RegisterExtension
    public final Mockery context = new JUnit5Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final String ITEM_ID = "";
    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction, sniperListener);
    private final States sniperState = context.states("sniper");

    @Test
    void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding(with(any(SniperSnapshot.class)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperLost();
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        sniper.auctionClosed();
    }

    @Test
    void reportsWinningIfBidIsInFavorOfSniperWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding(with(any(SniperSnapshot.class)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperWinning();
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
    }

    @Test
    void reportsBiddingIfBidIsNotInFavorOfSniperWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperWinning();
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperBidding(with(any(SniperSnapshot.class)));
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperWinning();
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperWon();
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
        sniper.auctionClosed();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {{
            oneOf(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperBidding(new SniperSnapshot(ITEM_ID, price, bid));
        }});

        sniper.currentPrice(price, increment, PriceSource.FROM_OTHER_BIDDER);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperWinning();
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
    }
}