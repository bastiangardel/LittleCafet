package ch.littlecafet.server.repository;

import ch.littlecafet.server.model.User;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by bastiangardel on 03.07.17.
 */
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

    User findByEmailAndActive(String email, boolean active);

}

