package ch.bastiangardel.LittleCafet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by bastiangardel on 14.07.17.
 */

@ConfigurationProperties(prefix = "littlecafet")
@Component
public class LittlecafetProperties {

    private String  signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
