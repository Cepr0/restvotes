package restvotes.rest.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import restvotes.domain.entity.Poll;
import restvotes.repository.PollRepo;
import restvotes.rest.view.UserProfile;
import restvotes.util.LinksHelper;

/**
 * 'Root' Resource processor (/api).
 * Adds two links: to current {@link Poll} and to {@link UserProfile}
 * @author Cepro, 2017-01-13
 */
@Component
@RequiredArgsConstructor
public class RootResourceProcessor implements ResourceProcessor<RepositoryLinksResource> {
    
    private final @NonNull LinksHelper links;
    
    private final @NonNull PollRepo pollRepo;
    
    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        
        pollRepo.getCurrent().ifPresent(poll ->
                resource.add(links.getCurrentPollLink(), links.getUserProfileLink()));
        
        return resource;
    }
}
