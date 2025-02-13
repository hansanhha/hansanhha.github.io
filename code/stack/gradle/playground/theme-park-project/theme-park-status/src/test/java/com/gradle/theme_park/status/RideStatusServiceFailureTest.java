package com.gradle.theme_park.status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class RideStatusServiceFailureTest {

    @Test
    void unknownRideCausesFailure() {
        var rideStatusService = new RideStatusService();

        assertThrows(IllegalArgumentException.class, () -> {
            rideStatusService.getRideStatus("dodgems");
        });
    }

//    @Test
    void alwaysFailure() {
        fail();
    }
}
