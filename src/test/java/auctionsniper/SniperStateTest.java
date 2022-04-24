package auctionsniper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SniperStateTest {

    @Test
    void givenJoiningStateWhenAuctionClosedThenSniperHasLost() {
        SniperState joiningState = SniperState.JOINING;

        SniperState actualState = joiningState.whenAuctionClosed();

        assertThat(actualState, equalTo(SniperState.LOST));
    }

    @Test
    void givenBiddingStateWhenAuctionClosedThenSniperHasLost() {
        SniperState biddingState = SniperState.BIDDING;

        SniperState actualState = biddingState.whenAuctionClosed();

        assertThat(actualState, equalTo(SniperState.LOST));
    }

    @Test
    void givenWinningStateWhenAuctionClosedThenSniperHasWon() {
        SniperState winningState = SniperState.WINNING;

        SniperState actualState = winningState.whenAuctionClosed();

        assertThat(actualState, equalTo(SniperState.WON));
    }

    @Test
    void givenWonStateWhenAuctionClosedThenThrowsDefect() {
        SniperState wonState = SniperState.WON;

        Defect e = assertThrows(Defect.class, wonState::whenAuctionClosed);

        assertEquals("Auction is already closed", e.getMessage());
    }

    @Test
    void givenLostStateWhenAuctionClosedThenThrowsDefect() {
        SniperState lostState = SniperState.LOST;

        Defect e = assertThrows(Defect.class, lostState::whenAuctionClosed);

        assertEquals("Auction is already closed", e.getMessage());
    }
}