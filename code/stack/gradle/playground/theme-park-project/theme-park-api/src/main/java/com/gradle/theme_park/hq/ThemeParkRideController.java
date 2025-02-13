package com.gradle.theme_park.hq;

//import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gradle.theme_park.status.RideStatusService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Iterator;

//@RestController
public class ThemeParkRideController {

//    @GetMapping("/rides")
    public Iterator<ThemeParkRide> getRides() {
//        ObjectNode rollercoaster = RideStatusService.getRideStatus("rollercoaster");
//        System.out.println(RideStatusService.getRideStatus("rollercoaster"));
        return Arrays.asList(
//                new ThemeParkRide("rollercoaster",
//                        rollercoaster.get("status").asText()),
                new ThemeParkRide("Rollercoaster",
                        "Train ride that speeds you along."),
                new ThemeParkRide("Log flume",
                        "Boat ride with plenty of splashes."),
                new ThemeParkRide("Teacups",
                        "Spinning ride in a giant tea-cup.")
        ).iterator();
    }
}
