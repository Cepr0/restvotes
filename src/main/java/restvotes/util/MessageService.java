package restvotes.util;

/**
 * @author Cepro, 2017-02-09
 */
public interface MessageService {
    
    String userMessage(String codeOrMessage, Object... args);
    
    String logMessage(String codeOrMessage, Object... args);
}
