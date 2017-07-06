package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.dto.BuyDTO;
import ch.bastiangardel.LittleCafet.dto.CredentialDTO;
import ch.bastiangardel.LittleCafet.dto.PaymentDTO;
import ch.bastiangardel.LittleCafet.dto.SuccessMessageDTO;
import ch.bastiangardel.LittleCafet.exception.NotEnoughMoneyDebitException;
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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

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
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger log = LoggerFactory.
            getLogger(TransactionController.class);

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


    private ProductList productList = new ProductList();

    public TransactionController() throws JDOMException, IOException {
    }


    @RequestMapping(value = "/buy", method = POST)
    @RequiresAuthentication
    @Transactional
    public SuccessMessageDTO buy(@RequestParam final int idProduct){


        final Subject subject = SecurityUtils.getSubject();

        User user = userRepo.findByEmail((String) subject.getSession().getAttribute("email"));

        log.info("Buy: {}" , user.getEmail());

        Transaction tmp = new Transaction();

        Product p = productList.getProduct(idProduct);
        tmp.setAmount(p.getPrice());
        tmp.setDescription(p.getName());

        Transaction transaction = transactionRepository.save(tmp);


        user.setSolde(user.getSolde() + transaction.getAmount());
        List<Transaction>list = user.getTransactions();
        list.add(transaction);
        user.setTransactions(list);

        userRepo.save(user);


        return new SuccessMessageDTO("Purchase with success");
    }

}
