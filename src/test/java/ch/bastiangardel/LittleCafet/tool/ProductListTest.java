package ch.bastiangardel.LittleCafet.tool;

import org.jdom2.JDOMException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by bastiangardel on 06.07.17.
 */
public class ProductListTest {

    @Test
    public void count() throws JDOMException, IOException {

        ProductList productList = new ProductList();

        assertEquals(productList.getList().size(),5);
    }


}