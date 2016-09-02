/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JUnitTest;

import datahandlers.AccessLastFM;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author Sajith
 */
public class TestAccessLastFM {

    private static Document doc = null;

    public TestAccessLastFM() throws Exception {
        String filePath = "data/textXML.xml";
        File testFile = new File(filePath);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.parse(testFile);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    
    @Test
    public void test_getElementList() throws Exception{
        NodeList books = AccessLastFM.getElementList(doc, "book");
        NodeList cars = AccessLastFM.getElementList(doc, "cars");
        assertEquals(5, books.getLength());
        assertEquals(null, cars);
    }
    
    @Test
    public void test_extractAttributes(){
        List<String> bookList = new ArrayList<>();
        bookList.add("XML Developer's Guide");
        bookList.add("Midnight Rain");
        bookList.add("Maeve Ascendant");
        bookList.add("Oberon's Legacy");
        bookList.add("The Sundered Grail");
        
        NodeList books = AccessLastFM.getElementList(doc, "book");
        
        List<String> returndBookList 
                = AccessLastFM.extractAttributes(books, "title");
        assertEquals(bookList, returndBookList);
    }
    
    @Test
    public void test_extractSingleAttribute(){
        String author = "Gambardella, Matthew";
        NodeList books = AccessLastFM.getElementList(doc, "book");
        String extractedAuthor 
                = AccessLastFM.extractSingleAttribute(books.item(0), "author",0);
        assertEquals(author, extractedAuthor);
    }
}
