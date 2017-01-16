package restvotes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import restvotes.service.PollService;

import java.time.LocalDate;
import java.time.LocalTime;

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
        
        info(LOG, "App is running...");
    
        LocalTime endOfVotingTime = properties.getEndOfVotingTimeValue();
        info(LOG, "The end of voting time is set to %s", endOfVotingTime);
    
        info(LOG, "Trying to create a new Poll by copying previous one...");
        pollService.copyPrevious();
    
        info(LOG, "Checking previous Polls if they closed...");
    
        LocalDate today = LocalDate.now();
    
        // If now is a time after the end of voting, we are disabling all Polls until now
        if (LocalTime.now().isAfter(endOfVotingTime)) {
            if (pollService.closeAllUntil(today)) {
                info(LOG, "All Polls until now have been closed.");
            }
        } else {
            // If now is time before the end of voting, we are disabling all Polls until yesterday exclusive
            if (pollService.closeAllUntil(today.minusDays(1L))) {
                info(LOG, "All Polls until yesterday have been closed.");
            }
        }
    
        info(LOG, "Checking if all finished Polls have a winner...");
        pollService.placeWinners();
    
        // Setup a scheduled every day task (at 11-00 by default)
        scheduler.schedule(() -> {
            
            // 1. Close all Polls until now
            info(LOG, "The end of voting. Closing current Poll...");
            pollService.closeAllUntil(LocalDate.now());

            // 2. Placing a winner of the voting
            info(LOG, "Setting a winner...");
            pollService.placeWinners();
    
            // TODO 3. Log voting result
            // TODO 4. Send result to each user by email
        
        }, new CronTrigger(properties.getEndOfVotingSchedule()));
    }
    
    @Scheduled(cron="1 0 0 * * *")
    public void newDayTask() {
        
        info(LOG, "Starting a new day. Trying to create a new Poll by copying previous one...");
        pollService.copyPrevious();

        info(LOG, "Checking if all finished Polls have a winner...");
        pollService.placeWinners();
    }
}
