package restvotes.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

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
    
    public ErrorMsg errorMsg(Set<ConstraintViolation<?>> violations) {
        
        ErrorMsg errorMsg = new ErrorMsg();
        for (ConstraintViolation<?> v : violations) {
            errorMsg.addError(
                    v.getRootBeanClass().getSimpleName(),
                    v.getPropertyPath().iterator().next().getName(),
                    v.getInvalidValue().toString(),
                    v.getMessage());
        }
        
        return errorMsg;
    }
    
    public ErrorMsg errorMsg(String message) {
        
        ErrorMsg errorMsg = new ErrorMsg();
        errorMsg.addError(message);
        return errorMsg;
    }
    
    public <T extends Errors> ErrorMsg errorMsg(T errors) {
        
        ErrorMsg errorMsg = new ErrorMsg();
        for (FieldError fe : errors.getFieldErrors()) {
            errorMsg.addError(
                    fe.getObjectName(),
                    fe.getField(),
                    fe.getRejectedValue().toString(),
                    fe.getDefaultMessage());
        }
        
        return errorMsg;
    }
    
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private class ErrorMsg {
        
        @Getter
        private final List<Error> errors = new ArrayList<>();
        
        private ErrorMsg addError(String object, String property, String invalidValue, String message) {
        
            errors.add(new Error(
                    msgHelper.userMessage(object),
                    msgHelper.userMessage(property),
                    invalidValue,
                    msgHelper.userMessage(message)));
            
            return this;
        }
        
        private ErrorMsg addError(String message) {
        
            errors.add(new Error(null, null, null, msgHelper.userMessage(message)));
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