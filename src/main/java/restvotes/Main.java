package restvotes;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.MenuItem;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Restaurant;
import restvotes.repository.MenuRepo;
import restvotes.repository.PollRepo;
import restvotes.repository.RestRepo;

import java.sql.SQLException;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;

/**
 * @author Cepro, 2017-01-01
 */
@RequiredArgsConstructor
@SpringBootApplication
@EnableAsync
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
    
    private final PollRepo pollRepo;
    
    private final MenuRepo menuRepo;
    
    private final RestRepo restRepo;
    
    @Async
    @EventListener
    @Profile({"dev", "demo", "test"})
    public void propagateDemoData(ApplicationReadyEvent event) {
        
        Restaurant r1 = new Restaurant("Rest1", "Address1", "http://rest1.com", "1234567890");
        Restaurant r2 = new Restaurant("Rest2", "Address2", "http://rest2.com", "2345678901");
        Restaurant r3 = new Restaurant("Rest3", "Address3", "http://rest3.com", "3456789012");
        
        restRepo.save(asList(r1, r2, r3));
        
        Menu m1 = new Menu(r1, valueOf(35.0), asList(
                new MenuItem("Title1 M1", "Description1 M1"),
                new MenuItem("Title2 M1", "Description2 M1"),
                new MenuItem("Title3 M1", "Description3 M1")
        ));
    
        Menu m2 = new Menu(r2, valueOf(45.0), asList(
                new MenuItem("Title1 M2", "Description1 M2"),
                new MenuItem("Title2 M2", "Description2 M2"),
                new MenuItem("Title3 M2", "Description3 M2")
        ));

        Menu m3 = new Menu(r3, valueOf(55.0), asList(
                new MenuItem("Title1 M3", "Description1 M3"),
                new MenuItem("Title2 M3", "Description2 M3"),
                new MenuItem("Title3 M3", "Description3 M3")
        ));
        
        menuRepo.save(asList(m1, m2, m3));
        
        Poll poll = new Poll(asList(m1, m2, m3));
        
        pollRepo.save(poll);
    }
}
