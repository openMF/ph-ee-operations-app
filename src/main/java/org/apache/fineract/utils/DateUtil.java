package org.apache.fineract.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String getUTCFormat(String dateTime, String interfaceTimezone) {
        // Interface time zone reference : https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY:MM:DD HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
        ZoneId zoneId = ZoneId.of(interfaceTimezone);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        logger.info("Date Inside: {}",zonedDateTime);
        return String.valueOf(zonedDateTime);
    }
}
