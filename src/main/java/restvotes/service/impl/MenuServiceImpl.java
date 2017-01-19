package restvotes.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restvotes.AuthorizedUser;
import restvotes.domain.entity.*;
import restvotes.repository.PollRepo;
import restvotes.repository.VoteRepo;
import restvotes.service.MenuService;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Cepro, 2017-01-19
 */
@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MenuServiceImpl implements MenuService {
    
    private final @NonNull VoteRepo voteRepo;
    
    private final @NonNull PollRepo pollRepo;
    
    @Override
    public Vote submitVote(Menu menu) {
        
        Optional<Poll> pollOptional = pollRepo.getCurrent();
        if (!pollOptional.isPresent()) {
            // If current unfinished Poll is not found
            return null;
        }
    
        Poll poll = pollOptional.get();
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
