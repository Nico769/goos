package auctionsniper;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static auctionsniper.AuctionEventListener.PriceSource;
import static org.hamcrest.Matchers.equalTo;

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
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        sniper.auctionClosed();
    }

    @Test
    void reportsWinningIfBidIsInFavorOfSniperWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
    }

    @Test
    void reportsBiddingIfBidIsNotInFavorOfSniperWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
        sniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON)));
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
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid,
                    SniperState.BIDDING));
        }});

        sniper.currentPrice(price, increment, PriceSource.FROM_OTHER_BIDDER);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135,
                    SniperState.WINNING));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(135, 45, PriceSource.FROM_SNIPER);
    }

    private Matcher<SniperSnapshot> aSniperThatIs(SniperState state) {
        return new FeatureMatcher<>(equalTo(state), "sniper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}