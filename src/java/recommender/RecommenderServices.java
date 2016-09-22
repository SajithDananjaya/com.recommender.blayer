/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recommender;

import java.util.List;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import processes.RecomenderProccess;
import objectModels.FacebookUser;
import objectModels.Song;
import datahandlers.FacebookDataHandler;
/**
 *
 * @author Sajith
 */
@WebService(serviceName = "RecommenderServices")
public class RecommenderServices {

    private boolean systemInitiated = false;

    
    @WebMethod(operationName = "getSystemStatus")
    public boolean getSystemStatus() {
        return RecomenderProccess.systemOnline();
    }
    
    @WebMethod(operationName = "initSystem")
    public boolean initSystem() {
        systemInitiated = false;
        systemInitiated = RecomenderProccess.initateApplication();
        return systemInitiated;
    }
    
    @WebMethod(operationName = "registerUser")
    public boolean registerUser(@WebParam
        (name = "newUser") FacebookUser newUser) {
        return RecomenderProccess.registerUser(newUser);
    }
    
    @WebMethod(operationName = "accessSystem")
    public FacebookUser accessSystem(@WebParam(name = "uname") String uname,
            @WebParam(name = "pword") String pword) {
        return FacebookDataHandler.getUserObj(uname, pword);
    }

    @WebMethod(operationName = "getSongList")
    public List<Song> getSongList(@WebParam
        (name = "loggedUser") FacebookUser loggedUser) {
        return RecomenderProccess.getSongList(loggedUser);
    }
    
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
}
