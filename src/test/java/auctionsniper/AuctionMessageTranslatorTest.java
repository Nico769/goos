package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;


class AuctionMessageTranslatorTest {

    public static final Chat UNUSED_CHAT = null;
    @RegisterExtension
    public final Mockery context = new JUnit5Mockery() {{
        // Allow JMock to create mock objects of classes.
        // By default, JMock can only mock interfaces.
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");
        translator.processMessage(UNUSED_CHAT, message);
    }

}