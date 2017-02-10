package restvotes.rest.view;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.util.LinksHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Cepro, 2017-02-11
 */
@RequiredArgsConstructor
@Component
public class PollViewAssembler {
    
    private final @NonNull LinksHelper links;
    
    public PollView makePollView(Poll poll, Long chosenMenuId, Map<Long, Integer> ranks, LocalDate curPollDate) {
    
        LocalDate date = poll.getDate();
        Boolean finished = poll.getFinished();
        List<Menu> menus = poll.getMenus();
        Menu winner = poll.getWinner();
        
        PollView pollView = new PollView(poll, chosenMenuId, ranks, curPollDate);
    
        List<Resource<Menu.Detailed>> content = new ArrayList<>();
        for (Menu menu : menus) {
        
            MenuView menuView = new MenuView(menu, chosenMenuId, ranks, winner != null ? winner.getId() : null);
            
            pollView.getMenus().add(menuView);
        
            // making menu resource and adding its links
            content.add(new Resource<>(menuView, links.getMenuLinks(menuView, finished)));
        }
    
        pollView.setMenuResources(new Resources<>(content, links.getPollViewLinks(date, chosenMenuId, winner)));
        return pollView;
    }
}
