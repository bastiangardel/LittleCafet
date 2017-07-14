package ch.bastiangardel.LittleCafet.tool.product;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bastiangardel on 01.07.17.
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
public class ProductList {

    private List<Product> list;


    String productsfilepath = "products.xml";

    public ProductList() throws JDOMException, IOException {
        this.list = new ArrayList<>();


        SAXBuilder sxb = new SAXBuilder();

        Document document = sxb.build(new File(productsfilepath));

        Element racine = document.getRootElement();

        List listProduct = racine.getChildren("product");


        Iterator i = listProduct.iterator();
        while(i.hasNext())
        {
            Element courant = (Element)i.next();
            Product p = new Product();

            p.setName(courant.getChild("name").getText());
            p.setPrice(Double.parseDouble(courant.getChild("price").getText()));

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

    public int getNumberOfProduct()
    {
        return list.size();
    }
}
