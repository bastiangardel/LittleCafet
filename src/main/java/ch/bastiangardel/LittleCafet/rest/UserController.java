package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.dto.CredentialDTO;
import ch.bastiangardel.LittleCafet.dto.UserInfoDTO;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by bastiangardel on 16.05.16.
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
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.
            getLogger(UserController.class);


    @Autowired
    private UserRepository userRepo;



    @RequestMapping(value = "/auth", method = POST)
    @ApiOperation(value = "Try to logged in")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny")})
    public void authenticate(@RequestBody final CredentialDTO credentials) {

        final Subject subject = SecurityUtils.getSubject();

        log.info("Authenticating {}", credentials.getUsername() + " : " + subject.getSession().getHost());

        subject.login(credentials.daoToModel(subject.getSession().getHost()));
        // set attribute that will allow session querying
        subject.getSession().setAttribute("email", credentials.getUsername());

    }


    @RequestMapping(value = "/logout", method = POST)
    @ApiOperation(value = "Try to logged out")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny")})
    @RequiresAuthentication
    public void logout() {

        final Subject subject = SecurityUtils.getSubject();
        log.info("logout {}",subject.getSession().getAttribute("email"));
        subject.logout();
    }



    @RequestMapping(value = "/info", method = GET)
    @ApiOperation(value = "Get the logged in user's info")
    @ApiResponses(value = { @ApiResponse(code = 401, message = "Access Deny")})
    @RequiresAuthentication
    public UserInfoDTO info(){

        final Subject subject = SecurityUtils.getSubject();
        User user = userRepo.findByEmail((String) subject.getSession().getAttribute("email"));

        log.info("User Info: {}" , user.getEmail());

        return new UserInfoDTO().modelToDTO(user);
    }

}
