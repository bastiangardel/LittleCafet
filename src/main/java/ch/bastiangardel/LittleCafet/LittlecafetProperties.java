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

    private String productspath;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProductspath() {
        return productspath;
    }

    public void setProductspath(String productspath) {
        this.productspath = productspath;
    }
}
