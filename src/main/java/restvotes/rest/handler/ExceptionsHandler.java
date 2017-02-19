package restvotes.rest.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import restvotes.util.ErrorAssembler;
import restvotes.util.exception.ForbiddenException;
import restvotes.util.exception.NotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

/**
 * The exception handlers collection
 * @author Cepro, 2017-02-14
 */
@RequiredArgsConstructor
@ControllerAdvice
public class ExceptionsHandler {
    
    private final @NonNull ErrorAssembler assembler;
    
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(DataIntegrityViolationException e) {
        return new ResponseEntity<>(assembler.errorMsg(e.getRootCause().getMessage()), CONFLICT);
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        return ResponseEntity.badRequest().body(assembler.errorMsg(violations));
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE + 2)
    @ExceptionHandler(RepositoryConstraintViolationException.class)
    public ResponseEntity<?> handleRepositoryConstraintViolationException(RepositoryConstraintViolationException e) {
        Errors errors = e.getErrors();
        return ResponseEntity.badRequest().body(assembler.errorMsg(errors));
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE + 3)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return ResponseEntity.badRequest().body(assembler.errorMsg(bindingResult));
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE + 4)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(assembler.errorMsg(e.getRootCause().getMessage()));
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE + 5)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(assembler.errorMsg(e.getMessage()), NOT_FOUND);
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE + 6)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        return new ResponseEntity<>(assembler.errorMsg(e.getMessage()), FORBIDDEN);
    }
    
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return new ResponseEntity<>(assembler.errorMsg(e.getMessage()), INTERNAL_SERVER_ERROR);
    }
}
