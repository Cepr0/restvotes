package restvotes.web.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
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
@Relation(value = "menu", collectionRelation = "menus")
@JsonInclude(NON_NULL)
public class MenuView extends Menu implements Menu.Detailed {

    private Boolean chosen = null;

    private Integer rank = null;
    
    public MenuView(Menu menu) {
        super.setId(menu.getId());
        super.setRestaurant(menu.getRestaurant());
        super.setItems(menu.getItems());
    }
    
    public MenuView(Menu menu, Long chosenMenuId, Map<Long, Integer> ranks) {
        this(menu);
        chosen = getId().equals(chosenMenuId);
        rank = (ranks != null) ? ranks.getOrDefault(getId(), 0) : null;
    }
    
    @JsonProperty("restaurant")
    @Override
    public Restaurant getRestaurant() {
        return super.getRestaurant();
    }
    
    @JsonProperty("items")
    @Override
    public List<MenuItem> getItems() {
        return super.getItems();
    }
    
    @JsonProperty("price")
    @Override
    public BigDecimal getPrice() {
        return super.getPrice();
    }
    
    public MenuView setChosen(Boolean chosen) {
        this.chosen = chosen;
        return this;
    }
    
    public MenuView setRank(Integer rank) {
        this.rank = rank;
        return this;
    }
}
