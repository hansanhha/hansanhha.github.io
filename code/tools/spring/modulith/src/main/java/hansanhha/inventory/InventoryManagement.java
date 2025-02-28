package hansanhha.inventory;

import hansanhha.order.events.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class InventoryManagement {

    private final InventoryInternal inventoryInternal;

    @ApplicationModuleListener
    void on(OrderCompletedEvent event) throws InterruptedException {
        var orderId = event.orderId();

        log.info("Received order completion for {}.", orderId);

        // 비즈니스 로직
        Thread.sleep(1000);

        log.info("finished order completion for {}.", orderId);
    }
}
