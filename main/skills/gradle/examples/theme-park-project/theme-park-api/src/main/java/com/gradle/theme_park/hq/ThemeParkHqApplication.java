package com.gradle.theme_park.hq;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gradle.theme_park.status.RideStatusService;

//@SpringBootApplication
public class ThemeParkHqApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(ThemeParkHqApplication.class, args);
//    }

    public static void main(String[] args) {
        System.out.println(RideStatusService.getRideStatus("rollercoaster"));
    }
}
