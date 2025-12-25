package parfumerie.parfilya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "parfumerie.parfilya.repositories.msql")
@EnableMongoRepositories(basePackages = "parfumerie.parfilya.repositories.mongo")
@EnableNeo4jRepositories(basePackages = "parfumerie.parfilya.repositories.neo4j")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
