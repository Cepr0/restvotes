package restvotes.rest.validator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
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
    
        ErrorMsg errors =  new ErrorMsg();
        for (FieldError fe : bindingResult.getFieldErrors()) {
            errors.addError(
                    msgHelper.userMessage(fe.getObjectName()),
                    msgHelper.userMessage(fe.getField()),
                    fe.getRejectedValue().toString(),
                    msgHelper.userMessage(fe.getDefaultMessage()));
        }
    
        return ResponseEntity.badRequest().body(errors);
    }
    
    public ResponseEntity<?> errorMsg(Set<ConstraintViolation<?>> violations) {

        ErrorMsg errors =  new ErrorMsg();
        for (ConstraintViolation<?> v : violations) {
            errors.addError(
                    msgHelper.userMessage(v.getRootBeanClass().getName()),
                    msgHelper.userMessage(v.getMessageTemplate()),
                    msgHelper.userMessage(v.getInvalidValue().toString()),
                    msgHelper.userMessage(v.getMessage()));
        }
        
        return ResponseEntity.badRequest().body(errors);
    }
    
    public ResponseEntity<?> errorMsg(String localizedMessage) {
    
        ErrorMsg errors =  new ErrorMsg();
        errors.addError(localizedMessage);
        return ResponseEntity.badRequest().body(errors);
    }
    
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private class ErrorMsg {
        @Getter
        private final List<Error> errors = new ArrayList<>();
        
        ErrorMsg addError(String object, String property, String invalidValue, String message) {
            errors.add(new Error(object, property, invalidValue, message));
            return this;
        }
    
        ErrorMsg addError(String message) {
            errors.add(new Error(null, null, null, message));
            return this;
        }
    }

    @JsonInclude(NON_NULL)
    @Value
    private class Error {
        String object;
        String property;
        String invalidValue;
        String message;
    }
}