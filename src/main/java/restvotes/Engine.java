package restvotes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import restvotes.service.PollService;

import java.time.LocalDate;
import java.time.LocalTime;

import static restvotes.AppProperties.ScheduleType.*;
import static restvotes.util.LogUtils.info;

/**
 * @author Cepro, 2017-01-15
 */
@Service
@Slf4j
@AllArgsConstructor
@Profile(value = {"dev", "demo", "prod"})
public class Engine {
    
    private final AppProperties properties;
    
    private final PollService pollService;
    
    private final TaskScheduler scheduler;
    
    @Async
    @EventListener
    public void appReady(ApplicationReadyEvent event) throws InterruptedException {
        
        Thread.sleep(5000);
        
        info(LOG, "STARTING APPLICATION...");
    
        LocalTime endOfVotingTime = properties.getEndOfVotingTimeValue();
        info(LOG, "The end of voting time is set to %s", endOfVotingTime);
    
        LocalTime newDayPollTime = properties.getNewDayPollTimeValue();
        info(LOG, "Automatic Poll creation time is set to %s", newDayPollTime);
    
        // info(LOG, "Trying to create a new Poll by copying previous one...");
        // pollService.copyPrevious();
        //
        info(LOG, "Checking previous Polls if they closed...");
    
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
    
        // If now is a time after the end of voting - disabling all Polls until now
        if (now.isAfter(endOfVotingTime)) {
            if (pollService.closeAllUntil(today)) {
                info(LOG, "All Polls until now have been closed.");
            }
        } else {
            // If now is time before the end of voting, we are disabling all Polls until yesterday (exclusive)
            if (pollService.closeAllUntil(today.minusDays(1L))) {
                info(LOG, "All Polls until yesterday have been closed.");
            }
        }
    
        info(LOG, "Checking if all finished Polls have a winner...");
        pollService.placeWinners();
    
        info(LOG, "Searching 'empty' polls...");
        pollService.deleteEmpty();
    
        // If now is time after Automatic Poll creation time but before the end of voting - automatically creating a new Poll
        if (now.isAfter(newDayPollTime) && now.isBefore(endOfVotingTime)) {
            newPollTask();
        }
    
        // Setup a scheduled end of voting task (at 11:00 by default)
        info(LOG, "Setting up end of voting task...");
        scheduler.schedule(this::endOfVotingTask, new CronTrigger(properties.getSchedule(END_OF_VOTING)));
    
        // Setup a scheduled new day task (at 0:00:01)
        info(LOG, "Setting up new day task...");
        scheduler.schedule(this::newDayTask, new CronTrigger(properties.getSchedule(NEW_DAY)));
    
        // Setup a scheduled creating of a new Poll of the new day task (at 9:00 by default)
        info(LOG, "Setting up new Poll task...");
        scheduler.schedule(this::newPollTask, new CronTrigger(properties.getSchedule(NEW_DAY_POLL)));
    }
    
    private void endOfVotingTask() {
        info(LOG, "THE END OF VOTING...");
        
        // 1. Close all Polls until now
        info(LOG, "Closing current Poll...");
        pollService.closeAllUntil(LocalDate.now());
        
        // 2. Placing a winner of the voting
        info(LOG, "Setting a winner...");
        pollService.placeWinners();
        
        // TODO 3. Log voting result
        // TODO 4. Send result to each user by email (using Queue?..)
    }
    
    private void newDayTask() {
        
        info(LOG, "STARTING NEW DAY...");
        info(LOG, "Checking if all finished Polls have a winner...");
        pollService.placeWinners();
        
        info(LOG, "Searching 'empty' polls...");
        pollService.deleteEmpty();
    }

    private void newPollTask() {
    
        info(LOG, "TRYING TO AUTOMATICALLY CREATE A NEW POLL...");
        pollService.copyPrevious();
    }
    
}
