package restvotes.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;
import restvotes.domain.entity.Menu;
import restvotes.domain.entity.MenuItem;
import restvotes.domain.entity.Restaurant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Cepro, 2017-01-11
 */
@Getter
@Setter
@Relation(value = "menu", collectionRelation = "menus")
@JsonInclude(NON_NULL)
public class MenuView implements Menu.Detailed, Identifiable<Long> {

    @JsonIgnore
    private Long id;

    private Boolean chosen;

    private Integer rank = null;
    
    private Restaurant restaurant;
    
    private List<MenuItem> items;
    
    private BigDecimal price;
    
    public MenuView(Menu.Detailed menu) {
        id = menu.getId();
        restaurant = menu.getRestaurant();
        price = menu.getPrice();
        items = menu.getItems();
    }
    
    public MenuView(Menu.Detailed menu, Long chosenMenuId, Map<Long, Integer> ranks) {
        this(menu);
        chosen = id.equals(chosenMenuId);
        rank = (ranks != null) ? ranks.getOrDefault(id, 0) : null;
    }
}
