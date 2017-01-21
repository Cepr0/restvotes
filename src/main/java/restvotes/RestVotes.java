package restvotes;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.sql.SQLException;

/**
 * @author Cepro, 2017-01-01
 */
@RequiredArgsConstructor
@SpringBootApplication
@EnableAsync
@EnableScheduling
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
    @Profile(value = {"dev", "demo"})
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
    
    
    /**
     * Get one of TaskScheduler to run our tasks (see {@link Engine})
     *
     * @return TaskScheduler instance
     */
    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
