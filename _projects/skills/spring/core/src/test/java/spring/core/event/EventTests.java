package spring.core.event;

import com.hansanhha.spring.event.event.*;
import com.hansanhha.spring.event.pulisher.StockMarket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@RecordApplicationEvents
public class EventTests {

    @Autowired
    private StockMarket stockMarket;

    @Autowired
    private ApplicationEvents events;

    @Test
    void StockMarketShouldLaunchMarketEvent() throws Exception {
        stockMarket.launchMarket();

        assertThat(events
                .stream(LaunchMarketEvent.class)
                .count())
                .isEqualTo(1);
    }

    @Test
    void StockMarketShouldCloseMarketEvent() throws Exception {
        stockMarket.closeMarket();

        assertThat(events
                .stream(CloseMarketEvent.class)
                .count())
                .isEqualTo(1);
    }

    @Test
    void StockMarketShouldSuspendMarketEvent() throws Exception {
        stockMarket.circuitBreaker();

        assertThat(events
                .stream(SuspendMarketEvent.class)
                .count())
                .isEqualTo(1);
    }

    @Test
    void transactionalEvent() throws Exception {
        stockMarket.registerStocks();

        assertThat(events
                .stream(RegisterStockEvent.class)
                .count())
                .isEqualTo(1);
    }
}
