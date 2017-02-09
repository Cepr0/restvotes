package restvotes.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restvotes.domain.entity.*;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.service.VoteService;
import restvotes.util.AuthorizedUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Cepro, 2017-01-19
 */
@RequiredArgsConstructor
@Service
@Transactional
public class VoteServiceImpl implements VoteService {
    
    private final @NonNull VoteRepo voteRepo;
    
    private final @NonNull PollRepo pollRepo;
    
    @Override
    public Vote submitVote(Menu menu) {
        
        Optional<Poll> pollOptional = pollRepo.getCurrent();
        if (!pollOptional.isPresent()) {
            // If Poll is not found
            return null;
        }
    
        Poll poll = pollOptional.get();
        
        if (poll.getFinished()) {
            // If current Poll is closed
            return null;
        }
    
        List<Long> menuIds = poll.getMenus().stream().map(Menu::getId).collect(Collectors.toList());
        if (!menuIds.contains(menu.getId())) {
            // if menu does not belong to current Poll
            return null;
        }
    
        User user = AuthorizedUser.get();
        Restaurant restaurant = menu.getRestaurant();
    
        Optional<Vote> voteOptional = voteRepo.findByPollAndUser(poll, user);
        if (voteOptional.isPresent()) {
        
            Vote vote = voteOptional.get();
            vote.setMenu(menu);
            vote.setRestaurant(restaurant);
            vote.setRegistered(LocalDateTime.now());
        
            return voteRepo.save(vote);
        } else {
            return voteRepo.save(new Vote(poll, menu, restaurant, user));
        }
    }
}
