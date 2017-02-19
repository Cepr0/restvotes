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
 * Serving the application parameters from application.properties (prefix = "restvotes")
 *
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
    
    /**
     * 'End of voting' time parameter
     */
    private String endOfVotingTime;
    
    /**
     * 'New Day Poll creating' time parameter - in which time new Poll will be automatically created in new day
     * <p>(in fact - 'Start of voting time')</p>
     */
    private String newDayPollTime;
    
    /**
     * Email list of admins which have to received error info
     */
    private String sendErrorsTo;
    
    /** Get the 'End of voting' time parameter value
     * @return {@link LocalTime} of 'End of voting' time
     */
    public LocalTime getEndOfVotingTimeValue() {
        try {
            return parse(endOfVotingTime, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            LOG.error(msgHelper.logMessage("engine.endOfVotingTime_is_not_parsed", endOfVotingTime));
            return parse(END_OF_VOTING_TIME_DEFAULT);
        }
    }
    
    /**
     * Get the 'New Day Poll creating' time parameter value
     * @return {@link LocalTime} of 'New Day Poll creating' time
     */
    public LocalTime getNewDayPollTimeValue() {
        try {
            return parse(newDayPollTime, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            LOG.error(msgHelper.logMessage("engine.newDayPollTime_is_not_parsed", newDayPollTime));
            return parse(NEW_DAY_POLL_TIME_DEFAULT);
        }
    }
    
    /**
     * Make crone schedule by given {@link ScheduleType} to use in {@link restvotes.StartUp}
     * @param type {@link ScheduleType}
     * @return
     */
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
    
    /**
     * Described Application events:
     * <ul>
     * <li>- New day (0:00)</li>
     * <li>- Auto-creating a Poll for new day (i.e. Start of voting) (9:00)</li>
     * <li>- End of voting (11:00)</li>
     * </ul>
     */
    public enum ScheduleType {
        NEW_DAY, NEW_DAY_POLL, END_OF_VOTING
    }
}
