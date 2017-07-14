package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.dto.PaymentDTO;
import ch.bastiangardel.LittleCafet.dto.SuccessMessageDTO;
import ch.bastiangardel.LittleCafet.exception.ProductDoestExist;
import ch.bastiangardel.LittleCafet.exception.UserNotFoundException;
import ch.bastiangardel.LittleCafet.model.Permission;
import ch.bastiangardel.LittleCafet.model.Role;
import ch.bastiangardel.LittleCafet.model.Transaction;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.PermissionRepository;
import ch.bastiangardel.LittleCafet.repository.RoleRepository;
import ch.bastiangardel.LittleCafet.repository.TransactionRepository;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import ch.bastiangardel.LittleCafet.tool.email.EmailService;
import ch.bastiangardel.LittleCafet.tool.product.Product;
import ch.bastiangardel.LittleCafet.tool.product.ProductList;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by bastiangardel on 01.07.17.
 *
 * Copyright (c) 2017 Bastian Gardel
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


    @Autowired
    private EmailService emailService;


    private ProductList productList = new ProductList();


    public AdminController() throws JDOMException, IOException {
    }


    @RequestMapping(value = "/transaction", method = GET)
    @ApiOperation(value = "Get a user's transactions list", notes = "The username is required")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny"),
                            @ApiResponse(code = 404, message = "User not found")})
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    public List<Transaction> getUserTransactionsList(@RequestParam String username,
                                                     @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                     @RequestParam(value = "count", defaultValue = "10", required = false) int size) {

        User user = userRepo.findByEmail(username);

        if (user == null)
            throw new UserNotFoundException("Not found User with Username : " + username);

        List<Transaction> transactionList = new ArrayList<>(transactionRepository.findAllByUser(user, new PageRequest(page, size)).getContent());

        transactionList.sort(Comparator.comparing(Transaction::getCreated));

        for(Transaction ignored :transactionList)
            log.info("Read Transaction: {}", transactionList.get(0).getCreated());

        return transactionList;
    }

    @RequestMapping(value = "/payment", method = POST)
    @ApiOperation(value = "Save a user's payment")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny"),
                            @ApiResponse(code = 404, message = "User not found")})
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    @Transactional
    public SuccessMessageDTO payment(@RequestBody final PaymentDTO paymentDTO) {
        log.info("payment: {} " , paymentDTO.getUsername());

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

    @RequestMapping(value = "/manuallyCheck", method = POST)
    @ApiOperation(value = "Manually check a number of one product")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny"),
            @ApiResponse(code = 404, message = "Product not found"),
            @ApiResponse(code = 404, message = "User not found")})
    @RequiresAuthentication
    @RequiresRoles("ADMIN" )
    @Transactional
    public SuccessMessageDTO manuallyCheck(@RequestParam String username, @RequestParam final int idProduct,  @RequestParam final int number){

        User user = userRepo.findByEmail(username);


        if (user == null)
            throw new UserNotFoundException("Not found User with Username : " + username);

        if (idProduct > productList.getNumberOfProduct()-1)
            throw new ProductDoestExist("Product doesn't exist !!");

        Product p = productList.getProduct(idProduct);

        log.info("Check: {} - {} * {}", new Object[]{username,number,p.getName()});

        for (int i = 1; i <= number; i++)
        {
            Transaction tmp = new Transaction();
            tmp.setUser(user);
            tmp.setAmount(p.getPrice());
            tmp.setDescription(p.getName());
            Transaction transaction = transactionRepository.save(tmp);

            log.info("Create Transaction: {}" , transaction.getCreated());

            user = userRepo.findByEmail(username);

            user.setSolde(user.getSolde() + transaction.getAmount());
            List<Transaction>list = user.getTransactions();
            list.add(transaction);
            user.setTransactions(list);
            userRepo.save(user);
        }

        return new SuccessMessageDTO("Check with success");
    }

    @RequestMapping(value = "/sendinvoice", method = POST)
    @ApiOperation(value = "Envoie de factures")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny"),
            @ApiResponse(code = 404, message = "Product not found"),
            @ApiResponse(code = 404, message = "User not found")})
    @RequiresAuthentication
    @RequiresRoles("ADMIN" )
    @Transactional
    public SuccessMessageDTO sendInvoices(@RequestParam(value = "solde minimum", defaultValue = "10", required = false) final int soldemin,
                                          @RequestParam(value = "username", defaultValue = "", required = false) final String username){

        final Subject subject = SecurityUtils.getSubject();
        User admin = userRepo.findByEmail((String) subject.getSession().getAttribute("email"));

        if (!username.equalsIgnoreCase("")) {
            User user = userRepo.findByEmail(username);

            if (user == null)
                throw new UserNotFoundException("Not found User with Username : " + username);

            emailService.prepareAndSend(admin.getEmail(),
                                        user.getEmail(),
                                        user.getName(),
                                        "Compte Cafétéria",
                                        user.getSolde());
        }


        return new SuccessMessageDTO("Check with success");
    }

    @RequestMapping(value = "/userslist", method = GET)
    @ApiOperation(value = "Get the list of all users")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny")})
    @RequiresAuthentication
    @RequiresRoles("ADMIN" )
    public List<User> getAll() {
        log.info("Get All User");
        return (List<User>)userRepo.findAll();
    }

    @RequestMapping(method = PUT)
    @ApiOperation(value = "Load some elements in database for test purpose ")
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
        userRepo.save(user2);

        final User user3 = new User();
        user3.setActive(true);
        user3.setCreated(System.currentTimeMillis());
        user3.setEmail("bastian.gardel@heig-vd.ch");
        user3.setName("Bastian Gardel");
        user3.setPassword(passwordService.encryptPassword("test"));
        user3.getRoles().add(roleAdmin);
        user3.setSolde(50.0);

        userRepo.save(user3);

        log.info("Scenario initiated.");
    }
}
