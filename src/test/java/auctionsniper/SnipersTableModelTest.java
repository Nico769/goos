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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void setsSniperValuesInColumns() {
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(aRowChangedEvent()));
        }});

        model.sniperStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATE, Main.STATUS_BIDDING);
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    /**
     * Builds a Matcher that reflectively compares the property values of any {@link TableModelEvent} it receives
     * against an expected template. We need this since {@link TableModelEvent} does not implement an equals() method
     *
     * @return the Matcher that compares property values via reflection
     */
    private Matcher<TableModelEvent> aRowChangedEvent() {
        return SamePropertyValuesAs.samePropertyValuesAs(new TableModelEvent(model, 0));
    }
}