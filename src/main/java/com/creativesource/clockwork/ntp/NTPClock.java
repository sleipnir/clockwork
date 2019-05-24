package com.creativesource.clockwork.ntp;

import org.apache.commons.net.ntp.TimeInfo;

import java.time.LocalDateTime;

public interface NTPClock {
    LocalDateTime currentUTCTime() throws IllegalStateException;

    TimeInfo currentTimeInfo();
}
