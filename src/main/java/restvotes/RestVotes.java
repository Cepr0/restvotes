package restvotes;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.sql.SQLException;

/**
 * @author Cepro, 2017-01-01
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
// @EnableCaching
public class RestVotes {

    public static void main(String[] args) {
        SpringApplication.run(RestVotes.class, args);
    }
    
    /**
     * Start internal H2 server so we can query the DB from IDE
     *
     * @return H2 Server instance
     * @throws SQLException
     */
    @Profile(value = {"dev"})
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
    
    
    /**
     * Get one of TaskScheduler to run our tasks (see {@link Engine})
     *
     * @return TaskScheduler instance
     */
    @Profile(value = {"dev", "demo", "prod"})
    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
    
    /**
     * http://stackoverflow.com/a/30558018/5380322
     * @return a ReloadableResourceBundleMessageSource instance
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setCacheSeconds(60); //reload messages every 60 seconds
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
    
    // /**
    //  * http://stackoverflow.com/a/26283080/5380322
    //  * https://goo.gl/D4oDR9
    //  * @return a {@link SimpleCacheManager}
    //  */
    // @Bean
    // CacheManager cacheManager() {
    //
    //     SimpleCacheManager cacheManager = new SimpleCacheManager();
    //     cacheManager.setCaches(singletonList(new ConcurrentMapCache("polls")));
    //
    //     return cacheManager;
    // }
}
