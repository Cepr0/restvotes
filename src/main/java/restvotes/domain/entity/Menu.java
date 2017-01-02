package restvotes.domain.entity;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.rest.core.config.Projection;
import restvotes.domain.base.LongId;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static javax.persistence.AccessType.PROPERTY;
import static javax.persistence.CascadeType.ALL;

/**
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@Entity
@Access(PROPERTY)
@Table(name = "menus")
public class Menu extends LongId {
    
    private Restaurant restaurant;
    
    private BigDecimal price = ZERO;
    
    private List<MenuItem> items = new ArrayList<>();
    
    public Menu(@NonNull Restaurant restaurant, @NonNull BigDecimal price, @NonNull List<MenuItem> items) {
        this.restaurant = restaurant;
        this.price = price;
        setItems(items);
    }
    
    @ManyToOne(optional = false)
    public Restaurant getRestaurant() {
        return restaurant;
    }
    
    public Menu setRestaurant(@NonNull Restaurant restaurant) {
        this.restaurant = restaurant;
        return this;
    }
    
    @Column(nullable = false)
    public BigDecimal getPrice() {
        return price;
    }
    
    public Menu setPrice(@NonNull BigDecimal price) {
        this.price = price;
        return this;
    }
    
    @OneToMany(mappedBy = "menu", cascade = ALL, orphanRemoval = true)
    public List<MenuItem> getItems() {
        return items;
    }
    
    public Menu setItems(@NonNull List<MenuItem> items) {
        try {
            items.forEach(item -> item.setMenu(this));
        } catch (Exception ignore) {
        }
        this.items = items;
        return this;
    }
    
    @Projection(name = "detailed", types = Menu.class)
    public interface Detailed {
        Restaurant getRestaurant();
        BigDecimal getPrice();
        List<MenuItem> getItems();
    }
}
