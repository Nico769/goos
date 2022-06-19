package auctionsniper;

import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static auctionsniper.SnipersTableModel.textFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SnipersTableModelTest {
    @RegisterExtension
    public final Mockery context = new JUnit5Mockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @BeforeEach
    void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    void setsSniperValuesInColumns() {
        SniperSnapshot joining = SniperSnapshot.joining("item id");
        SniperSnapshot bidding = joining.bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            oneOf(listener).tableChanged(with(aRowChangedEvent(0)));
        }});

        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    void notifiesListenersWhenAddingASniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
    }

    @Test
    void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test
    void updatesCorrectRowForSniper() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        SniperSnapshot notToUpdate = SniperSnapshot.joining("item 0");
        SniperSnapshot toUpdate = SniperSnapshot.joining("item 1");
        model.addSniper(notToUpdate);
        model.addSniper(toUpdate);

        SniperSnapshot expectedSnapshot = toUpdate.bidding(555, 666);
        model.sniperStateChanged(expectedSnapshot);

        assertRowMatchesSnapshot(1, expectedSnapshot);
    }

    @Test
    void throwsDefectIfNoExistingSniperForAnUpdate() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        SniperSnapshot existingSnapshot = SniperSnapshot.joining("item 0");
        model.addSniper(existingSnapshot);
        SniperSnapshot notExistingSnapshot = SniperSnapshot.joining("not exist");

        Defect e = assertThrows(Defect.class, () -> {
            model.sniperStateChanged(notExistingSnapshot);
        });

        assertEquals("Cannot find match for " + notExistingSnapshot, e.getMessage());
    }

    private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot expectedSnapshot) {
        assertEquals(expectedSnapshot.itemId, cellValue(rowIndex, Column.ITEM_IDENTIFIER));
        assertEquals(expectedSnapshot.lastPrice, cellValue(rowIndex, Column.LAST_PRICE));
        assertEquals(expectedSnapshot.lastBid, cellValue(rowIndex, Column.LAST_BID));
        assertEquals(textFor(expectedSnapshot.state), cellValue(rowIndex, Column.SNIPER_STATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
        return SamePropertyValuesAs.samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     * Builds a Matcher that reflectively compares the property values of any {@link TableModelEvent} it receives
     * against an expected template. We need this since {@link TableModelEvent} does not implement an equals() method
     *
     * @param indexRow the index of the row where the change occurred
     * @return the Matcher that compares property values via reflection
     */
    private Matcher<TableModelEvent> aRowChangedEvent(int indexRow) {
        return SamePropertyValuesAs.samePropertyValuesAs(new TableModelEvent(model, indexRow));
    }
}