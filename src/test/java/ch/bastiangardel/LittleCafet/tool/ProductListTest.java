package ch.bastiangardel.LittleCafet.tool;

import ch.bastiangardel.LittleCafet.tool.product.ProductList;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by bastiangardel on 06.07.17.
 */
public class ProductListTest {

    @Autowired
    ProductList productList;


    @Test
    public void count() throws JDOMException, IOException {

        //ProductList productList = new ProductList();

        assertEquals(productList.getList().size(),5);
    }


}