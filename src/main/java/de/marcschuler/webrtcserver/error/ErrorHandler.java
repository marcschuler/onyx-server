package de.marcschuler.webrtcserver.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ProblemDetail> handleFileUploadException(FileUploadException ex){
        log.error("Could not upload file", ex);
        return buildResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    //TODO does not work. Need to catch at a deeper level (HandlerExceptionResolver)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex){
        log.error("Uploaded file too big, limit is {}B",ex.getMaxUploadSize(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleNotFound(Exception ex) {
        log.error("An unexpected error happened", ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Internal server error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex){
        log.error("An exception on a controller happened", ex);
        return ResponseEntity.of(ex.getBody()).build();
    }

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatus status, String title){
        var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        return ResponseEntity.status(status)
                .body(problemDetail);
    }
}
