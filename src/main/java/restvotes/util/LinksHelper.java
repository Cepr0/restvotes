package restvotes.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.Poll;
import restvotes.domain.entity.Restaurant;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.rest.webmvc.ProfileController.getRootPath;

/**
 * @author Cepro, 2017-01-14
 */
@RequiredArgsConstructor
@Component
public class LinksHelper {
    
    private static final String POLL = "poll";
    private static final String CURRENT_POLL = "currentPoll";
    private static final String MENU = "menu";
    private static final String RESTAURANT = "restaurant";
    private static final String VOTE = "vote";
    private static final String WINNER = "winner";
    private static final String MENUS = "menus";
    private static final String PROFILE = "profile";
    private static final String POLLS = "polls";
    private static final String SEARCH = "search";
    private static final String USER_PROFILE = "userProfile";
    private static final String SLASH = "/";
    private static final String CURRENT = "current";
    
    private final @NonNull RepositoryEntityLinks entityLinks;
    
    private final @NonNull RepositoryRestConfiguration configuration;
    
    // @SuppressWarnings("SpringJavaAutowiringInspection")
    // @Autowired
    // private LinksHelper(RepositoryEntityLinks entityLinks, RepositoryRestConfiguration configuration) {
    //     LinksHelper.entityLinks = entityLinks;
    //     Assert.notNull(configuration, "RepositoryRestConfiguration must not be null!");
    //     LinksHelper.configuration = configuration;
    // }
    //
    
    public Iterable<Link> getMenuLinks(Menu menu, Boolean finished) {
        
        LinkBuilder menuLinkBuilder = entityLinks.linkForSingleResource(Menu.class, menu.getId());
        Link restaurantLink = entityLinks.linkForSingleResource(Restaurant.class, menu.getRestaurant().getId()).withRel(RESTAURANT);

        if (finished != null && !finished) {
            return Arrays.asList(
                    menuLinkBuilder.withSelfRel(),
                    restaurantLink,
                    menuLinkBuilder.slash(VOTE).withRel(VOTE) // Only if Poll is not finished
            );
        } else {
            return Arrays.asList(
                    menuLinkBuilder.withSelfRel(),
                    restaurantLink
            );
        }
    }
    
    public Iterable<Link> getPollViewLinks(LocalDate pollDate, Long chosenMenuId, Menu winner) {
        
        List<Link> links = new ArrayList<>();
        
        links.add(getPollSelfLink(pollDate));
        
        if (chosenMenuId != null) {
            links.add(entityLinks.linkForSingleResource(Menu.class, chosenMenuId).withRel("userChoice"));
        }
        
        if (winner != null) {
            links.add(entityLinks.linkForSingleResource(Poll.class, pollDate).slash(MENUS).slash(winner).withRel(WINNER));
        }
        
        return links;
    }
    
    public Link getPollLink(Poll poll) {
        return entityLinks.linkForSingleResource(poll).withRel(POLL);
    }
    
    public Link getPollLink(LocalDate date) {
        return entityLinks.linkForSingleResource(Poll.class, date).withRel(POLL);
    }
    
    public Link getCurrentPollLink() {
        return entityLinks.linkFor(Poll.class).slash(CURRENT).withRel(CURRENT_POLL);
    }
    
    public Link getCurrentPollLink(Poll poll) {
        return entityLinks.linkForSingleResource(poll).withRel(CURRENT_POLL);
    }
    
    public Link getCurrentPollLink(LocalDate date) {
        return entityLinks.linkForSingleResource(Poll.class, date).withRel(CURRENT_POLL);
    }
    
    public Link getPollSelfLink(Poll poll) {
        return entityLinks.linkForSingleResource(poll).withSelfRel();
    }
    
    public Link getPollSelfLink(LocalDate date) {
        return entityLinks.linkForSingleResource(Poll.class, date).withSelfRel();
    }
    
    public Link getRestaurantLink(Restaurant restaurant) {
        return entityLinks.linkForSingleResource(restaurant).withRel(RESTAURANT);
    }
    
    public Link getRestaurantMenusLink(Restaurant restaurant) {
        return entityLinks.linkForSingleResource(restaurant).slash(MENUS).withRel(MENUS);
    }
    
    public Link getMenuLink(Menu menu) {
        return entityLinks.linkForSingleResource(menu).withRel(MENU);
    }
    
    public Link getWinnerLink(Poll.Brief poll, Menu winner) {
        return entityLinks.linkForSingleResource(Poll.class, poll.getDate()).slash(MENUS).slash(winner).withRel(WINNER);
    }
    
    public Link getMenuSelfLink(Menu.Detailed menu) {
        return entityLinks.linkForSingleResource(Menu.class, menu.getId()).withSelfRel();
    }
    
    public Link getPollSearchLink() {
        return entityLinks.linkFor(Poll.class).slash(SEARCH).withRel(SEARCH);
    }
    
    public Link getPollProfileLink() {
        return new Link(getRootPath(configuration) + SLASH + POLLS, PROFILE);
    }
    
    public Link getUserProfileLink() {
        return new Link(getBasePath() + SLASH + USER_PROFILE, USER_PROFILE);
    }
    
    private String getBasePath() {
        BaseUri baseUri = new BaseUri(configuration.getBaseUri());
        return baseUri.getUriComponentsBuilder().build().toString();
    }
}
