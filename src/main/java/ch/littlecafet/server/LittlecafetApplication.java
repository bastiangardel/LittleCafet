package ch.littlecafet.server;

import ch.littlecafet.server.model.User;
import ch.littlecafet.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LittlecafetApplication {

	public static void main(String[] args) {
		SpringApplication.run(LittlecafetApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(LittlecafetApplication.class);

	@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {

			repository.save(new User("Jack", "Bauer", "test1", ""));
			repository.save(new User("Chloe", "O'Brian", "test2",""));
			repository.save(new User("Kim", "Bauer", "test3",""));
			repository.save(new User("David", "Palmer", "test4",""));
			repository.save(new User("Michelle", "Dessler", "test5",""));


			// fetch all customers
			log.info("Users found with findAll():");
			log.info("-------------------------------");
			for (User user : repository.findAll()) {
				log.info(user.toString());
			}
			log.info("");
		};
	}
}
