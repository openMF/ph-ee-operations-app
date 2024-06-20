/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.domain;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.MathUtil;
import org.springframework.jdbc.support.JdbcUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Support for retrieving possibly null values from jdbc recordset delegating to springs {@link JdbcUtils} where
 * possible.
 */
public final class JdbcSupport {

    private JdbcSupport() {}

    public static ZonedDateTime getDateTime(final ResultSet rs, final String columnName) throws SQLException {
        return DateUtils.toTenantZonedDateTime(rs.getTimestamp(columnName));
    }

    public static OffsetDateTime getOffsetDateTime(ResultSet rs, String columnName) throws SQLException {
        return DateUtils.toTenantOffsetDateTime(rs.getTimestamp(columnName), DateUtils.getSystemZoneId());
    }

    public static LocalDate getLocalDate(final ResultSet rs, final String columnName) throws SQLException {
        return DateUtils.toLocalDate(rs.getDate(columnName));
    }

    public static LocalTime getLocalTime(final ResultSet rs, final String columnName) throws SQLException {
        return DateUtils.toLocalTime(rs.getTimestamp(columnName));
    }

    public static Long getLong(final ResultSet rs, final String columnName) throws SQLException {
        return (Long) JdbcUtils.getResultSetValue(rs, rs.findColumn(columnName), Long.class);
    }

    public static Integer getInteger(final ResultSet rs, final String columnName) throws SQLException {
        return (Integer) JdbcUtils.getResultSetValue(rs, rs.findColumn(columnName), Integer.class);
    }

    public static Integer getIntegerDefaultToNullIfZero(final ResultSet rs, final String columnName) throws SQLException {
        return MathUtil.defaultToNull(getInteger(rs, columnName), 0);
    }

    public static Long getLongDefaultToNullIfZero(final ResultSet rs, final String columnName) throws SQLException {
        return MathUtil.zeroToNull(getLong(rs, columnName));
    }

    public static BigDecimal getBigDecimalDefaultToZeroIfNull(final ResultSet rs, final String columnName) throws SQLException {
        return MathUtil.nullToZero(rs.getBigDecimal(columnName));
    }

    public static BigDecimal getBigDecimalDefaultToNullIfZero(final ResultSet rs, final String columnName) throws SQLException {
        return MathUtil.zeroToNull(rs.getBigDecimal(columnName));
    }
}
