/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahandlers;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import objectModels.LastFMUser;
import objectModels.Artist;
import objectModels.Song;

/**
 *
 * @author Sajith
 */
public class LastFMDataHandler {

    private static final Logger LOGGER
            = AppLogger.getNewLogger(AccessLastFM.class.getName());

    private static int currentTagID = 1;
    private static int currentUserID = 1;

    private static HashMap<String, User> initialUsers = new HashMap<>();
    //Stores tag name as key and Tag object as the value
    private static HashMap<String, Tag> initialTags = new HashMap<>();
    //Stores Artist name as key and Artist object as the value
    private static HashMap<String, Artist> initialArtists = new HashMap<>();

    private static HashMap<String, Song> initalSongList = new HashMap<>();

    public static void initiateUsers() {
        loadPreviousData();
        for (String user : getBaseUsers()) {
            LastFMUser tempUser = new LastFMUser(user);//Initates the LastFm User
            for (String artist : getUserArtistList(user)) {
                if (!initialArtists.containsKey(artist)) {
                    //If the artist is not initated initated the artists
                    initiateArtist(artist);
                }
                for (Tag tag : initialArtists.get(artist).getTagList()) {
                    //adds each artists tags to user to generate user taste
                    tempUser.addTag(tag);
                }
            }
            tempUser.filterTaste();
            tempUser.addSongList(getUserSongList(user));
            initialUsers.put(currentUserID + "", tempUser);
            currentUserID++;
        }
        saveTagInfo();
        saveArtistInfo();
    }

    private static void initiateArtist(String artist) {
        Artist tempArtist = new Artist(artist);
        List<Tag> artistTags = new ArrayList<>();
        try {
            for (String tag : getArtistTags(artist)) {
                if (!initialTags.containsKey(tag)) {
                    Tag tempTag = new Tag(currentTagID++, tag);
                    initialTags.put(tag, tempTag);
                }
                artistTags.add(initialTags.get(tag));
            }
        } catch (Exception e) {
            System.out.println("initiate artist failed [" + artist + "]");
        }
        tempArtist.addTagSet(artistTags);
        initialArtists.put(artist, tempArtist);
    }

    //Returns a string list of users
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

    //Returns a string list of Artist's name for the given user
    public static List<String> getUserArtistList(String user) {
        int artistCount = Integer.parseInt(
                ConfigParameters.configParameter().getParameter("artistCountPerUser"));
        String method = "user.getTopArtists"
                + "&user=" + user;
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

    //Returns a string list of tags for the given artist
    public static List<String> getArtistTags(String artistName) {
        int artistTagCount = Integer.parseInt(
                ConfigParameters.configParameter().getParameter("tagCountPerArtist"));
        String method = "artist.getTopTags"
                + "&artist=" + artistName;
        List<String> artistTagList = null;
        try {
            URL url = AccessLastFM.getURL(method, artistTagCount);
            Document artistListXML = AccessLastFM.getResponse(url);
            NodeList artistTagInfo = AccessLastFM.getElementList(artistListXML, "tag");
            artistTagList = AccessLastFM.extractAttributes(artistTagInfo, "name");
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            String msg = "Artist artistTagList unreachable or currupted";
            //LOGGER.log(Level.SEVERE, msg, ex);
        }
        return artistTagList;
    }

    public static List<Song> getUserSongList(String userName) {
        List<Song> userSongList = new ArrayList<>();
        for (String mbid : getSongList(userName)) {
            if (mbid.length() > 0) {
                if (!initalSongList.containsKey(mbid)) {
                    initiateSong(mbid);
                }
                userSongList.add(initalSongList.get(mbid));
            }
        }
        return userSongList;
    }

    private static List<String> getSongList(String user) {
        int songCount = Integer.parseInt(ConfigParameters
                .configParameter().getParameter("numberOfTracksPerUser"));
        String method = "user.getLovedTracks"
                + "&user=" + user;
        List<String> songsList = null;
        try {
            URL url = AccessLastFM.getURL(method, songCount);
            Document songListXML = AccessLastFM.getResponse(url);
            NodeList songListInfo = AccessLastFM
                    .getElementList(songListXML, "track");
            songsList = AccessLastFM.extractAttributes(songListInfo, "mbid");
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            String msg = "User track list unreachable or currupted";
            //LOGGER.log(Level.SEVERE, msg, ex);
        }
        return songsList;
    }

    public static void initiateSong(String mbid) {
        String method = "track.getInfo"
                + "&mbid=" + mbid;
        try {
            URL url = AccessLastFM.getURL(method, 1);
            System.out.println(url.toString());
            Document trackInfoXML = AccessLastFM.getResponse(url);
            Node trackInfo = AccessLastFM
                    .getElementList(trackInfoXML, "track").item(0);
            Node artistInfo = AccessLastFM
                    .getElementList(trackInfoXML, "artist").item(0);
            Node albumInfo = AccessLastFM
                    .getElementList(trackInfoXML, "album").item(0);
            Song tempSong = new Song();
            tempSong.setMbid(mbid);

            String trackName = "N/A";
            if (AccessLastFM
                    .extractSingleAttribute(trackInfo, "name", 0) != null) {
                trackName = AccessLastFM
                        .extractSingleAttribute(trackInfo, "name", 0)
                        .replace("'", "");
            }

            String trackURL = "N/A";
            if (AccessLastFM
                    .extractSingleAttribute(trackInfo, "url", 0) != null) {
                trackURL = AccessLastFM
                        .extractSingleAttribute(trackInfo, "url", 0)
                        .replace("'", "");
            }

            String artistName = "N/A";
            if (AccessLastFM
                    .extractSingleAttribute(artistInfo, "name", 0) != null) {
                artistName = AccessLastFM
                        .extractSingleAttribute(artistInfo, "name", 0)
                        .replace("'", "");
            }
            
            String trackImage = "N/A";
            if (AccessLastFM
                    .extractSingleAttribute(artistInfo, "name", 0) != null) {
                trackImage = AccessLastFM
                    .extractSingleAttribute(albumInfo, "image", 6);
            }

            tempSong.setTrackName(trackName);
            tempSong.setTrackURL(trackURL);
            tempSong.setArtistName(artistName);
            tempSong.setImageURL(trackImage);
            saveSong(tempSong);

        } catch (IOException | ParserConfigurationException | SAXException ex) {
            String msg = "Track inforamtion is unreachable or currupted";
            //LOGGER.log(Level.SEVERE, msg, ex);
        }
    }

    private static int saveSong(Song song) {
        String sqlQ = "INSERT INTO song_information"
                + "(mbid,artist,song,song_url,img_url)"
                + "VALUES('" + song.getMbid() + "','" + song.getArtistName()
                + "','" + song.getTrackName() + "','" + song.getTrackURL()
                + "','" + song.getImageURL() + "')";
        return AccessDB.getDBConnection().saveData(sqlQ);
    }

    private static void loadPreviousData() {
        currentTagID = 1;
        initialTags = loadSavedTags();
        currentTagID = initialTags.size() + 1;
        initialArtists = loadSavedArtists();
        loadSavedTracks();
    }

    private static void loadSavedTracks() {
        String sqlQ = "Select mbid,artist,song,song_url,img_url from song_information";
        ResultSet rs = AccessDB.getDBConnection().getData(sqlQ);
        try {
            while (rs.next()) {
                Song tempSong = new Song();
                tempSong.setMbid(rs.getString("mbid"));
                tempSong.setArtistName(rs.getString("artist"));
                tempSong.setTrackName(rs.getString("song"));
                tempSong.setTrackURL(rs.getString("song_url"));
                tempSong.setImageURL(rs.getString("img_url"));
                initalSongList.put(tempSong.getMbid(), tempSong);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Loading saved tracks failed");
        }
    }

    public static HashMap<String, Tag> loadSavedTags() {
        String filePath = ConfigParameters
                .configParameter().getParameter("tagInfoFilePath");
        HashMap<String, Tag> tempMap = new HashMap<>();
        BufferedReader dataReader = getReader(filePath);
        try {
            String dataLine = "";
            while ((dataLine = dataReader.readLine()) != null) {
                String[] data = dataLine.split(",");
                int tagID = Integer.parseInt(data[0]);
                String tagName = data[1];
                Tag tempTag = new Tag(tagID, tagName);
                tempMap.put(tagName, tempTag);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Learning Saved Tags failed");
        }
        return tempMap;
    }

    public static HashMap<String, Artist> loadSavedArtists() {
        String filePath = ConfigParameters
                .configParameter().getParameter("artistInfoFilePath");
        HashMap<String, Artist> tempMap = new HashMap<>();
        BufferedReader dataReader = getReader(filePath);
        try {
            String dataLine = "";
            while ((dataLine = dataReader.readLine()) != null) {
                String[] data = dataLine.split(",");
                Artist tempArtist = new Artist(data[0]);
                if (data.length > 1) {
                    for (int index = 1; index < data.length; index++) {
                        if (initialTags.containsKey(data[index])) {
                            Tag tag = initialTags.get(data[index]);
                            tempArtist.addTag(tag);
                        }
                    }
                }
                tempMap.put(tempArtist.getName(), tempArtist);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Learning Saved Artist failed");
        }
        return tempMap;
    }

    private static BufferedReader getReader(String filePath) {
        File tempFile = new File(filePath);
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(tempFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Reading file failed");
        }
        return new BufferedReader(fileReader);
    }

    public static void saveTagInfo() {
        BufferedWriter tempWriter = getWriter(ConfigParameters
                .configParameter().getParameter("tagInfoFilePath"));
        try {
            for (String tagName : initialTags.keySet()) {
                Tag t = initialTags.get(tagName);
                String data = t.getID() + "," + t.getName();
                // System.err.println(data);
                tempWriter.write(data + "\n");
            }
            tempWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Saving Tag Info failed");
        }
    }

    private static void saveArtistInfo() {
        BufferedWriter tempWriter = getWriter(ConfigParameters
                .configParameter().getParameter("artistInfoFilePath"));
        try {
            for (String artistName : initialArtists.keySet()) {
                try {
                    Artist artist = initialArtists.get(artistName);
                    String data = artist.getName() + artist.getTagListString();
                    tempWriter.write(data + "\n");
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Saving " + artistName + " failed");
                }
            }
            tempWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Saving Artist Info failed");
        }
    }

    public static void buildDataSheet() {
        String dataSheetPath = ConfigParameters
                .configParameter().getParameter("dataSetFilePath");
        BufferedWriter tempWriter = getWriter(dataSheetPath);

        try {
            tempWriter.write("@relation dataSet");
            tempWriter.newLine();
            tempWriter.newLine();

            tempWriter.write("@attribute userID numeric");
            tempWriter.newLine();

            for (int index = 0; index < currentTagID - 1; index++) {
                tempWriter.write("@attribute tag" + index + " numeric");
                tempWriter.newLine();
            }

            tempWriter.newLine();
            tempWriter.write("@data");
            tempWriter.newLine();

            for (String userID : initialUsers.keySet()) {
                tempWriter.write(userID + initialUsers.
                        get(userID).getTasteString(currentTagID - 1));
                tempWriter.newLine();
            }
            tempWriter.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path for saving dataset is invalid", e);
        }
    }

    private static BufferedWriter getWriter(String filePath) {
        File tempFile = new File(filePath);
        BufferedWriter bufferedWriter = null;
        try {
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(tempFile.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path is invalid", e);
        }
        return bufferedWriter;
    }

    public static List<User> getInitialUserList() {
        List<User> userList = new ArrayList<>();
        for (String userID : initialUsers.keySet()) {
            userList.add(initialUsers.get(userID));
        }
        return userList;
    }

    public static int getInitialTagCount() {
        return initialTags.size();
    }

    public static HashMap<String, Tag> getInitialTags() {
        return initialTags;
    }

    public static HashMap<String, User> getInitiatUserList() {
        return initialUsers;
    }

    public static Artist getArtistInformation(String artistName) {
        if (initialArtists.containsKey(artistName)) {
            return initialArtists.get(artistName);
        }
        return null;
    }

    public static User getInitiatedUser(String userID) {
        return initialUsers.get(userID);
    }
}
