package restvotes.web.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.repository.PollRepo;

import static restvotes.util.LinksHelper.getCurrentPollLink;
import static restvotes.util.LinksHelper.getUserProfileLink;

/**
 * @author Cepro, 2017-01-13
 */
@Component
@RequiredArgsConstructor
public class RootResourceProcessor implements ResourceProcessor<RepositoryLinksResource> {
    
    private final @NonNull PollRepo pollRepo;
    
    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        pollRepo.getCurrent()
                .ifPresent(poll -> resource.add(getCurrentPollLink(poll), getUserProfileLink()));
        return resource;
    }
}
