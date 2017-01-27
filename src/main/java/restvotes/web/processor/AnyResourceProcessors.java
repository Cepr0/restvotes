package restvotes.web.processor;

import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

/**
 * @author Cepro, 2017-01-28
 */
@Component
public class AnyResourceProcessors {
    
    @Component
    public class SingleResourceProcessor implements ResourceProcessor<Resource<?>> {
    
        @Override
        public Resource<?> process(Resource<?> resource) {
            return resource;
        }
    }
    
    @Component
    public class MultiResourceProcessor implements ResourceProcessor<Resources<Resource<?>>> {
    
        @Override
        public Resources<Resource<?>> process(Resources<Resource<?>> resource) {
            return resource;
        }
    }
    
    @Component
    public class PagedResourceProcessor implements ResourceProcessor<PagedResources<Resource<?>>> {
    
        @Override
        public PagedResources<Resource<?>> process(PagedResources<Resource<?>> resource) {
            return resource;
        }
    }
}
