package org.apache.fineract.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${interface.timezone}")
    public String interfaceTimezone;

    public String getUTCFormat(String dateTime) {
        logger.info("Inside GET UTC Method 1");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logger.info("Inside GET UTC Method 2");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        logger.info("Inside GET UTC Method 3");
        ZoneId interfaceZone = ZoneId.of(interfaceTimezone);
        logger.info("Inside GET UTC Method 4");
        ZonedDateTime interfaceDateTime = ZonedDateTime.of(localDateTime, interfaceZone);
        logger.info("Inside GET UTC Method 5");
        ZonedDateTime gmtDateTime = interfaceDateTime.withZoneSameInstant(ZoneId.of("GMT"));
        logger.info("Inside GET UTC Method 6");
        DateTimeFormatter gmtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logger.info("Inside GET UTC Method 7");
        String gmtDateTimeString = gmtDateTime.format(gmtFormatter);
        logger.info("Inside GET UTC Method 8");
        logger.info("New Date time : {}",gmtDateTimeString);
        return gmtDateTimeString;
    }
}
