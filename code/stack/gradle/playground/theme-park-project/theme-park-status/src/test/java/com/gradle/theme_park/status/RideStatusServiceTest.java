package com.gradle.theme_park.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RideStatusServiceTest {

    @ParameterizedTest(name = "{index} gets {0} ride status")
    @ValueSource(strings = {"rollercoaster", "logflume", "teacups"})
    void getRideStatus(String ride) {
        var rideStatusService = new RideStatusService();
        var rideStatus = rideStatusService.getRideStatus(ride);
        assertNotNull(rideStatus);
    }
}
