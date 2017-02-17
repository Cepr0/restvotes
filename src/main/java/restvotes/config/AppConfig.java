package restvotes.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import restvotes.util.MessageHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static java.time.LocalTime.parse;

/**
 * @author Cepro, 2016-12-13
 */
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
@Component
@ConfigurationProperties(prefix = "restvotes")
public class AppConfig {
    
    private final @NonNull MessageHelper msgHelper;
    
    private static final String END_OF_VOTING_TIME_DEFAULT = "11:00";
    private static final String NEW_DAY_POLL_TIME_DEFAULT = "9:00";
    private static final String SCHEDULE_PATTERN = "1 %s %s * * *";
    private static final String EMAIL_DELIMITERS_PATTERN = "[,;]?\\s+";
    
    private String endOfVotingTime;
    
    private String newDayPollTime;
    
    private String sendErrorsTo;

    public LocalTime getEndOfVotingTimeValue() {
        try {
            return parse(endOfVotingTime, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            LOG.error(msgHelper.logMessage("engine.endOfVotingTime_is_not_parsed", endOfVotingTime));
            return parse(END_OF_VOTING_TIME_DEFAULT);
        }
    }
    
    public LocalTime getNewDayPollTimeValue() {
        try {
            return parse(newDayPollTime, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            LOG.error(msgHelper.logMessage("engine.newDayPollTime_is_not_parsed", newDayPollTime));
            return parse(NEW_DAY_POLL_TIME_DEFAULT);
        }
        
    }
    
    public String getSchedule(ScheduleType type) {
    
        String result = null;
        
        switch (type) {
    
            case NEW_DAY:
                result = format(SCHEDULE_PATTERN, "0", "0");
                break;

            case NEW_DAY_POLL:
                result = format(SCHEDULE_PATTERN,
                        getNewDayPollTimeValue().getMinute(),
                        getNewDayPollTimeValue().getHour());
                break;

            case END_OF_VOTING:
                result = format(SCHEDULE_PATTERN,
                        getEndOfVotingTimeValue().getMinute(),
                        getEndOfVotingTimeValue().getHour());
                break;
        }
        
        return result;
    }
    
    public String[] getEmailList() {
        // TODO Add handler of 'sendErrorsTo'
        return sendErrorsTo.split(EMAIL_DELIMITERS_PATTERN);
    }
    
    public enum ScheduleType {
        NEW_DAY, NEW_DAY_POLL, END_OF_VOTING
    }
}
