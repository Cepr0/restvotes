package restvotes;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import restvotes.config.AppConfig;
import restvotes.service.PollService;
import restvotes.util.MessageHelper;

import java.time.LocalDate;
import java.time.LocalTime;

import static restvotes.config.AppConfig.ScheduleType.*;

/**
 * @author Cepro, 2017-01-15
 */
@Component
@Slf4j
@AllArgsConstructor
@Profile(value = {"dev", "demo", "prod"})
public class StartUp {
    
    private final @NonNull MessageHelper msgHelper;
    
    private final @NonNull AppConfig config;
    
    private final @NonNull PollService pollService;
    
    private final @NonNull TaskScheduler scheduler;
    
    @Async
    @EventListener
    public void appReady(ApplicationReadyEvent event) throws InterruptedException {
        
        Thread.sleep(1000);
    
        LOG.info(msgHelper.logMessage("STARTING APPLICATION..."));
    
        LocalTime endOfVotingTime = config.getEndOfVotingTimeValue();
        LOG.info(msgHelper.logMessage("The end of voting time is set to %s", endOfVotingTime));
    
        LocalTime newDayPollTime = config.getNewDayPollTimeValue();
        LOG.info(msgHelper.logMessage("Automatic Poll creation time is set to %s", newDayPollTime));
    
        LOG.info(msgHelper.logMessage("Checking previous Polls if they closed..."));
    
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
    
        // If now is a time after the end of voting - disabling all Polls until now
        if (now.isAfter(endOfVotingTime)) {
            if (pollService.closeAllUntil(today)) {
                LOG.info(msgHelper.logMessage("All Polls until now have been closed."));
            }
        } else {
            // If now is time before the end of voting, we are disabling all Polls until yesterday (exclusive)
            if (pollService.closeAllUntil(today.minusDays(1L))) {
                LOG.info(msgHelper.logMessage("All Polls until yesterday have been closed."));
            }
        }
    
        LOG.info(msgHelper.logMessage("Checking if all finished Polls have a winner..."));
        pollService.placeWinners();
    
        LOG.info(msgHelper.logMessage("Searching 'empty' polls..."));
        pollService.deleteEmpty();
    
        // If now is time after Automatic Poll creation time but before the end of voting - automatically creating a new Poll
        if (now.isAfter(newDayPollTime) && now.isBefore(endOfVotingTime)) {
            newPollTask();
        }
    
        // Setup a scheduled end of voting task (at 11:00 by default)
        LOG.info(msgHelper.logMessage("Setting up end of voting task..."));
        scheduler.schedule(this::endOfVotingTask, new CronTrigger(config.getSchedule(END_OF_VOTING)));
    
        // Setup a scheduled new day task (at 0:00:01)
        LOG.info(msgHelper.logMessage("Setting up new day task..."));
        scheduler.schedule(this::newDayTask, new CronTrigger(config.getSchedule(NEW_DAY)));
    
        // Setup a scheduled creating of a new Poll of the new day task (at 9:00 by default)
        LOG.info(msgHelper.logMessage("Setting up new Poll task..."));
        scheduler.schedule(this::newPollTask, new CronTrigger(config.getSchedule(NEW_DAY_POLL)));
    }
    
    private void endOfVotingTask() {
        LOG.info(msgHelper.logMessage("THE END OF VOTING..."));
        
        // 1. Close all Polls until now
        LOG.info(msgHelper.logMessage("Closing current Poll..."));
        pollService.closeAllUntil(LocalDate.now());
        
        // 2. Placing a winner of the voting
        LOG.info(msgHelper.logMessage("Setting a winner..."));
        pollService.placeWinners();
        
        // TODO 3. Log voting result
        // TODO 4. Send result to each user by email (using Queue?..)
    }
    
    private void newDayTask() {
        
        LOG.info(msgHelper.logMessage("STARTING NEW DAY..."));
        LOG.info(msgHelper.logMessage("Checking if all finished Polls have a winner..."));
        pollService.placeWinners();
        
        LOG.info(msgHelper.logMessage("Searching 'empty' polls..."));
        pollService.deleteEmpty();
    }

    private void newPollTask() {
    
        LOG.info(msgHelper.logMessage("TRYING TO AUTOMATICALLY CREATE A NEW POLL..."));
        pollService.copyPrevious();
    }
    
}