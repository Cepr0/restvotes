package restvotes;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static java.time.LocalTime.parse;
import static restvotes.util.LogUtils.error;

/**
 * @author Cepro, 2016-12-13
 */
@Getter
@Setter
@Slf4j
@Component
@ConfigurationProperties(prefix = "restvotes")
public class AppProperties {
    
    private static final String END_OF_VOTING_TIME_DEFAULT = "11:00";
    private static final String END_OF_VOTING_SCHEDULE_PATTERN = "1 %s %s * * *";
    private static final String EMAIL_DELIMITERS_PATTERN = "[,;]?\\s+";
    
    private String endOfVotingTime;
    
    private String sendErrorsTo;

    public LocalTime getEndOfVotingTimeValue() {
        try {
            return parse(endOfVotingTime, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            error(LOG, "engine.endOfVotingTime_is_not_parsed", endOfVotingTime);
            return parse(END_OF_VOTING_TIME_DEFAULT);
        }
    }
    
    public String getEndOfVotingSchedule() {
        return format(END_OF_VOTING_SCHEDULE_PATTERN,
                getEndOfVotingTimeValue().getMinute(),
                getEndOfVotingTimeValue().getHour());
    }
    
    public String[] getEmailList() {
        // TODO Add handler of 'sendErrorsTo'
        return sendErrorsTo.split(EMAIL_DELIMITERS_PATTERN);
    }
}
