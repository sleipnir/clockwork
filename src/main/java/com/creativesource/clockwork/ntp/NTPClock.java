package com.creativesource.clockwork.ntp;

import java.time.LocalDateTime;

public interface NTPClock {
    LocalDateTime currentUTCTime() throws IllegalStateException;
}
