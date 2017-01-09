package restvotes.web.resource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import restvotes.domain.entity.Menu;

/**
 * @author Cepro, 2017-01-09
 */
@Getter
@Setter
public class MenuDetailedResource extends Resource<Menu.Detailed> {
    
    private Boolean chosen;
    
    private Integer rank;
    
    public MenuDetailedResource(Menu.Detailed content, Iterable<Link> links) {
        super(content, links);
    }
}
