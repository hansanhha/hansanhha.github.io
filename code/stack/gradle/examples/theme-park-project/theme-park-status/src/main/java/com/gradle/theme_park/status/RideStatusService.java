package com.gradle.theme_park.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RideStatusService {

    public static ObjectNode getRideStatus(String ride) {
        List<String> rideStatuses = readFile(String.format("%s.txt",
                StringUtils.trim(ride)));
        String rideStatus = rideStatuses.get(new Random().nextInt(rideStatuses.size()));
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("status", rideStatus);
        return node;
    }

//       public static void main(String[] args) {
//        System.out.println(System.getProperty("java.version"));
//
//        if (args.length != 1) {
//            System.out.println("A single ride name must be passed");
//            System.exit(1);
//        }
//
//        var rideName = args[0];
//        var rideStatus = getRideStatus(rideName);
//
//        System.out.printf("Current status of %s is '%s'%n", rideName, rideStatus);
//    }

//    public static String getRideStatus(String ride) {
//        var rideStatuses = readFile(String.format("%s.txt", StringUtils.trim(ride)));
//        return rideStatuses.get(new Random().nextInt(rideStatuses.size()));
//    }

    private static List<String> readFile(String filename) {
        var resourceStream = RideStatusService.class.getClassLoader()
                .getResourceAsStream(filename);

        if (resourceStream == null) {
            throw new IllegalArgumentException("Ride not found");
        }

        List<String> result = new ArrayList<>();
        try (var bufferedInputStream = new BufferedReader(
                new InputStreamReader(resourceStream, StandardCharsets.UTF_8))) {
            while (bufferedInputStream.ready()) {
                result.add(bufferedInputStream.readLine());
            }
        } catch (IOException exception) {
            throw new RuntimeException("Couldn't read file", exception);
        }
        return result;
    }

}
