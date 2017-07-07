package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.dto.PaymentDTO;
import ch.bastiangardel.LittleCafet.dto.SuccessMessageDTO;
import ch.bastiangardel.LittleCafet.exception.UserNotFoundException;
import ch.bastiangardel.LittleCafet.model.Permission;
import ch.bastiangardel.LittleCafet.model.Role;
import ch.bastiangardel.LittleCafet.model.Transaction;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.PermissionRepository;
import ch.bastiangardel.LittleCafet.repository.RoleRepository;
import ch.bastiangardel.LittleCafet.repository.TransactionRepository;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import ch.bastiangardel.LittleCafet.tool.Product;
import ch.bastiangardel.LittleCafet.tool.ProductList;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by bastiangardel on 16.05.16.
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
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.
            getLogger(AdminController.class);

    @Autowired
    private DefaultPasswordService passwordService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PermissionRepository permissionRepo;

    @Autowired
    private TransactionRepository transactionRepository;


    @RequestMapping(value = "/transaction", method = GET)
    @ApiOperation(value = "Get a user's transactions list", notes = "The username is required")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny"),
                            @ApiResponse(code = 404, message = "User not found")})
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    public List<Transaction> getUserTransactionsList(@RequestParam String username,
                                                     @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                     @RequestParam(value = "count", defaultValue = "10", required = false) int size,
                                                     @RequestParam(value = "order", defaultValue = "ASC", required = false) Sort.Direction direction) {

        User user = userRepo.findByEmail(username);

        if (user == null)
            throw new UserNotFoundException("Not found User with Username : " + username);

        return transactionRepository.findAllByUser(user,new PageRequest(page, size, new Sort(direction, "created"))).getContent();
    }

    @RequestMapping(value = "/payment", method = POST)
    @ApiOperation(value = "Save a user's payment")
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    @Transactional
    public SuccessMessageDTO payment(@RequestBody final PaymentDTO paymentDTO) {
        log.info("payment: {}" , paymentDTO.getUsername());

        User user = userRepo.findByEmail(paymentDTO.getUsername());

        if (user == null)
            throw new UserNotFoundException("Not found User with Username : " + paymentDTO.getUsername());


        Transaction tmp = paymentDTO.daoToModel();
        tmp.setUser(user);
        Transaction transaction = transactionRepository.save(tmp);

        user.setSolde(user.getSolde() - paymentDTO.getAmount());
        List<Transaction>list = user.getTransactions();
        list.add(transaction);
        user.setTransactions(list);

        userRepo.save(user);

        return new SuccessMessageDTO("Payment save with success");
    }


    @RequestMapping(value = "/userslist", method = GET)
    @ApiOperation(value = "Get the list of all users")
    @RequiresAuthentication
    @RequiresRoles("ADMIN" )
    public List<User> getAll() {
        log.info("Get All User");
        return (List<User>)userRepo.findAll();
    }

    @RequestMapping(method = PUT)
    @ApiOperation(value = "Load some elements in database for test purpose ", notes = "The username is required")
    public void initScenario() {
        log.info("Initializing scenario..");
        // clean-up users, roles and permissions
        userRepo.deleteAll();
        roleRepo.deleteAll();
        permissionRepo.deleteAll();
        // define permissions
        Permission p1 = new Permission();
        p1.setName("VIEW_ALL_USERS");
        p1 = permissionRepo.save(p1);
        Permission p2 = new Permission();
        p2.setName("DO_SOMETHING");
        p2 = permissionRepo.save(p2);
        Permission p3 = new Permission();
        p3.setName("SELLING");
        p3 = permissionRepo.save(p3);
        Permission p4 = new Permission();
        p4.setName("BUYING");
        p4 = permissionRepo.save(p4);
        // define roles
        Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleAdmin.getPermissions().add(p1);
        roleAdmin = roleRepo.save(roleAdmin);
        Role roleSeller = new Role();
        roleSeller.setName("SELLER");
        roleSeller.getPermissions().add(p3);
        roleSeller = roleRepo.save(roleSeller);
        Role roleClient = new Role();
        roleClient.setName("Client");
        roleClient.getPermissions().add(p4);
        // define user
        final User user = new User();
        user.setActive(true);
        user.setCreated(System.currentTimeMillis());
        user.setEmail("test@test.com");
        user.setName("Paulo Pires");
        user.setPassword(passwordService.encryptPassword("test"));
        user.getRoles().add(roleAdmin);
        user.setSolde(100.0);
        userRepo.save(user);

        final User user2 = new User();
        user2.setActive(true);
        user2.setCreated(System.currentTimeMillis());
        user2.setEmail("test2@test.com");
        user2.setName("Bastian Gardel");
        user2.setPassword(passwordService.encryptPassword("test"));
        user2.getRoles().add(roleSeller);
        user2.setSolde(100.0);


        final User user3 = new User();
        user3.setActive(true);
        user3.setCreated(System.currentTimeMillis());
        user3.setEmail("test3@test.com");
        user3.setName("David Dupont");
        user3.setPassword(passwordService.encryptPassword("test"));
        user3.getRoles().add(roleSeller);
        user3.setSolde(100.0);
        userRepo.save(user3);


        log.info("Scenario initiated.");
    }
}
