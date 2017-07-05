package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.dto.*;
import ch.bastiangardel.LittleCafet.exception.*;
import ch.bastiangardel.LittleCafet.model.CheckOut;
import ch.bastiangardel.LittleCafet.model.Receipt;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.CheckOutRepository;
import ch.bastiangardel.LittleCafet.repository.ReceiptRepository;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
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
@RequestMapping("/receipts")
public class ReceiptController {
    private static final Logger log = LoggerFactory.
            getLogger(ReceiptController.class);

    @Autowired
    private ReceiptRepository receiptRepo;

    @Autowired
    private CheckOutRepository checkOutRepo;

    @Autowired
    private UserRepository userRepo;

    @RequestMapping(method = POST)
    @RequiresAuthentication
    @RequiresRoles("SELLER")
    public SuccessMessageDTO create(@RequestBody ReceiptCreationDTO receiptCreationDTO){
        log.info("create new Receipt {}");

        CheckOut checkOut;


        checkOut = checkOutRepo.findByUuid(receiptCreationDTO.getUuidCheckout());

        if (checkOut == null)
            throw new CheckOutNotFoundException("Not Found CheckOut with UUID : " + receiptCreationDTO.getUuidCheckout());

        final Subject subject = SecurityUtils.getSubject();


        log.info("{} create new Receipt from {}", checkOut.getOwner().getEmail(), subject.getSession().getAttribute("email"));


        if(!checkOut.getOwner().getEmail().equals(subject.getSession().getAttribute("email")))
            throw new OwnerException("Your are not the owner of this checkout");

        if(checkOut.getLastReceipt() != null)
            throw new ReceiptToPayAlreadyExist("There is already a receipt to pay in this checkout");

        Receipt receipt = receiptRepo.save(receiptCreationDTO.dtoToModel());

        checkOut.setLastReceipt(receipt);

        checkOutRepo.save(checkOut);

        return new SuccessMessageDTO("Creation with Success");
    }

    @JsonView(View.Summary.class)
    @RequestMapping(method = GET)
    @RequiresAuthentication
    @RequiresRoles("ADMIN" )
    public List<Receipt> getAll() {
        log.info("Get All Receipt");
        return (List<Receipt>) receiptRepo.findAll();
    }

    @RequestMapping(value = "/history", method = GET)
    @RequiresAuthentication
    public List<ReceiptHistoryDTO> getReceiptHistory(@RequestParam("uuid") String uuid){

        log.info("Get Receipt History from checkOut : {}", uuid);

        CheckOut checkOut;

        checkOut = checkOutRepo.findByUuid(uuid);
        if (checkOut == null)
            throw new CheckOutNotFoundException("Not Found CheckOut with UUID : " + uuid);

        List<ReceiptHistoryDTO> list = new LinkedList<>();
        for(Receipt receipt : checkOut.getReceiptsHistory())
        {
            ReceiptHistoryDTO receiptPayDTO = new ReceiptHistoryDTO();
            receiptPayDTO.modelToDto(receipt);
            list.add(receiptPayDTO);

        }


        return  list;

    }


    @RequestMapping(value = "/pay", method = GET)
    @RequiresAuthentication
    public ReceiptPayDTO getReceiptToPay(@RequestParam("uuid") String uuid){

        log.info("Get Receipt from checkOut : {}", uuid);

        CheckOut checkOut;

        checkOut = checkOutRepo.findByUuid(uuid);
        if (checkOut == null)
            throw new CheckOutNotFoundException("Not Found CheckOut with UUID : " + uuid);


        ReceiptPayDTO receiptPayDTO = new ReceiptPayDTO();

        Receipt receipt = checkOut.getLastReceipt();

        if (receipt == null)
            throw new NoReceiptToPayExeption("No Receipt to Pay");


        return  receiptPayDTO.modelToDto(receipt);

    }

    @RequestMapping(value = "/pay", method = POST)
    @RequiresAuthentication
    public SuccessMessageDTO paiement(@RequestBody ReceiptPayDTO receiptPayDTO, @RequestParam("uuid") String uuid){
        log.info("PayReceipt : {}", receiptPayDTO.getId());

        final Subject subject = SecurityUtils.getSubject();
        Receipt receipt;

        receipt = receiptRepo.findOne(receiptPayDTO.getId());

        if (receipt == null)
            throw new ReceiptNotFoundException("Not found Receipt with ID : " + receiptPayDTO.getId());


        CheckOut checkOut;

        checkOut = checkOutRepo.findByUuid(uuid);

        if (checkOut == null)
            throw new CheckOutNotFoundException("Not found CheckOut with UUID : " + uuid);

        User owner = checkOut.getOwner();

        if (receipt.isPaid())
            throw new NoReceiptToPayExeption("Receipt with id : " + receipt.getId() + " already pay");

        User user = userRepo.findByEmail((String) subject.getSession().getAttribute("email"));

        if (receipt.getAmount() > user.getSolde())
            throw new NotEnoughMoneyException("You have not enough money in your account!!");


        checkOut.setLastReceipt(null);
        List<Receipt> listreceipt = checkOut.getReceiptsHistory();
        listreceipt.add(receipt);
        receipt.setPaid(true);

        user.setSolde(user.getSolde() - receipt.getAmount());

        receipt.setPaiyedBy(user);

        owner.setSolde(owner.getSolde() + receipt.getAmount());

        List<Receipt> list = user.getReceiptHistory();
        list.add(receipt);


        ApnsService service = APNS.newService()
                .withCert("apns.p12", "sake56ekas")
                .withSandboxDestination()
                .build();

        String payload = APNS.newPayload()
                .alertBody("Receipt " + receipt.getId()+ " on checkout " + uuid + " payed by " + user.getName())
                .alertTitle("Receipt Payed").customField("uuid", uuid).build();

        String token = receipt.getDeviceToken();

        if (token != "")
        {

            log.info("Playload : {}", payload);

            service.push(token, payload);

            log.info("The notification has been hopefully sent");
        }

        userRepo.save(user);
        userRepo.save(owner);
        receiptRepo.save(receipt);
        checkOutRepo.save(checkOut);

        return new SuccessMessageDTO("Payment executed with Success");

    }

}
