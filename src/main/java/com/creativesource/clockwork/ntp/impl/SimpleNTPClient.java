package com.creativesource.clockwork.ntp.impl;


import com.creativesource.clockwork.ntp.NTPClock;
import lombok.Getter;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@Log
public class SimpleNTPClient implements NTPClock {
    private static final int SALT = 2;
    private static final int TIMEOUT = 70;
    private static final List<String> DEFAULT_NTP_POOL = Arrays.asList(
            "ntp.usp.br",
            "0.pool.ntp.org",
            "a.st1.ntp.br",
            "1.pool.ntp.org",
            "b.st1.ntp.br",
            "2.pool.ntp.org",
            "c.st1.ntp.br",
            "3.pool.ntp.org",
            "d.st1.ntp.br",
            "a.ntp.br",
            "b.ntp.br",
            "c.ntp.br",
            "gps.ntp.br",
            "br.pool.ntp.org",
            "ntp.puc-rio.br",
            "ntp.cais.rnp.br",
            "ntp.on.br",
            "ntp2.on.br");

    @Getter private final NTPUDPClient client;
    @Getter private final List<String> ntpPoolList;
    @Getter private final int timeout;

    public SimpleNTPClient(){
        this(new NTPUDPClient(), DEFAULT_NTP_POOL, TIMEOUT);
    }

    public SimpleNTPClient(NTPUDPClient client, List<String> ntpPoolList, int timeout){
        this.client = client;
        this.ntpPoolList = ntpPoolList;
        this.timeout = timeout;
        client.setDefaultTimeout(timeout);
    }

    @Override
    public LocalDateTime currentUTCTime() throws IllegalStateException {

        for (int index = 0; index < ntpPoolList.size(); index++) {
            String pool = ntpPoolList.get(index);
            try {
                InetAddress inetAddress = InetAddress.getByName(pool);
                TimeInfo timeInfo = client.getTime(inetAddress);

                long returnTime = timeInfo.getReturnTime();
                //log.trace("[SimpleNTPClient]: TimeInfo -> {}", timeInfo.getMessage());

                TimeStamp destNtpTime = TimeStamp.getNtpTime(returnTime);
                //log.debug("[SimpleNTPClient]: Pool [{}]. System time: {} {}", pool, destNtpTime, destNtpTime.toDateString());

                TimeStamp currentNtpTime = TimeStamp.getCurrentTime();
                //log.debug("[SimpleNTPClient]: Pool [{}]. Atomic time: {} {}", pool, currentNtpTime, currentNtpTime.toDateString());

                return currentNtpTime.getDate().toInstant()
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDateTime();
            } catch (Exception e) {
                //log.warn("[SimpleNTPClient]: NTP Server [{}] not alive or error during query. Cause: {}", pool, e.getMessage());
                int swapPos;
                if((index + SALT) <= ntpPoolList.size() -1){
                    swapPos = index + SALT;
                } else {
                    swapPos = ntpPoolList.size() -1;
                }
                Collections.swap(ntpPoolList, index, swapPos);
            }

        }

        throw new IllegalStateException("Pool of NTP Servers not return any 'Time' for query 'current time'. Probably all servers is down ");

    }

    public TimeInfo currentTimeInfo() throws IllegalStateException {

        for (int index = 0; index < ntpPoolList.size(); index++) {
            String pool = ntpPoolList.get(index);
            try {
                InetAddress inetAddress = InetAddress.getByName(pool);
                TimeInfo timeInfo = client.getTime(inetAddress);
                return timeInfo;
            } catch (Exception e) {
                //log.warn("[SimpleNTPClient]: NTP Server [{}] not alive or error during query. Cause: {}", pool, e.getMessage());
                int swapPos;
                if((index + SALT) <= ntpPoolList.size() -1){
                    swapPos = index + SALT;
                } else {
                    swapPos = ntpPoolList.size() -1;
                }
                Collections.swap(ntpPoolList, index, swapPos);
            }

        }

        throw new IllegalStateException("Pool of NTP Servers not return any 'Time' for query 'current time'. Probably all servers is down ");

    }

}
