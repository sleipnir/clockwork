package com.creativesource.clockwork.test.docs;

import com.creativesource.clockwork.ClockWork;

import java.time.LocalDateTime;

public class ClockWorkUsage {

    public static void main(String... args){
        ClockWork clock = ClockWork.UTC;
        LocalDateTime dateTime = LocalDateTime.now(clock);
        System.out.println(String.format("UTC Time -> %s", dateTime));
    }
}
