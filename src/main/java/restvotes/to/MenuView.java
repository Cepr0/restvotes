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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Cepro, 2017-01-11
 */
@Getter
@Setter
@Relation(collectionRelation = "menus")
@JsonInclude(NON_NULL)
public class MenuView implements Menu.Detailed, Identifiable<Long> {

    @JsonIgnore
    private Long id;

    private Restaurant restaurant;

    private BigDecimal price;

    private Boolean chosen;

    private Integer rank;

    private List<MenuItem> items;

    public MenuView(Menu.Detailed menu) {
        id = menu.getId();
        restaurant = menu.getRestaurant();
        items = menu.getItems();
    }
}
