package restvotes.domain.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import restvotes.domain.base.LongId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Defines a restaurant object used in {@link Menu}
 * <p>'name' is an unique field</p>
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "restaurants")
public class Restaurant extends LongId {
    
    private static final int MIN_LEN = 3;
    
    @NonNull
    @NotNull(message = "valid.field")
    @Length(min = MIN_LEN, message = "valid.name")
    @Column(unique = true, nullable = false)
    private String name;
    
    @NonNull
    @NotNull(message = "valid.field")
    @Length(min = MIN_LEN, message = "valid.address")
    @Column(nullable = false)
    private String address;
    
    @URL(message = "valid.url")
    private String url;
    
    private String phone;
}
