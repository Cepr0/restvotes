package restvotes.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import restvotes.domain.base.LongId;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")
public class MenuItem extends LongId {
    
    @ManyToOne
    private Menu menu;

    @NonNull
    @NotEmpty
    private String title;
    
    private String description;
    
    public MenuItem(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
