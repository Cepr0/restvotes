package restvotes.service;

import restvotes.domain.entity.Poll;

import java.time.LocalDate;

/**
 * PollService interface described the business logic for {@link Poll} instances
 *
 * @author Cepro, 2017-01-15
 */
public interface PollService {
    
    /**
     * Disables all unfinished {@link Poll}s until given date (inclusive).
     * <p>Unfinished Poll must be automatically finished at 'EndOfVotingTime'
     * (see app.properties; default value is 11-00)
     *
     * @return true if operation is performed or false otherwise
     */
    boolean disableAllUntil(LocalDate until);
    
    /**
     * Gets {@link Poll} by the given date
     *
     * @param date the given date
     * @return a persisted Poll or null if not found
     */
    Poll getOne(LocalDate date);
    
    /**
     * Make a replica of a previous {@link Poll} if its 'date' is less than now
     * <p>A new poll must be created automatically at 00-00
     *
     * @return a new Poll or null if some error has occurred
     */
    Poll copyPrevious();
    
    /**
     * Creates a replica of the given {@link Poll}.
     * <p>The replica is created for the current day if current Poll not exists.
     *
     * @param poll {@link Poll} that must be copied
     * @return a persisted copy op poll or null if some error has occurred
     */
    Poll copyOf(Poll poll);
    
}
