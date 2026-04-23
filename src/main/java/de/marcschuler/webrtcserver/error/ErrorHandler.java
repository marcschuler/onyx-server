package de.marcschuler.webrtcserver.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleNotFound(Exception ex) {
        log.error("An unexpected error happened", ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Internal server error");
        problem.setDetail(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex){
        log.error("An exception on a controller happened", ex);
        return ResponseEntity.of(ex.getBody()).build();
    }
}
