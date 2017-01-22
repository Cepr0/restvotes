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
     * Close all unfinished {@link Poll}s until given date (inclusive).
     * <p>Unfinished Poll must be automatically finished at 'EndOfVotingTime'
     * (see app.properties; default value is 11-00)
     *
     * @return true if operation is performed or false otherwise
     */
    boolean closeAllUntil(LocalDate until);
    
    /**
     * Make a replica of a previous {@link Poll} if its 'date' is less than now
     * <p>A new poll must be created automatically at 00-00
     *
     * @return a new Poll or null if some error has occurred
     */
    Poll copyPrevious();
    
    /**
     * Place vote winners in finished Polls if they still haven't them
     */
    void placeWinners();
    
    /**
     * Delete {@link Poll}s if they finished and don't have any votes (e.g. 'empty')
     * @return number of deleted {@link Poll}s
     */
    int deleteEmpty();
}
