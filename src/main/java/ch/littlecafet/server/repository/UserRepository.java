package ch.littlecafet.server.repository;

import ch.littlecafet.server.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by bastiangardel on 03.07.17.
 */

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByLastName(String lastName);

}

