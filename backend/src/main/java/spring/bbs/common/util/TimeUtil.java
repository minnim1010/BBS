package spring.bbs.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtil {
    private TimeUtil() {
    }

    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime, ZoneId zoneId) {
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        return Date.from(instant);
    }
}
