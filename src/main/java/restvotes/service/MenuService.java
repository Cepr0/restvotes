package restvotes.service;

import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Vote;

/**
 * MenuService interface described the business logic for {@link Menu} instances
 * @author Cepro, 2017-01-19
 */
public interface MenuService {
    
    /**
     * Submit vote of authenticated user for given Menu in the current Poll
     *
     * @param menu a User choice of the Restaurant menu
     * @return this operation result
     */
    Vote submitVote(Menu menu);
}
