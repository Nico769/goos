package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WON = "Won";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Name";
    public static final String SNIPERS_TABLE_NAME = "Snipers Table";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    public static final String JOIN_COMMAND_FORMAT = "";
    public static final String BID_COMMAND_FORMAT = "";

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;
    private MainWindow ui;
    @SuppressWarnings("unused")
    private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        disconnectWhenUICloses(connection);
        final Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
        this.notToBeGCd = chat;
        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(new AuctionMessageTranslator(connection.getUser(), new AuctionSniper(itemId, auction,
                new SniperStateDisplayer())));
        auction.join();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    public class SniperStateDisplayer implements SniperListener {
        @Override
        public void sniperBidding(SniperSnapshot sniperSnapshot) {
            SwingUtilities.invokeLater(() -> ui.sniperStatusChanged(sniperSnapshot, Main.STATUS_BIDDING));
        }

        @Override
        public void sniperLost() {
            showStatus(STATUS_LOST);
        }

        @Override
        public void sniperWinning() {
            showStatus(STATUS_WINNING);
        }

        @Override
        public void sniperWon() {
            showStatus(STATUS_WON);
        }

        private void showStatus(final String status) {
            SwingUtilities.invokeLater(() -> ui.showStatus(status));
        }
    }
}
