package restvotes.web.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Poll;

/**
 * @author Cepro, 2016-12-21
 */
@Component
@RequiredArgsConstructor
public class OtherResourceProcessors {
    
    @Component
    public class ResourceSupportProcessor implements ResourceProcessor<ResourceSupport> {
        
        @Override
        public ResourceSupport process(ResourceSupport resource) {
            return resource;
        }
    }
    
    @Component
    public class PollResourceProcessor implements ResourceProcessor<Resource<? extends Poll>> {
        
        // http://stackoverflow.com/a/29039089/5380322
        // https://github.com/spring-projects/spring-hateoas/issues/270
        @Override
        public Resource<? extends Poll> process(Resource<? extends Poll> resource) {
            // Poll source = resource.getContent();
            // PollView poll = new PollView(source);
            // Resource<PollView> pollResource = new Resource<>(poll);
            //
            // return pollResource;
            return resource;
        }
    }
    
    @Component
    public class AnyResourceProcessor implements ResourceProcessor<Resource<?>> {

        @Override
        public Resource<?> process(Resource<?> resource) {
            return resource;
        }
    }

    @Component
    public class AnyResourcesProcessor implements ResourceProcessor<Resources<?>> {

        @Override
        public Resources<?> process(Resources<?> resource) {
            return resource;
        }
    }

    @Component
    public class AnyPagedResourcesProcessor implements ResourceProcessor<PagedResources<?>> {

        @Override
        public PagedResources<?> process(PagedResources<?> resource) {
            return resource;
        }
    }

}
