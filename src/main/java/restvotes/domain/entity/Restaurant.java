package restvotes.domain.entity;

import lombok.*;
import org.hibernate.validator.constraints.URL;
import restvotes.domain.base.LongId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Cepro, 2017-01-01
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "restaurants")
public class Restaurant extends LongId {
    
    @NonNull
    @Column(unique = true, nullable = false)
    private String name;
    
    @NonNull
    @Column(nullable = false)
    private String address;
    
    @URL
    private String url;
    
    private String phone;
}
