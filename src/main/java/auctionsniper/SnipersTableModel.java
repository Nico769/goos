package auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {

    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private SniperSnapshot snapshot = STARTING_UP;
    private static final String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Won", "Lost"};

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // The book suggests that a default case is needed but, since Java 14,
        // this is redundant when the switch statement deals with Enums,
        // because the compiler implicitly provides the default case for you.
        // See here for more info: https://openjdk.java.net/jeps/361
        return switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER -> snapshot.itemId;
            case LAST_PRICE -> snapshot.lastPrice;
            case LAST_BID -> snapshot.lastBid;
            case SNIPER_STATE -> textFor(snapshot.state);
        };
    }

    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        snapshot = newSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }
}
