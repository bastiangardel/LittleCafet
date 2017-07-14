package ch.bastiangardel.LittleCafet.tool.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by bastiangardel on 14.07.17.
 */
@Service
public class ProductsFilePathLoader {

    @Value("${littlecafet.productspath}")
    private String productspath;


    public String getProductspath() {
        return productspath;
    }

    public void setProductspath(String productspath) {
        this.productspath = productspath;
    }
}
