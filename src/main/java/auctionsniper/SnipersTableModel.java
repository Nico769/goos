package auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {

    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0);
    private SniperSnapshot sniperSnapshot = STARTING_UP;
    private String statusText = Main.STATUS_JOINING;

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
            case ITEM_IDENTIFIER -> sniperSnapshot.itemId;
            case LAST_PRICE -> sniperSnapshot.lastPrice;
            case LAST_BID -> sniperSnapshot.lastBid;
            case SNIPER_STATE -> statusText;
        };
    }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperSnapshot newSniperSnapshot, String newStatusText) {
        sniperSnapshot = newSniperSnapshot;
        setStatusText(newStatusText);
    }
}
