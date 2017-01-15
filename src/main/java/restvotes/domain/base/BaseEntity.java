package restvotes.domain.base;

import org.springframework.hateoas.Identifiable;

import java.io.Serializable;

/**
 * Base class for entity implementations.
 * @author Cepro, 2016-12-10
 */
public abstract class BaseEntity<ID extends Serializable> implements Identifiable<ID> {
    
    @Override
    public abstract ID getId();
    
    public abstract BaseEntity setId(ID id);
}
