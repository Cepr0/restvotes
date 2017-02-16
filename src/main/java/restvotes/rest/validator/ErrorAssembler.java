package restvotes.rest.validator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import restvotes.util.MessageHelper;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Cepro, 2017-02-14
 */
@RequiredArgsConstructor
@Component
public class ErrorAssembler {
    
    private final @NonNull MessageHelper msgHelper;
    
    public ResponseEntity<?> errorMsg(BindingResult bindingResult) {
    
        ErrorMsg errorMsg =  new ErrorMsg();
        for (FieldError fe : bindingResult.getFieldErrors()) {
            errorMsg.addError(
                    msgHelper.userMessage(fe.getObjectName()),
                    msgHelper.userMessage(fe.getField()),
                    fe.getRejectedValue().toString(),
                    msgHelper.userMessage(fe.getDefaultMessage()));
        }
    
        return ResponseEntity.badRequest().body(errorMsg);
    }
    
    public ResponseEntity<?> errorMsg(Set<ConstraintViolation<?>> violations) {

        ErrorMsg errorMsg =  new ErrorMsg();
        for (ConstraintViolation<?> v : violations) {
            errorMsg.addError(
                    msgHelper.userMessage(v.getRootBeanClass().getSimpleName()),
                    msgHelper.userMessage(v.getPropertyPath().iterator().next().getName()),
                    msgHelper.userMessage(v.getInvalidValue().toString()),
                    msgHelper.userMessage(v.getMessage()));
        }
        
        return ResponseEntity.badRequest().body(errorMsg);
    }
    
    public ResponseEntity<?> errorMsg(String localizedMessage) {
    
        ErrorMsg errorMsg =  new ErrorMsg();
        errorMsg.addError(localizedMessage);
        return ResponseEntity.badRequest().body(errorMsg);
    }
    
    public ResponseEntity<?> errorMsg(Errors e) {
        ErrorMsg errorMsg =  new ErrorMsg();
        for (FieldError fe : e.getFieldErrors()) {
            errorMsg.addError(
                    msgHelper.userMessage(fe.getObjectName()),
                    msgHelper.userMessage(fe.getField()),
                    fe.getRejectedValue().toString(),
                    msgHelper.userMessage(fe.getDefaultMessage()));
        }
    
        return ResponseEntity.badRequest().body(errorMsg);
    }
    
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private class ErrorMsg {
        @Getter
        private final List<Error> errors = new ArrayList<>();
        
        private ErrorMsg addError(String object, String property, String invalidValue, String message) {
            errors.add(new Error(object, property, invalidValue, message));
            return this;
        }
    
        private ErrorMsg addError(String message) {
            errors.add(new Error(null, null, null, message));
            return this;
        }
    }

    @JsonInclude(NON_NULL)
    @Value
    private class Error {
        private final String object;
        private final String property;
        private final String invalidValue;
        private final String message;
    }
}