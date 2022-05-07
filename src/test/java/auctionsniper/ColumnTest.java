package auctionsniper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ColumnTest {

    @Test
    void getExpectedItemIdentifierAndPriceDetailsValuesFromColumnLabels() {
        SniperSnapshot snapshot = new SniperSnapshot("item id", 555, 666, SniperState.JOINING);

        Object actualItemId = Column.ITEM_IDENTIFIER.valueIn(snapshot);
        Object actualLastPrice = Column.LAST_PRICE.valueIn(snapshot);
        Object actualLastBid = Column.LAST_BID.valueIn(snapshot);

        assertThat(actualItemId, equalTo("item id"));
        assertThat(actualLastPrice, equalTo(555));
        assertThat(actualLastBid, equalTo(666));
    }

    @Test
    void getJoiningStateValueFromSniperStateColumnLabel() {
        SniperSnapshot snapshot = new SniperSnapshot("irrelevant", -1, -1, SniperState.JOINING);

        Object actualState = Column.SNIPER_STATE.valueIn(snapshot);

        assertThat(actualState, equalTo("Joining"));
    }

    @Test
    void getBiddingStateValueFromSniperStateColumnLabel() {
        SniperSnapshot snapshot = new SniperSnapshot("irrelevant", -1, -1, SniperState.BIDDING);

        Object actualState = Column.SNIPER_STATE.valueIn(snapshot);

        assertThat(actualState, equalTo("Bidding"));
    }

    @Test
    void getWinningStateValueFromSniperStateColumnLabel() {
        SniperSnapshot snapshot = new SniperSnapshot("irrelevant", -1, -1, SniperState.WINNING);

        Object actualState = Column.SNIPER_STATE.valueIn(snapshot);

        assertThat(actualState, equalTo("Winning"));
    }


    @Test
    void getLostStateValueFromSniperStateColumnLabel() {
        SniperSnapshot snapshot = new SniperSnapshot("irrelevant", -1, -1, SniperState.LOST);

        Object actualState = Column.SNIPER_STATE.valueIn(snapshot);

        assertThat(actualState, equalTo("Lost"));
    }

    @Test
    void getWonStateValueFromSniperStateColumnLabel() {
        SniperSnapshot snapshot = new SniperSnapshot("irrelevant", -1, -1, SniperState.WON);

        Object actualState = Column.SNIPER_STATE.valueIn(snapshot);

        assertThat(actualState, equalTo("Won"));
    }
}