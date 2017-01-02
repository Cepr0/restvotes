package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.config.Projection;
import restvotes.domain.base.LongId;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.ALL;

/**
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "menus")
public class Menu extends LongId {
    
    @ManyToOne(optional = false)
    private Restaurant restaurant;
    
    @OneToMany(mappedBy = "menu", cascade = ALL, orphanRemoval = true)
    private List<MenuItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "menu")
    private List<Vote> votes = new ArrayList<>();
    
    public Menu(@NonNull Restaurant restaurant, @NonNull List<MenuItem> items) {
        this.restaurant = restaurant;
        setItems(items);
    }
    
    @JsonProperty(access = READ_ONLY)
    public BigDecimal getPrice() {
        return items.stream().map(MenuItem::getCost).reduce(BigDecimal::add).orElse(ZERO);
    }
    
    @JsonProperty(access = READ_ONLY)
    public Integer getRank() {
        return votes.size();
    }
    
    public Menu setItems(@NonNull List<MenuItem> items) {
        try {
            items.forEach(item -> item.setMenu(this));
        } catch (Exception ignore) {
        }
        this.items = items;
        return this;
    }
    
    @RestResource(exported = false)
    public List<Vote> getVotes() {
        return votes;
    }
    
    @Projection(name = "detailed", types = Menu.class)
    public interface Detailed {
        Restaurant getRestaurant();
        BigDecimal getPrice();
        Long getRank();
        List<MenuItem> getItems();
    }
}
