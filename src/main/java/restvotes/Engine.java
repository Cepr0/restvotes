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

        info(LOG, "Trying to check previous Polls...");
    
        LocalDate today = LocalDate.now();
    
        // If now is a time after the end of voting, we are disabling all Polls until now
        if (LocalTime.now().isAfter(endOfVotingTime)) {
            if (pollService.disableAllUntil(today)) {
                info(LOG, "All Polls until now have been disabled.");
            }
        } else {
            // If now is time before the end of voting, we are disabling all Polls until yesterday exclusive
            if (pollService.disableAllUntil(today.minusDays(1L))) {
                info(LOG, "All Polls until yesterday have been disabled.");
            }
        }
    
        // Create a new Poll if the current one doesn't exist
        newDayTask();
    
        // Setup a schedule task - disable all Poll until now every day (at 11-00 by default)
        scheduler.schedule(() -> {
        
            // TODO Определить победителя и добавить его в Poll
        
            if (pollService.disableAllUntil(LocalDate.now())) {
                info(LOG, "The current Poll has been disable.");
            }
        
            // TODO Сделать вывод в лог результатов голосования
            // TODO Сделать рассылку результатов по почте всем пользователям
        
        }, new CronTrigger(properties.getEndOfVotingSchedule()));
    }
    
    @Scheduled(cron="1 0 0 * * *")
    public void newDayTask() {
        info(LOG, "Starting a new day. Trying to copy previous Poll or create new one...");

        // If Poll for current day doesn't exist - make copy of the last Poll
        if (pollService.copyPrevious() != null) {
            info(LOG, "A previous Poll is copied.");
        } else {
            // TODO Create new Poll
        }
    }
}
