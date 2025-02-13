package hansanhha.classes.enums;

import java.time.LocalDateTime;

public enum DeliveryStatusManager implements StatusManager<DeliveryStatusManager> {

    PREPARING("준비 중") {
        @Override
        public DeliveryStatusManager nextStatus() {
            var now = LocalDateTime.now();
            PREPARING.finish.set(now);
            READY.start.set(now);
            return READY;
        }
    },
    READY("준비됨") {
        @Override
        public DeliveryStatusManager nextStatus() {
            var now = LocalDateTime.now();
            READY.finish.set(now);
            DELIVERING.start.set(now);
            return DELIVERING;
        }
    },

    DELIVERING("배송 중") {
        @Override
        public DeliveryStatusManager nextStatus() {
            var now = LocalDateTime.now();
            DELIVERING.finish.set(now);
            DELIVERED.start.set(now);
            DELIVERED.finish.set(now);
            return DELIVERED;
        }
    },

    DELIVERED("배송됨") {
        @Override
        public DeliveryStatusManager nextStatus() {
            throw new IllegalCallerException();
        }
    };

    private final String value;
    private final ThreadLocal<LocalDateTime> start = new ThreadLocal<>();
    private final ThreadLocal<LocalDateTime> finish = new ThreadLocal<>();
    private static final String PREFIX = "delivery: ";

    public String getStatus() {
        return PREFIX;
    }

    DeliveryStatusManager(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getStart() {
        return start.get();
    }

    public LocalDateTime getFinish() {
        return finish.get();
    }

    public void set() {
        if (this.equals(PREPARING)) {
            this.start.set(LocalDateTime.now());
        }
    }
}
