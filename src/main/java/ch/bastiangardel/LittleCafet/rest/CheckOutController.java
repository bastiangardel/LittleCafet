package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.dto.CheckOutCreationDTO;
import ch.bastiangardel.LittleCafet.dto.CheckOutSummaryDTO;
import ch.bastiangardel.LittleCafet.dto.SuccessMessageDTO;
import ch.bastiangardel.LittleCafet.exception.*;
import ch.bastiangardel.LittleCafet.model.CheckOut;
import ch.bastiangardel.LittleCafet.model.Receipt;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.CheckOutRepository;
import ch.bastiangardel.LittleCafet.repository.ReceiptRepository;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
@RequestMapping("/checkouts")
public class CheckOutController {

    private static final Logger log = LoggerFactory.
            getLogger(CheckOutController.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CheckOutRepository checkoutRepo;


    @Autowired
    private ReceiptRepository receiptRepo;

    @RequestMapping(method = POST)
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    public SuccessMessageDTO create(@RequestBody CheckOutCreationDTO checkOutCreationDTO) {
        log.info("create new Checkout {}", checkOutCreationDTO.getUuid());

        User user;
        CheckOut checkOut;

        String email = checkOutCreationDTO.getEmail();

        if (email == null)
            email = "";

        user = userRepo.findByEmail(email);

        if (user == null)
            throw new UserNotFoundException("Not found User with Username : " + email);


        log.info("create new Checkout to user {}", user.getEmail());

        checkOut = checkoutRepo.findByUuid(checkOutCreationDTO.getUuid());

        if (checkOut == null) {

            List<CheckOut> list = user.getCheckoutInPossesion();

            checkOut = checkoutRepo.save(checkOutCreationDTO.dtoToModel(user));

            list.add(checkOut);

            userRepo.save(user);


            return new SuccessMessageDTO("Creation with Success");
        }


        throw new UUIDAlreadyInUseException("UUID " + checkOutCreationDTO.getUuid() + " already in use");
    }


    @JsonView(View.Summary.class)
    @RequestMapping(method = GET)
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    public List<CheckOut> getAll() {
        log.info("getAll Checkouts {}");

        return (List<CheckOut>) checkoutRepo.findAll();
    }

    @RequestMapping(value = "/checkoutlist", method = GET)
    @RequiresAuthentication
    @RequiresRoles("SELLER")
    public List<CheckOutSummaryDTO> getUserCheckOuts() {
        log.info("get User Checkouts {}");
        final Subject subject = SecurityUtils.getSubject();
        String email = (String) subject.getSession().getAttribute("email");

        User user = userRepo.findByEmail(email);

        List<CheckOutSummaryDTO> list = new LinkedList<>();
        for(CheckOut checkOut : user.getCheckoutInPossesion())
        {
            CheckOutSummaryDTO checkOutSummaryDTO = new CheckOutSummaryDTO();
            checkOutSummaryDTO.modelToDto(checkOut);
            list.add(checkOutSummaryDTO);

        }

        return list;
    }

    @RequestMapping(value = "/receipttopay", method = DELETE)
    @RequiresAuthentication
    @RequiresRoles("SELLER")
    public void deleteLastreceipt(@RequestParam("uuid") String uuid) {
        CheckOut checkOut;


        checkOut = checkoutRepo.findByUuid(uuid);

        if (checkOut == null)
            throw new CheckOutNotFoundException("Not Found CheckOut with UUID : " + uuid);

        final Subject subject = SecurityUtils.getSubject();



        if(!checkOut.getOwner().getEmail().equals(subject.getSession().getAttribute("email")))
            throw new OwnerException("Your are not the owner of this checkout");


        Receipt receipt = checkOut.getLastReceipt();


        if (receipt == null)
            throw new NoReceiptToPayExeption("No Receipt to Delete");




        checkOut.setLastReceipt(null);

        checkoutRepo.save(checkOut);

        receiptRepo.delete(receipt);
    }
}
