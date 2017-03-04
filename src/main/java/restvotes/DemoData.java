package restvotes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import restvotes.domain.entity.*;
import restvotes.repository.*;
import restvotes.util.MessageHelper;

import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static restvotes.domain.entity.User.Role.ROLE_ADMIN;
import static restvotes.domain.entity.User.Role.ROLE_USER;

/**
 * Propagate demo data for Application
 * <p>Available in dev, demo and test Spring profiles only</p>
 * @author Cepro, 2017-01-02
 */
@Slf4j
@Profile({"dev", "demo", "test"})
@Component
@RequiredArgsConstructor
public class DemoData implements ApplicationRunner {
    
    private final @NonNull MessageHelper msgHelper;
    
    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull MenuRepo menuRepo;
    
    private final @NonNull RestRepo restRepo;
    
    private final @NonNull UserRepo userRepo;
    
    private final @NonNull VoteRepo voteRepo;
    
    public static final LocalDate MINUS_2_DAYS = LocalDate.now().minusDays(2);
    public static final LocalDate MINUS_1_DAYS = LocalDate.now().minusDays(1);

    private Restaurant r1, r2, r3;
    private Menu m1, m2, m3, m4, m5, m6;
    private Poll p1, p2;
    public static User u1, u2, u3, u4, u5, u6, u7, u8;

    @Override
    public void run(ApplicationArguments args) throws Exception {
    
        prepareUserData();
        
        if (userRepo.count() > 0) {
            LOG.info("Data is already present. Skipping...");
            return;
        }
    
        try {
            addRestaurants();
            addMenus();
            addPolls();
            addUsers();
            addVotes();
            // copyLastPoll();
        } catch (Exception e) {
            LOG.error(msgHelper.logMessage("Adding demo data failed! Cause: %s", e.getMessage()));
        }
    }
    
    /**
     * Prepare user data to use it also in other places of the Application
     */
    private void prepareUserData() {

        u1 = new User("Frodo Baggins", "frodo@restvotes.com", "123456", ROLE_ADMIN);
        u2 = new User("Gandalf the Grey", "gandalf@restvotes.com", "123456", ROLE_ADMIN);
        u3 = new User("Sam Gamgee", "sam@restvotes.com", "123456", ROLE_USER);
        u4 = new User("Merry Brandybuck", "marry@restvotes.com", "123456", ROLE_USER);
        u5 = new User("Pippin Took", "pippin@restvotes.com", "123456", ROLE_USER);
        u6 = new User("Aragorn", "aragorn@restvotes.com", "123456", ROLE_USER);
        u7 = new User("Legolas", "legolas@restvotes.com", "123456", ROLE_USER);
        u8 = new User("Gimli", "gimli@restvotes.com", "123456", ROLE_USER);
    }
    
    // @Transactional()
    // private void copyLastPoll() {
    //     // http://stackoverflow.com/q/27115639/5380322
    //     // http://stackoverflow.com/q/26611173/5380322
    //     // http://stackoverflow.com/a/10466591/5380322
    //
    //     pollRepo.findByDate(p2.getDate()).ifPresent( p -> {
    //         List<Menu> menus = p.getMenus();
    //         Poll p3 = new Poll(menus);
    //         pollRepo.save(p3);
    //     });
    // }

    @Transactional
    private void addRestaurants() {
    
        LOG.info(msgHelper.logMessage("Inserting demo restaurants..."));

        r1 = new Restaurant("Rest1", "Address1", "http://rest1.com", "1234567890");
        r2 = new Restaurant("Rest2", "Address2", "http://rest2.com", "2345678901");
        r3 = new Restaurant("Rest3", "Address3", "http://rest3.com", "3456789012");

        restRepo.save(asList(r1, r2, r3));
    }

    @Transactional
    private void addMenus() {

        LOG.info(msgHelper.logMessage("Inserting demo menus..."));

        m1 = new Menu(r1, asList(
                new MenuItem("Description1 M1", valueOf(15.0)),
                new MenuItem("Description2 M1", valueOf(20.0)),
                new MenuItem("Description3 M1", valueOf(10.0))
        ));

        m2 = new Menu(r2, asList(
                new MenuItem("Description1 M2", valueOf(15.50)),
                new MenuItem("Description2 M2", valueOf(20.50)),
                new MenuItem("Description3 M2", valueOf(10.50))
        ));

        m3 = new Menu(r3, asList(
                new MenuItem("Description1 M3", valueOf(15.90)),
                new MenuItem("Description2 M3", valueOf(20.90)),
                new MenuItem("Description3 M3", valueOf(10.90))
        ));

        m4 = new Menu(r1, asList(
                new MenuItem("Description1 M4", valueOf(16.0)),
                new MenuItem("Description2 M4", valueOf(21.0)),
                new MenuItem("Description3 M4", valueOf(11.0))
        ));

        m5 = new Menu(r2, asList(
                new MenuItem("Description1 M5", valueOf(16.50)),
                new MenuItem("Description2 M5", valueOf(21.50)),
                new MenuItem("Description3 M5", valueOf(11.50))
        ));

        m6 = new Menu(r3, asList(
                new MenuItem("Description1 M6", valueOf(16.90)),
                new MenuItem("Description2 M6", valueOf(21.90)),
                new MenuItem("Description3 M6", valueOf(11.90))
        ));

        menuRepo.save(asList(m1, m2, m3, m4, m5, m6));
    }

    @Transactional
    private void addPolls() {

        LOG.info(msgHelper.logMessage("Inserting demo polls..."));
    
        p1 = new Poll(MINUS_2_DAYS, asList(m1, m2, m3)).setFinished(true);
        p2 = new Poll(MINUS_1_DAYS, asList(m4, m5, m6));

        pollRepo.save(asList(p1, p2));
    }

    @Transactional
    private void addUsers() {

        LOG.info(msgHelper.logMessage("Inserting demo users..."));
        userRepo.save(asList(u1, u2, u3, u4, u5, u6, u7, u8));
    }

    @Transactional
    private void addVotes() {

        LOG.info(msgHelper.logMessage("Inserting demo votes..."));

        voteRepo.save(asList(
                // uncomment for test purposes
                // new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u1),
                // new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u2),
                new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u3),
                new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u4),
                new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u5),
                new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u6),
                new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u7),
                new Vote(p1, p1.getMenus().get(2), p1.getMenus().get(2).getRestaurant(), u8),
        
                // uncomment for test purposes
                // new Vote(p2, p2.getMenus().get(0), p2.getMenus().get(0).getRestaurant(), u1),
                // new Vote(p2, p2.getMenus().get(0), p2.getMenus().get(0).getRestaurant(), u2),
                new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u3),
                new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u4),
                new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u5),
                new Vote(p2, p2.getMenus().get(1), p2.getMenus().get(1).getRestaurant(), u6),
                new Vote(p2, p2.getMenus().get(2), p2.getMenus().get(2).getRestaurant(), u7),
                new Vote(p2, p2.getMenus().get(2), p2.getMenus().get(2).getRestaurant(), u8)
        ));
    }
}
