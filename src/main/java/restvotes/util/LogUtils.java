package restvotes.util;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static restvotes.util.LogUtils.LogType.*;

/**
 * Logging support utils
 * @author Cepro, 2016-12-11
 */
@Component
public class LogUtils {
    private static MessageSource messages;
    
    @Autowired
    private LogUtils(MessageSource source) {
        messages = source;
    }
    
    public static void debug(Logger log, String codeOrMessage, Object... args) {
        logIt(DEBUG, log, codeOrMessage, args);
    }
    
    public static void error(Logger log, String codeOrMessage, Object... args) {
        logIt(ERROR, log, codeOrMessage, args);
    }
    
    public static void info(Logger log, String codeOrMessage, Object... args) {
        logIt(INFO, log, codeOrMessage, args);
    }
    
    private static void logIt(LogType logType, Logger log, String codeOrMessage, Object... args) {
        
        String message;
        message = messages.getMessage(codeOrMessage, args, String.format(codeOrMessage, args), null);
        
        switch (logType) {
            case INFO:
                log.info(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
        }
    }
    
    public enum LogType {
        INFO, ERROR, DEBUG
    }
}
