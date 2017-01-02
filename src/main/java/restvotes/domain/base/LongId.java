package restvotes.domain.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base class for entity with {@link Long} id.
 * @author Cepro, 2016-12-10
 */
@MappedSuperclass
@NoArgsConstructor(force = true)
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class LongId extends BaseEntity<Long> {

    @Id
    @GeneratedValue
    // @JsonIgnore
    private final Long id;
}
