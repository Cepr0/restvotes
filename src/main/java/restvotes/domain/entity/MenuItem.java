package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.rest.core.annotation.RestResource;
import restvotes.domain.base.LongId;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

/**
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")
public class MenuItem extends LongId {
    
    private static final int MIN_LEN = 3;
    
    @ManyToOne
    private Menu menu;

    @NonNull
    @NotNull(message = "valid.field")
    @Length(min = MIN_LEN, message = "valid.name")
    private String description;
    
    @NonNull
    @NotNull(message = "valid.field")
    @DecimalMin(value = "0.01", message = "valid.cost")
    @Digits(integer=6, fraction=2, message = "valid.cost")
    private BigDecimal cost = ZERO;
    
    public MenuItem(@NonNull String description, @NonNull BigDecimal cost) {
        this.description = description;
        this.cost = cost;
    }

    @RestResource(exported = false)
    @JsonIgnore
    public Menu getMenu() {
        return this.menu;
    }
}
