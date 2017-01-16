package restvotes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import restvotes.domain.entity.*;
import restvotes.repository.*;
import restvotes.service.PollService;

import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static restvotes.domain.entity.User.Role.ROLE_ADMIN;
import static restvotes.domain.entity.User.Role.ROLE_USER;
import static restvotes.util.LogUtils.info;

/**
 * @author Cepro, 2017-01-02
 */
@Slf4j
@Profile({"dev", "demo", "test"})
@Component
@RequiredArgsConstructor
public class DemoData implements ApplicationRunner {

    private final PollService pollService;

    private final PollRepo pollRepo;
    
    private final MenuRepo menuRepo;
    
    private final RestRepo restRepo;
    
    private final UserRepo userRepo;
    
    private final VoteRepo voteRepo;

    private Restaurant r1, r2, r3;
    private Menu m1, m2, m3, m4, m5, m6;
    private Poll p1, p2;
    private User u1, u2, u3, u4, u5, u6, u7, u8;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // TODO Implement all in Services

        Step0();
        Step1();
        Step2();
        Step3();
        Step4();
//        Step5();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void Step5() {
        // http://stackoverflow.com/q/27115639/5380322
        // http://stackoverflow.com/q/26611173/5380322
        // http://stackoverflow.com/a/10466591/5380322

        Poll p = pollRepo.findByDate(p2.getDate());
        List<Menu> menus = p.getMenus();
        Poll p3 = new Poll(menus);
        pollRepo.save(p3);
    }

    @Transactional
    private void Step0() {

        info(LOG, "Inserting demo restaurants...");

        r1 = new Restaurant("Rest1", "Address1", "http://rest1.com", "1234567890");
        r2 = new Restaurant("Rest2", "Address2", "http://rest2.com", "2345678901");
        r3 = new Restaurant("Rest3", "Address3", "http://rest3.com", "3456789012");

        restRepo.save(asList(r1, r2, r3));
    }

    @Transactional
    private void Step1() {

        info(LOG, "Inserting demo menus...");

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
    private void Step2() {

        info(LOG, "Inserting demo polls...");

        p1 = new Poll(LocalDate.now().minusDays(2), asList(m1, m2, m3)).setFinished(true);
        p2 = new Poll(LocalDate.now().minusDays(1), asList(m4, m5, m6));

        pollRepo.save(asList(p1, p2));
    }

    @Transactional
    private void Step3() {

        info(LOG, "Inserting demo users...");

        u1 = new User("Frodo Baggins", "frodo@restvotes.com", "123456", ROLE_ADMIN);
        u2 = new User("Gandalf the Grey", "gandalf@restvotes.com", "123456", ROLE_ADMIN);
        u3 = new User("Sam Gamgee", "sam@restvotes.com", "123456", ROLE_USER);
        u4 = new User("Merry Brandybuck", "marry@restvotes.com", "123456", ROLE_USER);
        u5 = new User("Pippin Took", "pippin@restvotes.com", "123456", ROLE_USER);
        u6 = new User("Aragorn", "aragorn@restvotes.com", "123456", ROLE_USER);
        u7 = new User("Legolas", "legolas@restvotes.com", "123456", ROLE_USER);
        u8 = new User("Gimli", "gimli@restvotes.com", "123456", ROLE_USER);

        userRepo.save(asList(u1, u2, u3, u4, u5, u6, u7, u8));
    }

    @Transactional
    private void Step4() {

        info(LOG, "Inserting demo votes...");

        voteRepo.save(asList(
                // new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u1),
                // new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u2),
                new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u3),
                new Vote(p1, p1.getMenus().get(0), p1.getMenus().get(0).getRestaurant(), u4),
                new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u5),
                new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u6),
                new Vote(p1, p1.getMenus().get(1), p1.getMenus().get(1).getRestaurant(), u7),
                new Vote(p1, p1.getMenus().get(2), p1.getMenus().get(2).getRestaurant(), u8),

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
