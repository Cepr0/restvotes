package restvotes.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

/**
 * @author Cepro, 2017-01-11
 */
@Getter
@Setter
@Relation(value = "menu", collectionRelation = "menus")
// @JsonInclude(NON_NULL)
public class MenuView implements Menu.Detailed, Identifiable<Long> {

    @JsonIgnore
    private Long id;

    private Boolean chosen;

    private int rank = 0;
    
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
        chosen = (chosenMenuId != null) ? chosenMenuId.equals(id) : null;
        rank = (ranks != null) ? ranks.getOrDefault(id, 0) : 0;
    }
}
