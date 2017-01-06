package restvotes;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.*;
import restvotes.repository.*;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static restvotes.domain.entity.User.Role.ROLE_ADMIN;
import static restvotes.domain.entity.User.Role.ROLE_USER;

/**
 * @author Cepro, 2017-01-02
 */
@Component
@RequiredArgsConstructor
public class DemoData {

    private final PollRepo pollRepo;
    
    private final MenuRepo menuRepo;
    
    private final RestRepo restRepo;
    
    private final UserRepo userRepo;
    
    private final VoteRepo voteRepo;
    
    @Async
    @EventListener
    @Profile({"dev", "demo", "test"})
    public void propagateData(ApplicationReadyEvent event) {
        
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
        
        Poll p1 = new Poll(asList(m1, m2, m3)).setFinished(true);
        
        pollRepo.save(p1);
    
        Menu m4 = new Menu(r1, asList(
                new MenuItem("Description1 M4", valueOf(16.0)),
                new MenuItem("Description2 M4", valueOf(21.0)),
                new MenuItem("Description3 M4", valueOf(11.0))
        ));
    
        Menu m5 = new Menu(r2, asList(
                new MenuItem("Description1 M5", valueOf(16.50)),
                new MenuItem("Description2 M5", valueOf(21.50)),
                new MenuItem("Description3 M5", valueOf(11.50))
        ));
    
        Menu m6 = new Menu(r3, asList(
                new MenuItem("Description1 M6", valueOf(16.90)),
                new MenuItem("Description2 M6", valueOf(21.90)),
                new MenuItem("Description3 M6", valueOf(11.90))
        ));
    
        menuRepo.save(asList(m4, m5, m6));
    
        Poll p2 = new Poll(p1.getDate().plusDays(1), asList(m4, m5, m6));
    
        pollRepo.save(p2);
    
        User u1 = new User("Frodo Baggins", "frodo@restvotes.com", "123456", ROLE_ADMIN);
        User u2 = new User("Gandalf the Grey", "gandalf@restvotes.com", "123456", ROLE_ADMIN);
        User u3 = new User("Sam Gamgee", "sam@restvotes.com", "123456", ROLE_USER);
        User u4 = new User("Merry Brandybuck", "marry@restvotes.com", "123456", ROLE_USER);
        User u5 = new User("Pippin Took", "pippin@restvotes.com", "123456", ROLE_USER);
        User u6 = new User("Aragorn", "aragorn@restvotes.com", "123456", ROLE_USER);
        User u7 = new User("Legolas", "legolas@restvotes.com", "123456", ROLE_USER);
        User u8 = new User("Gimli", "gimli@restvotes.com", "123456", ROLE_USER);
    
        userRepo.save(asList(u1, u2, u3, u4, u5, u6, u7, u8));
    
        // Vote v1 = new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u1);
        // Vote v2 = new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u2);
        Vote v3 = new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u3);
        Vote v4 = new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u4);
        Vote v5 = new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u5);
        Vote v6 = new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u6);
        Vote v7 = new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u7);
        Vote v8 = new Vote(p1, p1.getMenus().get(2), p1.getMenus().get(2).getRestaurant(), u8);
        
        voteRepo.save(asList(/*v1, v2, */v3, v4, v5, v6, v7, v8));
    
        // Vote v9 = new Vote(p2, p2.getMenus().get(0), p2.getMenus().get(0).getRestaurant(), u1);
        // Vote v10 = new Vote(p2, p2.getMenus().get(0), p2.getMenus().get(0).getRestaurant(), u2);
        Vote v11 = new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u3);
        Vote v12 = new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u4);
        Vote v13 = new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u5);
        Vote v14 = new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u6);
        Vote v15 = new Vote(p2, p2.getMenus().get(2), p2.getMenus().get(2).getRestaurant(), u7);
        Vote v16 = new Vote(p2, p2.getMenus().get(2), p2.getMenus().get(2).getRestaurant(), u8);
    
        voteRepo.save(asList(/*v9, v10, */v11, v12, v13, v14, v15, v16));
    }
}
