/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JUnitTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import objectModels.Tag;
import objectModels.User;
import objectModels.FacebookUser;

/**
 *
 * @author Sajith
 */
public class TestModels {
    
    public TestModels() {
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
    public void userTaste() {
        Tag tagOne = new Tag("tagOne");
        Tag tagTwo = new Tag("tagTwo");
        Tag tagThree = new Tag("tagThree");
        Tag tagFour = new Tag("tagFour");
        
        User tempUser = new FacebookUser("tempUser");
        
        tempUser.addTag(tagOne);
        tempUser.addTag(tagOne);
        tempUser.addTag(tagTwo);
        tempUser.addTag(tagTwo);
        tempUser.addTag(tagTwo);
        tempUser.addTag(tagThree);
        
        assertEquals(tempUser.getSpiecificTagCount(tagOne), 2);
        assertEquals(tempUser.getSpiecificTagCount(tagTwo), 3);
        assertEquals(tempUser.getSpiecificTagCount(tagThree), 1);
        assertEquals(tempUser.getSpiecificTagCount(tagFour), 0);   
    }
}
