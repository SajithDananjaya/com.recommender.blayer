/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahandlers;

import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import processes.AppLogger;
import processes.ConfigParameters;
import objectModels.User;
import objectModels.Tag;

/**
 *
 * @author Sajith
 */
public class LastFMDataHandler {

    private static final Logger LOGGER
            = AppLogger.getNewLogger(AccessLastFM.class.getName());

    private static int currentTagID = 1;
    private static int currentUserID = 1;

    private static List<User> initialUsers = new ArrayList<>();
    //Stores tag name as key and user object as the value
    private static HashMap<String, Tag> initailTags = new HashMap<>();

    public static List<String> getBaseUsers() {
        String baseUser = ConfigParameters.configParameter().getParameter("lastFMUserName");
        int userCount = Integer.parseInt(
                ConfigParameters.configParameter().getParameter("initialUserCount"));
        String method = "user.getFriends"
                + "&user=" + baseUser;
        List<String> usersList = null;
        try {
            URL url = AccessLastFM.getURL(method, userCount);
            Document usersListXML = AccessLastFM.getResponse(url);
            NodeList usersInfo = AccessLastFM.getElementList(usersListXML, "user");
            usersList = AccessLastFM.extractAttributes(usersInfo, "name");
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            String msg = "Base user url curropted or unreachable";
            LOGGER.log(Level.SEVERE, msg, ex);
        }
        return usersList;
    }

    public static List<String> getUserArtistList(String user) {
        int artistCount = Integer.parseInt(
                ConfigParameters.configParameter().getParameter("artistCountPerUser"));
        String method = "user.getTopArtists"
                + "&user="+user;
        List<String> artistList = null;
        try {
            URL url = AccessLastFM.getURL(method, artistCount);
            Document artistListXML = AccessLastFM.getResponse(url);
            NodeList artistsInfo = AccessLastFM.getElementList(artistListXML, "artist");
            artistList = AccessLastFM.extractAttributes(artistsInfo, "name");
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            String msg = "Users artistList unreachable or currupted";
            LOGGER.log(Level.SEVERE, msg, ex);
        }
        return artistList;
    }
    
    public static List<String> getArtistTags(String artistName){
        int artistTagCount = Integer.parseInt(
                ConfigParameters.configParameter().getParameter("tagCountPerArtist"));
        String method = "artist.getTopTags"
                + "&artist="+artistName;
        List<String> artistTagList = null;
        try {
            URL url = AccessLastFM.getURL(method, artistTagCount);
            Document artistListXML = AccessLastFM.getResponse(url);
            NodeList artistTagInfo = AccessLastFM.getElementList(artistListXML, "tag");
            artistTagList = AccessLastFM.extractAttributes(artistTagInfo, "name");
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            
            String msg = "Artist artistTagList unreachable or currupted";
            LOGGER.log(Level.SEVERE, msg, ex);
        }finally{}
        return artistTagList;
    }

}
