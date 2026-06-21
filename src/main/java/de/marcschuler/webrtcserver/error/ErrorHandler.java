package de.marcschuler.webrtcserver.error;

import de.marcschuler.webrtcserver.error.webclient.PermissionDeniedException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ProblemDetail> handleFileUploadException(FileUploadException ex) {
        log.error("Could not upload file", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<ProblemDetail> handleInvalidMessageException(InvalidMessageException ex) {
        log.error("Could not handle message", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(FilePreviewException.class)
    public ResponseEntity<ProblemDetail> handleFilePreviewException(FilePreviewException ex) {
        log.error("Could not generate preview", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleExpiredJWT(ExpiredJwtException ex) {
        log.error("Expired JWT exception", ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, "JWT is expired");
    }

    //TODO does not work. Need to catch at a deeper level (HandlerExceptionResolver)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("Uploaded file too big, limit is {}B", ex.getMaxUploadSize(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ProblemDetail> handlePermissionDenied(PermissionDeniedException ex) {
        log.error("Permission denied for user request", ex);
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(),"Could not execute action '" + ex.getPermissionType() + "'");
    }


    @ExceptionHandler(InviteException.class)
    public ResponseEntity<ProblemDetail> handleInviteException(InviteException ex) {
        log.error("Invite cannot be done", ex);
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    //TODO rework needed?
    @ExceptionHandler
    public ResponseEntity<ProblemDetail> onTransactionException(TransactionSystemException ex) {
        if (ExceptionUtils.getRootCause(ex) instanceof ConstraintViolationException ex2) {
            log.error("Transaction exception", ex);
            var errorString = ex2.getConstraintViolations().stream()
                    .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                    .collect(Collectors.joining("\n"));
            return buildResponse(HttpStatus.BAD_REQUEST, "Invalid data:\n" + errorString);
        } else {
            return handleException(ex);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {
        log.error("An unexpected error happened", ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Internal server error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        log.error("An exception on a controller happened", ex);
        return ResponseEntity.of(ex.getBody()).build();
    }

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatus status, String title) {
        return buildResponse(status, title, null);
    }

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatus status, String title, String detail) {
        var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        if (detail != null)
            problemDetail.setDetail(detail);
        return ResponseEntity.status(status)
                .body(problemDetail);
    }
}
