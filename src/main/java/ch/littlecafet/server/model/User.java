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
    private String rfid;

    protected User() {}

    public User(String firstName, String lastName, String username,String rfid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.rfid = rfid;
    }


    @Override
    public String toString() {
        return String.format(
                "User[id=%d, firstName='%s', lastName='%s', username='%s', rfid='%s' ]",
                id, firstName, lastName, username,rfid);
    }

}
