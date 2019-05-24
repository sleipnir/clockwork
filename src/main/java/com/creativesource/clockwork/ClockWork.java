package com.creativesource.clockwork;

import com.creativesource.clockwork.ntp.NTPClock;
import com.creativesource.clockwork.ntp.impl.SimpleNTPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import java.io.Serializable;
import java.time.*;

public final class ClockWork extends Clock implements Serializable {
    public static final ClockWork UTC = new ClockWork(ZoneOffset.UTC);

    private final ZoneId zone;
    private final NTPClock client;

    private ClockWork(ZoneId zone) {
        this.zone = zone;
        this.client = new SimpleNTPClient();
    }

    /**
     * Gets the time-zone being used to create dates and times.
     * <p>
     * A clock will typically obtain the current instant and then convert that
     * to a date or time using a time-zone. This method returns the time-zone used.
     *
     * @return the time-zone being used to interpret instants, not null
     */
    @Override
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Returns a copy of this clock with a different time-zone.
     * <p>
     * A clock will typically obtain the current instant and then convert that
     * to a date or time using a time-zone. This method returns a clock with
     * similar properties but using a different time-zone.
     *
     * @param zone the time-zone to change to, not null
     * @return a clock based on this clock with the specified time-zone, not null
     */
    @Override
    public Clock withZone(ZoneId zone) {
        return new ClockWork(zone);
    }

    /**
     * Gets the current instant of the clock.
     * <p>
     * This returns an instant representing the current instant as defined by the clock.
     *
     * @return the current instant from this clock, not null
     * @throws DateTimeException if the instant cannot be obtained, not thrown by most implementations
     */
    @Override
    public Instant instant() {
        TimeInfo info = client.currentTimeInfo();
        return TimeStamp.getNtpTime(info.getReturnTime())
                .getDate().toInstant();
    }
}
