package ch.bastiangardel.LittleCafet.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by bastiangardel on 13.07.17.
 */
@Controller
@ApiIgnore
public class WelcomeController {

    @RequestMapping("/")
    public String Welcome(Model model) {

        final Subject subject = SecurityUtils.getSubject();

        String username = "Visitor";

        if(subject.isAuthenticated()){
            username = (String) subject.getSession().getAttribute("email");
        }


        model.addAttribute("username", username);
        return "index";
    }
}
