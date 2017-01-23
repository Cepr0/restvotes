package restvotes.web.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.web.view.MenuView;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static restvotes.util.LinksHelper.getMenuViewLinks;

/**
 * @author Cepro, 2017-01-07
 */
@RequiredArgsConstructor
@RepositoryRestController
@RequestMapping("/polls")
public class PollController {

    private final @NonNull PagedResourcesAssembler<Poll.Brief> assembler;

    private final @NonNull PollRepo pollRepo;
    
    private final @NonNull VoteRepo voteRepo;
    
    @GetMapping
    ResponseEntity<PagedResources<Resource<Poll.Brief>>> getPolls(Pageable pageable) {

        return ResponseEntity.ok(assembler.toResource(pollRepo.getAll(pageable)));
    }
    
    @GetMapping("/{date}/menus/{id}")
    ResponseEntity<?> getPollMenu(@PathVariable("date") Poll poll, @PathVariable("id") Menu menu) {
    
        if (menu == null || poll == null) {
            // TODO Replace with exception?
            return new ResponseEntity(NOT_FOUND);
        }
    
        LocalDate date = poll.getDate();
        Menu winner = poll.getWinner();
        Long winnerId = (winner != null) ? winner.getId() : null;
    
        Long chosenMenuId = voteRepo.getByUserAndDate(AuthorizedUser.get(), date)
                                    .map(v -> v.getMenu().getId())
                                    .orElse(null);
    
        Map<Long, Integer> ranks = voteRepo.getMenuAndRankParesByDate(date);
    
        MenuView menuView = new MenuView(menu, chosenMenuId, ranks, winnerId);
        return ResponseEntity.ok(new Resource<>(menuView, getMenuViewLinks(menuView, null)));
    }
    
    // http://stackoverflow.com/a/29924387/5380322
    // http://stackoverflow.com/a/31782016/5380322
    // http://stackoverflow.com/a/21362291/5380322
    // @SuppressWarnings("unchecked")
    // @GetMapping("/current")
    // public ResponseEntity<Resource<?>> getCurrent(PersistentEntityResourceAssembler assembler) {
    //     Optional<Poll> pollOptional = pollRepo.getCurrent();
    //
    //     if (pollOptional.isPresent()) {
    //         Poll poll = pollOptional.get();
    //         PersistentEntityResource resource = assembler.toFullResource(poll);
    //         return ResponseEntity.ok(resource);
    //     } else {
    //         return ResponseEntity.notFound().build();
    //     }
    // }
}
