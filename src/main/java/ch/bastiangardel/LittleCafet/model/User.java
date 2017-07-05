package ch.bastiangardel.LittleCafet.model;

import ch.bastiangardel.LittleCafet.rest.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bastiangardel on 15.05.16.
 *
 * Copyright (c) 2016 Bastian Gardel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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


    @JsonView(View.Summary.class)
    private String email;
    @JsonView(View.Summary.class)
    private String name;
    private Boolean active;
    private String password;
    @JsonView(View.Summary.class)
    private Double solde;

    @JsonView(View.Summary.class)
    @ManyToMany
    private List<Role> roles;


    @OneToMany
    private List<Receipt> receiptHistory;


    @OneToMany
    private List<CheckOut> checkoutInPossesion;



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

    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

    public List<Receipt> getReceiptHistory() {
        if (receiptHistory == null) {
            this.receiptHistory = new ArrayList<>();
        }
        return receiptHistory;
    }

    public void setReceiptHistory(List<Receipt> receiptHistory) {
        this.receiptHistory = receiptHistory;
    }

    public List<CheckOut> getCheckoutInPossesion() {
        if (checkoutInPossesion == null) {
            this.checkoutInPossesion = new ArrayList<>();
        }
        return checkoutInPossesion;
    }

    public void setCheckoutInPossesion(List<CheckOut> checkoutInPossesion) {
        this.checkoutInPossesion = checkoutInPossesion;
    }

    public User(Boolean active, Double solde, List<CheckOut> checkoutInPossesion, String email, String name, String password, List<Receipt> receiptHistory, List<Role> roles) {
        this.active = active;
        this.solde = solde;
        this.checkoutInPossesion = checkoutInPossesion;
        this.email = email;
        this.name = name;
        this.password = password;
        this.receiptHistory = receiptHistory;
        this.roles = roles;
    }

    public User() {
    }
}
