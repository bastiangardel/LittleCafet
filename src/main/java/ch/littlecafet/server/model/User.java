package ch.littlecafet.server.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bastiangardel on 03.07.17.
 */

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @CreatedDate
    private Long created;



    private String email;

    private String name;
    private Boolean active;
    private String password;

    private Double  amount;

    @ManyToMany
    private List<Role> roles;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        if (roles == null) {
            this.roles = new ArrayList<>();
        }
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }


    public User(Boolean active, Double amount, String email, String name, String password, List<Role> roles) {
        this.active = active;
        this.amount = amount;
        this.email = email;
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    public User() {
    }
}
