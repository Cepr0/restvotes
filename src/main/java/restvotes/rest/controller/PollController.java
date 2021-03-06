package restvotes.rest.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.rest.view.MenuView;
import restvotes.util.AuthorizedUser;
import restvotes.util.LinksHelper;
import restvotes.util.MessageHelper;
import restvotes.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controller to handle a {@link Poll} requests
 *
 * @author Cepro, 2017-01-07
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/polls")
public class PollController {
    
    private final @NonNull LinksHelper links;
    
    private final @NonNull MessageHelper msgHelper;
    
    private final @NonNull PagedResourcesAssembler<Poll.Brief> assembler;
    
    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull VoteRepo voteRepo;
    
    // http://stackoverflow.com/a/21362291/5380322
    // http://stackoverflow.com/a/29924387/5380322
    // http://stackoverflow.com/a/31782016/5380322
    // http://stackoverflow.com/a/21362291/5380322
    
    /**
     * Get {@link Poll} list with Polls in brief form
     * @param pageable pageable parameter
     * @return pageable {@link Poll} list
     */
    @GetMapping
    public ResponseEntity<PagedResources<Resource<Poll.Brief>>> getPolls(Pageable pageable) {

        PagedResources<Resource<Poll.Brief>> resource = assembler.toResource(pollRepo.getAll(pageable));
        return ResponseEntity.ok(resource);
    }
    
    /**
     * Get {@link Menu} of the specified {@link Poll}
     * <p>Used to get a Menu-winner</p>
     * @param poll specified {@link Poll}
     * @param menu requested {@link Menu}
     * @return {@link Menu} with additional info:
     * <ul>
     * <li> - mark 'chosen' by current user</li>
     * <li> - this menu 'rank' </li>
     * <li> - mark 'winner' if menu is winner or not</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    @GetMapping("/{date}/menus/{id}")
    public ResponseEntity<?> getPollMenu(@PathVariable("date") Poll poll, @PathVariable("id") Menu menu) {
    
        if (poll == null) {
            throw new NotFoundException(msgHelper.userMessage("poll.not_found"));
        }
        
        if (menu == null) {
            throw new NotFoundException(msgHelper.userMessage("menu.not_found"));
        }
    
        LocalDate date = poll.getDate();
        Menu winner = poll.getWinner();
        Long winnerId = (winner != null) ? winner.getId() : null;
    
        Long chosenMenuId = voteRepo.getByUserAndDate(AuthorizedUser.get(), date)
                                    .map(v -> v.getMenu().getId())
                                    .orElse(null);
    
        Map<Long, Integer> ranks = voteRepo.getMenuAndRankParesByDate(date);
    
        MenuView menuView = new MenuView(menu, chosenMenuId, ranks, winnerId);
        return ResponseEntity.ok(new Resource<>(menuView, links.getMenuLinks(menuView, null)));
    }
    
    /**
     * Get the current {@link Poll}
     * @param assembler {@link PersistentEntityResourceAssembler} injected to assemble returned Poll
     * @return {@link Poll} in 'full' view
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrent(PersistentEntityResourceAssembler assembler) {
        
        return pollRepo.getCurrent()
                       .map(poll -> ResponseEntity.ok(assembler.toFullResource(poll)))
                       .orElse(ResponseEntity.notFound().build());
    }
}
