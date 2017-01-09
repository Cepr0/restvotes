package restvotes.web.resource;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import restvotes.domain.entity.MenuItem;
import restvotes.domain.entity.Restaurant;

import java.math.BigDecimal;

/**
 * @author Cepro, 2017-01-08
 */
public class MenuResource extends ResourceSupport {
    
    Resource<Restaurant> restaurant;
    BigDecimal price;
    Boolean chosen = false;
    Resources<Resource<MenuItem>> items;
}
