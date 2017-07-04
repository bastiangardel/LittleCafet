package ch.littlecafet.server.repository;

import org.apache.shiro.session.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by bastiangardel on 04.07.17.
 */
@Transactional
public interface SessionRepository extends CrudRepository<Session, Long> {


}
