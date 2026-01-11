package de.marcschuler.webrtcserver.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorConfig {

    @ExceptionHandler
    public ProblemDetail onExpiredJWT(ExpiredJwtException ex) {
        log.error("Expired JWT exception", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "JWT has expired");
    }

    @ExceptionHandler
    public ProblemDetail onException(Exception ex) {
        log.error("Generic exception", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An server error occured");
    }

    @ExceptionHandler
    public ProblemDetail onTransactionException(TransactionSystemException ex) {
        if (ExceptionUtils.getRootCause(ex) instanceof ConstraintViolationException ex2) {
            log.error("Transaction exception", ex);
            var errorString = ex2.getConstraintViolations().stream()
                    .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                    .collect(Collectors.joining("\n"));
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid data:\n" + errorString);
        } else {
            return onException(ex);
        }
    }
}
