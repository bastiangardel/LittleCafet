package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.dto.PageTransactionDTO;
import ch.bastiangardel.LittleCafet.dto.SuccessMessageDTO;
import ch.bastiangardel.LittleCafet.exception.ProductDoestExist;
import ch.bastiangardel.LittleCafet.model.Transaction;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.TransactionRepository;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import ch.bastiangardel.LittleCafet.tool.product.Product;
import ch.bastiangardel.LittleCafet.tool.product.ProductList;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.hibernate.cfg.NotYetImplementedException;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger log = LoggerFactory.
            getLogger(TransactionController.class);


    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TransactionRepository transactionRepository;


    @Autowired
    private ProductList productList;



    @RequestMapping(value = "/buyWithID", method = POST)
    @ApiOperation(value = "Buy a product")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny"),
                            @ApiResponse(code = 404, message = "Product not found")})
    @RequiresAuthentication
    @Transactional
    public SuccessMessageDTO buyWithID(@RequestParam final int idProduct){

        final Subject subject = SecurityUtils.getSubject();

        User user = userRepo.findByEmail((String) subject.getSession().getAttribute("email"));

        if (idProduct > productList.getNumberOfProduct()-1)
            throw new ProductDoestExist("Product doesn't exist !!");

        Product p = productList.getProduct(idProduct);

        log.info("Buy: {} - {}" , user.getEmail(), p.getName());

        Transaction tmp = new Transaction();
        tmp.setUser(user);
        tmp.setAmount(p.getPrice());
        tmp.setDescription(p.getName());
        Transaction transaction = transactionRepository.save(tmp);


        user.setSolde(user.getSolde().add(transaction.getAmount()));
        List<Transaction>list = user.getTransactions();
        list.add(transaction);
        user.setTransactions(list);
        userRepo.save(user);


        return new SuccessMessageDTO("Purchase with success");
    }

    @RequestMapping(value = "/buy", method = POST)
    @ApiOperation(value = "Buy a list of products")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny"),
            @ApiResponse(code = 404, message = "Product not found")})
    @RequiresAuthentication
    @Transactional
    public SuccessMessageDTO buy(@RequestBody List<Product> listproduct){

        final Subject subject = SecurityUtils.getSubject();

        User user = userRepo.findByEmail((String) subject.getSession().getAttribute("email"));

        log.info("Buy: {}" ,listproduct);

        for (Product p : listproduct){

            log.info("Buy: {} - {}" , user.getEmail(), p.getName());

            if (!productList.getList().contains(p))
                throw new ProductDoestExist("Product doesn't exist !!");

            Product product = productList.getList().get(productList.getList().indexOf(p));


            Transaction tmp = new Transaction();
            tmp.setUser(user);
            tmp.setAmount(product.getPrice());
            tmp.setDescription(product.getName());
            Transaction transaction = transactionRepository.save(tmp);

            user.setSolde(user.getSolde().add(transaction.getAmount()));
            List<Transaction>list = user.getTransactions();
            list.add(transaction);
            user.setTransactions(list);
            userRepo.save(user);

        }


        return new SuccessMessageDTO("Purchase with success");

    }



    @RequestMapping(value = "/list", method = GET)
    @ApiOperation(value = "Get the logged in user's transactions list")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny")})
    @RequiresAuthentication
    public PageTransactionDTO getTransactionsList(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                 @RequestParam(value = "count", defaultValue = "10", required = false) int size){

        final Subject subject = SecurityUtils.getSubject();

        User user = userRepo.findByEmail((String) subject.getSession().getAttribute("email"));


        //List<Transaction> transactionList = new ArrayList<>(transactionRepository.findAllByUser(user, new PageRequest(page, size)).getContent());

/*        transactionList.sort(Comparator.comparing(Transaction::getCreated));

        if (transactionList.size() != 0)
        {
            log.info("Read Transaction: {}" , transactionList.get(0).getCreated());
        }*/

        return new PageTransactionDTO().modelToDTO(transactionRepository.findAllByUser(user, new PageRequest(page, size)));
    }


}
