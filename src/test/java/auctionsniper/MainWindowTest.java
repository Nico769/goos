package auctionsniper;

import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    void makeUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("an item-id"), "join " +
                "request");
        mainWindow.addUserRequestListener(buttonProbe::setReceivedValue);
        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }

}
