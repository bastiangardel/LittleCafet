package ch.bastiangardel.LittleCafet.tool;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProductList {

    private List<Product> list;

    private static String path = "products.xml";

    public ProductList() throws JDOMException, IOException {
        this.list = new ArrayList<>();


        SAXBuilder sxb = new SAXBuilder();

        Document document = sxb.build(new File(path));

        Element racine = document.getRootElement();

        List listProduct = racine.getChildren("product");


        Iterator i = listProduct.iterator();
        while(i.hasNext())
        {
            Element courant = (Element)i.next();
            Product p = new Product();

            p.setId(Integer.parseInt(courant.getChild("id").getText()));
            p.setName(courant.getChild("name").getText());
            p.setPrice(Double.parseDouble(courant.getChild("price").getText()));

            System.out.println(p.getId());
            System.out.println(p.getName());
            System.out.println(p.getPrice());


            System.out.println();

            list.add(p);
        }

    }

    public List<Product> getList() {
        return list;
    }

    public Product getProduct(int id){
        return list.get(id);
    }
}
