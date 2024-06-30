package org.apache.fineract.api;

import org.apache.fineract.batch.exception.ErrorInfo;
import org.apache.fineract.infrastructure.core.exception.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionApi {

    @Autowired
    private ErrorHandler errorHandler;

    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public ResponseEntity handleRunTimeException(RuntimeException exception) {
        ErrorInfo errorInfo = errorHandler.handle(exception);
        return new ResponseEntity<>(errorInfo.getMessage(), HttpStatusCode.valueOf(errorInfo.getStatusCode()));
    }
}
