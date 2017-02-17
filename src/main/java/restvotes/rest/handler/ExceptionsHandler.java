package restvotes.rest.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
 * @author Cepro, 2017-02-14
 */
@RequiredArgsConstructor
@ControllerAdvice
public class ExceptionsHandler {
    
    private final @NonNull ErrorAssembler assembler;
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(DataIntegrityViolationException e) {
        return new ResponseEntity<>(assembler.errorMsg(e.getRootCause().getMessage()), CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        return ResponseEntity.badRequest().body(assembler.errorMsg(violations));
    }
    
    @ExceptionHandler(RepositoryConstraintViolationException.class)
    public ResponseEntity<?> handleRepositoryConstraintViolationException(RepositoryConstraintViolationException e) {
        Errors errors = e.getErrors();
        return ResponseEntity.badRequest().body(assembler.errorMsg(errors));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return ResponseEntity.badRequest().body(assembler.errorMsg(bindingResult));
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(assembler.errorMsg(e.getRootCause().getMessage()));
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(assembler.errorMsg(e.getMessage()), NOT_FOUND);
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        return new ResponseEntity<>(assembler.errorMsg(e.getMessage()), FORBIDDEN);
    }
}
