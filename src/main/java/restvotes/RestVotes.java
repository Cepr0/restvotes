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
public class RestVotes {

    public static void main(String[] args) {
        SpringApplication.run(RestVotes.class, args);
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
        
        Menu m1 = new Menu(r1, asList(
                new MenuItem("Description1 M1", valueOf(15.0)),
                new MenuItem("Description2 M1", valueOf(20.0)),
                new MenuItem("Description3 M1", valueOf(10.0))
        ));
    
        Menu m2 = new Menu(r2, asList(
                new MenuItem("Description1 M2", valueOf(15.50)),
                new MenuItem("Description2 M2", valueOf(20.50)),
                new MenuItem("Description3 M2", valueOf(10.50))
        ));
    
        Menu m3 = new Menu(r3, asList(
                new MenuItem("Description1 M3", valueOf(15.90)),
                new MenuItem("Description2 M3", valueOf(20.90)),
                new MenuItem("Description3 M3", valueOf(10.90))
        ));
        
        menuRepo.save(asList(m1, m2, m3));
        
        Poll poll = new Poll(asList(m1, m2, m3));
        
        pollRepo.save(poll);
    }
}
