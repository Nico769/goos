package auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {

    private static final SniperState STARTING_UP = new SniperState("", 0, 0);
    private SniperState sniperState = STARTING_UP;
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
            case ITEM_IDENTIFIER -> sniperState.itemId;
            case LAST_PRICE -> sniperState.lastPrice;
            case LAST_BID -> sniperState.lastBid;
            case SNIPER_STATUS -> statusText;
        };
    }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperState newSniperState, String newStatusText) {
        sniperState = newSniperState;
        setStatusText(newStatusText);
    }
}
