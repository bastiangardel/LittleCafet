package ch.bastiangardel.LittleCafet.tool.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


/**
 * Created by bastiangardel on 14.07.17.
 */
@Service
public class MailContentBuilder {
    private TemplateEngine templateEngine;

    @Value("${littlecafet.signature}")
    String signature;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String name, String solde) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("solde", solde);
        context.setVariable("signature", signature);
        return templateEngine.process("mail", context);
    }
}
