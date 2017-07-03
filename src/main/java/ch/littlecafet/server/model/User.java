package ch.littlecafet.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by bastiangardel on 03.07.17.
 */

@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;

    protected User() {}

    public User(String firstName, String lastName, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }


    @Override
    public String toString() {
        return String.format(
                "User[id=%d, firstName='%s', lastName='%s', username='%s']",
                id, firstName, lastName, username);
    }

}
