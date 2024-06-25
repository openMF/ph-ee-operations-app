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
package org.apache.fineract.infrastructure.core.exceptionmapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiGlobalErrorResponse;
import org.apache.fineract.infrastructure.core.exception.ErrorHandler;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * An {@link ExceptionMapper} to map {@link PlatformDataIntegrityException} thrown by platform into a HTTP API friendly
 * format.
 *
 * The {@link PlatformDataIntegrityException} is thrown when modifying api call result in data integrity checks to be
 * fired.
 */
@Provider
@Component
@Scope("singleton")
@Slf4j
public class PlatformDataIntegrityExceptionMapper implements FineractExceptionMapper, ExceptionMapper<PlatformDataIntegrityException> {

    @Override
    public Response toResponse(final PlatformDataIntegrityException exception) {
        log.warn("Exception occurred", ErrorHandler.findMostSpecificException(exception));
        String originalExceptionMessage = exception.getDefaultUserMessage();
        // TODO: extract?
        String message = checkForUniqueConstraintError(exception, originalExceptionMessage);

        if (message.equals(originalExceptionMessage)) {
            message = checkForForeignConstraintError(exception, originalExceptionMessage);
        }
        // This must be last one!
        if (message.equals(originalExceptionMessage)) {
            message = originalExceptionMessage;
        }
        final ApiGlobalErrorResponse dataIntegrityError = ApiGlobalErrorResponse.dataIntegrityError(exception.getGlobalisationMessageCode(),
                message, exception.getParameterName(), exception.getDefaultUserMessageArgs());

        return Response.status(Status.FORBIDDEN).entity(dataIntegrityError).type(MediaType.APPLICATION_JSON).build();
    }

    @Override
    public int errorCode() {
        return 3001;
    }

    private static String checkForUniqueConstraintError(PlatformDataIntegrityException exception, String originalExceptionMessage) {
        String message = originalExceptionMessage;
        if (exception.getMessage().contains("duplicate key value violates unique constraint")) {
            String key = StringUtils.substringBetween(exception.getMessage(), "Detail: Key (", ")=(");
            String entry = StringUtils.substringBetween(exception.getMessage(), ")=(", ") already exists");
            message = "Duplicate entry '" + entry + "' for key '" + key + "'";
        } else if (exception.getMessage().contains("Duplicate entry")) {
            message = "Duplicate entry" + StringUtils.substringBetween(exception.getMessage(), "Duplicate entry", "\nError Code");
        }
        return message;
    }

    private String checkForForeignConstraintError(PlatformDataIntegrityException exception, String originalExceptionMessage) {
        String message = originalExceptionMessage;
        // POSTGRES
        if (exception.getMessage().contains("is still referenced from table")) {
            String key = StringUtils.substringBetween(exception.getMessage(), "Detail: Key (", ")=(");
            String entry = StringUtils.substringBetween(exception.getMessage(), ")=(", ") is still referenced from table");
            message = "Foreign key constraint fails: Entry with " + key + ": " + entry + " is still referenced!";
        } else if (exception.getMessage().contains("is not present in table")) {
            String key = StringUtils.substringBetween(exception.getMessage(), "Detail: Key (", ")=(");
            String entry = StringUtils.substringBetween(exception.getMessage(), ")=(", ") is not present in table");
            message = "Foreign key constraint fails: Entry with " + key + ": " + entry + " does not exists";
        }
        // MYSQL / MARIADB
        else if (exception.getMessage().contains("a foreign key constraint fails")) {
            String key = StringUtils.substringBetween(exception.getMessage(), "FOREIGN KEY (", ") REFERENCES");
            message = "Foreign key constraint fails for " + key;
        }
        return message;
    }
}
