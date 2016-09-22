/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahandlers;

import java.util.List;
import java.util.ArrayList;
import objectModels.FacebookUser;
import java.util.HashMap;
import processes.ConfigParameters;
import java.net.URL;
import java.io.IOException;
import org.json.JSONObject;
import objectModels.Artist;
import objectModels.Tag;
import java.sql.ResultSet;

/**
 *
 * @author Sajith
 */
public class FacebookDataHandler {

    private static HashMap<String, Integer> newArtists = new HashMap<>();

    public static FacebookUser initiateUserTaste(FacebookUser user) {
        List<String> artistList = getLikedArtists(user);
        artistList.addAll(getRecentMusicPosts(user));
        System.out.println("Learnd : "+artistList.size());
        for (String artist : artistList) {
            Artist tempArtist = LastFMDataHandler
                    .getArtistInformation(artist);
            if (tempArtist != null) {
                for (Tag tag : tempArtist.getTagList()) {
                    user.addTag(tag);
                }
            } else {
                if (newArtists.containsKey(artist)) {
                    int count = newArtists.get(artist);
                    count++;
                    newArtists.replace(artist, count);
                } else {
                    newArtists.put(artist, 1);
                }
            }
        }
        return user;
    }

    public static List<String> getLikedArtists(FacebookUser user) {
        List<String> artists = new ArrayList<>();
        int artistCount = Integer.parseInt(
                ConfigParameters.configParameter()
                .getParameter("artistCountPerUser"));
        String method = user.getAccountID() + "/Music";
        try {
            URL url = AccessFB.getURL(user, method, artistCount);
            String response = AccessFB.getResponse(url);
            JSONObject[] infoList = AccessFB.extractJSONObjects(response);
            for (JSONObject obj : infoList) {
                String artist = obj.getString("name");
                artists.add(artist);
            }
        } catch (IOException e) {
            System.out.println("Failed to access  user musics"
                    + user.getAccountID());
        }
        return artists;
    }

    public static List<String> getRecentMusicPosts(FacebookUser user) {
        List<String> artists = new ArrayList<>();
        int postCount = Integer.parseInt(
                ConfigParameters.configParameter()
                .getParameter("postCountPerUser"));
        String method = user.getAccountID() + "/Posts";

        try {
            URL url = AccessFB.getURL(user, method, postCount);
            String response = AccessFB.getResponse(url);
            JSONObject[] posts = AccessFB.extractJSONObjects(response);
            for (JSONObject post : posts) {
                if (post.has("story")) {
                    String story = post.getString("story");
                    if (story.contains("listen")) {
                        method = post.getString("id") + "/attachments";
                        url = AccessFB.getURL(user, method, 1);
                        response = AccessFB.getResponse(url);
                        JSONObject postInfo = AccessFB.getObject(response);
                        if (postInfo.has("description")) {
                            artists.add(postInfo.getString("description"));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Extracting post information failed.");
        }
        return artists;
    }

    public static int registerUser(FacebookUser user) {
        user.setAccessToken(AccessFB
                .extenedAccessToken(user.getAccessToken()));
        String sql = "INSERT INTO app_users (user_fb_id,token,"
                + "username,pword,max_track_num,cluster_code,"
                + "ex_date,display_name) VALUES('"
                + user.getAccountID() + "','" + user.getAccessToken() + "','"
                + user.getUsername() + "','" + user.getPassword() + "','"
                + "15" + "','" + user.getClusterID() + "','"
                + "2017-06-25','" + user.getDisplayName() + "');";
        int status = AccessDB.getDBConnection().saveData(sql);
        return status;
    }

    public static FacebookUser getUserObj(String uname, String password) {
        String sql = "SELECT token,user_fb_id,max_track_num,"
                + "cluster_code,display_name "
                + "FROM app_users "
                + "where username='"+uname+"' and pword='"+password+"';";
        FacebookUser loggedUser = null;
        
        try{
            ResultSet rs = AccessDB.getDBConnection().getData(sql);
            while(rs.next()){
                String userName = uname;
                String token = rs.getString("token");
                loggedUser = new FacebookUser(userName, token);
                loggedUser.setAccountID(rs.getString("user_fb_id"));
                loggedUser.setSongListMax(rs.getInt("max_track_num"));
                loggedUser.setClusterID(rs.getInt("cluster_code"));
                loggedUser.setDisplayName(rs.getString("display_name"));
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return loggedUser;
    }
}
