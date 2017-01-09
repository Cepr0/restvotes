package restvotes.web.resource;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import java.time.LocalDate;

/**
 * @author Cepro, 2017-01-07
 */
public class PollResource extends ResourceSupport {
    
    LocalDate date;
    Boolean finished;
    Resources<MenuResource> menus;
}