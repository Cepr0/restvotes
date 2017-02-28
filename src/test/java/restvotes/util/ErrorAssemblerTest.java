package restvotes.util;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import restvotes.BaseTest;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Cepro, 2017-02-28
 */
@SuppressWarnings("ALL")
public class ErrorAssemblerTest extends BaseTest {
    
    private static final String OBJ = "Object";
    private static final String FLD = "Field";
    private static final String MSG = "Message";
    @Autowired
    private ErrorAssembler errorAssembler;

    @Test
    public void errorMsgForErrors() throws Exception {
        
        Errors errors = Mockito.mock(Errors.class);
        
        when(errors.getFieldErrors()).thenReturn(asList(
                new FieldError(OBJ, FLD, MSG),
                new FieldError(OBJ, FLD, MSG)));
    
        ErrorAssembler.ErrorMsg errorMsg = errorAssembler.errorMsg(errors);
        assertThat(errorMsg.getErrors(), hasSize(2));

        testErrorMsg(errorMsg);
    }
    
    @Test
    public void errorMsgForConstraintViolation() throws Exception {
    
        ConstraintViolation<Object> v = Mockito.mock(ConstraintViolation.class);
        Path.Node node = Mockito.mock(Path.Node.class);
    
        when(node.getName()).thenReturn(FLD);
        when(v.getRootBeanClass()).thenReturn(Object.class);
        when(v.getPropertyPath()).thenReturn(() -> asList(node, node).iterator());
        when(v.getInvalidValue()).thenReturn(null);
        when(v.getMessage()).thenReturn(MSG);
        
        Set<ConstraintViolation<?>> violations = new HashSet<>(asList(v));
        ErrorAssembler.ErrorMsg errorMsg = errorAssembler.errorMsg(violations);
        assertThat(errorMsg.getErrors(), hasSize(1));

        testErrorMsg(errorMsg);
    }
    
    @Test
    public void errorMsgForString() throws Exception {
        
        ErrorAssembler.ErrorMsg errorMsg = errorAssembler.errorMsg(MSG);
        
        List<ErrorAssembler.Error> errors = errorMsg.getErrors();
        assertThat(errors, hasSize(1));
        
        assertThat(errors.get(0).getObject(), is(nullValue()));
        assertThat(errors.get(0).getProperty(), is(nullValue()));
        assertThat(errors.get(0).getInvalidValue(), is(nullValue()));
        assertThat(errors.get(0).getMessage(), is(MSG));
    }
    
    private void testErrorMsg(ErrorAssembler.ErrorMsg errorMsg) {
        
        for (ErrorAssembler.Error error : errorMsg.getErrors()) {
            assertThat(error.getObject(), is(OBJ));
            assertThat(error.getProperty(), is(FLD));
            assertThat(error.getInvalidValue(), is(nullValue()));
            assertThat(error.getMessage(), is(MSG));
        }
    }
}