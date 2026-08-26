package cz.doghotel.dog.controller;

import cz.doghotel.dog.service.DogNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Doménové chyby → RFC 9457 ProblemDetail. Validace vstupů (400) a ostatní MVC chyby
 * řeší vestavěný handler Spring MVC (spring.mvc.problemdetails.enabled=true).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DogNotFoundException.class)
    public ProblemDetail handleNotFound(DogNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://doghotel.cz/errors/dog-not-found"));
        problem.setTitle("Dog Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", "DOG_NOT_FOUND");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
