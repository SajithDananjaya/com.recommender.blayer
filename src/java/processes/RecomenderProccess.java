/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

import objectModels.User;
import objectModels.FacebookUser;
import objectModels.LastFMUser;
import datahandlers.FacebookDataHandler;
import datahandlers.LastFMDataHandler;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import objectModels.Tag;
import objectModels.Song;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Sajith
 */
public class RecomenderProccess {

    private static ClusterProcess clusters = new ClusterProcess();
    private static final Logger LOGGER = AppLogger
            .getNewLogger(RecomenderProccess.class.getName());
    private static boolean systemInitiated = false;

    public static boolean initateApplication() {
        systemInitiated = false;
        try {
            LOGGER.log(Level.INFO, "Initiating proccess started");
            LastFMDataHandler.initiateUsers();
//        LastFMDataHandler.buildDataSheet();
            String dataPath = ConfigParameters
                    .configParameter().getParameter("dataSetFilePath");
            clusters = new ClusterProcess();
            clusters.buildGraph(dataPath);
            LOGGER.log(Level.INFO, "Initiating finalized");
            systemInitiated = true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "System initiation falied");
        }
        return systemInitiated;
    }

    public static boolean registerUser(FacebookUser user) {
        FacebookUser tempUser = FacebookDataHandler.initiateUserTaste(user);
        int userClusterID = clusters.getClusterID(tempUser);
        tempUser.setClusterID(userClusterID);
        int status = FacebookDataHandler.registerUser(tempUser);
        boolean saved = false;
        if (status == 0) {
            saved = true;
        }
        return saved;
    }

    public static List<Song> getSongList(FacebookUser user) {
        FacebookUser tempUser = FacebookDataHandler.initiateUserTaste(user);
        String[] userIDs = clusters.getRelatedUsersID(tempUser);
        List<User> userList = new ArrayList<>();
        for (String id : userIDs) {
            userList.add(LastFMDataHandler.getInitiatedUser(id));
        }
        int userSongCount = user.getSongListMax();
        List<Song> songSet = getBestMatchedSongs(userList, tempUser);
        System.out.println(songSet.size()+"  Songs available");
        if (songSet.size() <= userSongCount) {
            return songSet;
        }
        return mixSongs(songSet, userSongCount);
    }

    private static List<Song> mixSongs(List<Song> songSet, int songCount) {
        int totalSongCount = songSet.size();
        List<Song> newList = new ArrayList<>();
        Random r = new Random();
        for (int addedSongs = 0; addedSongs < songCount;) {
            int index = r.nextInt(totalSongCount);
            Song tempSong = songSet.get(index);
            if (!newList.contains(tempSong)) {
                newList.add(tempSong);
                addedSongs++;
            }
        }
        return newList;
    }

    private static List<Song> getBestMatchedSongs(List<User> clusterUsers,
            User targetUser) {
        List<Song> songList = new ArrayList<>();
        HashMap<User, Double> userInfo = new HashMap<>();
        for (User user : clusterUsers) {
            double distance = getUserDistance(user, targetUser);
            userInfo.put(user, distance);
        }
        double avarageDistance = getAvarageDistance(userInfo);
        for (User user : userInfo.keySet()) {
            if (userInfo.get(user) >= avarageDistance) {
                LastFMUser tempUser = (LastFMUser) user;
                for (Song song : tempUser.getSongList()) {
                    songList.add(song);
                }
            }
        }
        return songList;
    }

    private static double getUserDistance(User targetUser, User clusterUser) {
        double distnaceScore = 0.0;
        Set<Tag> targetUserTags = targetUser.getMusicTaste().keySet();
        Set<Tag> clusterUserTags = clusterUser.getMusicTaste().keySet();
        List<Tag> commonTags = new ArrayList<>();
        for (Tag t : targetUserTags) {
            if (clusterUserTags.contains(t)) {
                commonTags.add(t);
            }
        }
        if (commonTags.size() > 0) {
            double tagDistance = (double) commonTags.size()
                    / LastFMDataHandler.getCurrentTagID() - 1;
            double tagDistance2 = (double) commonTags.size()
                    / targetUserTags.size();

            double sum = 0.0;
            for (Tag t : commonTags) {
                int tCout = targetUser.getMusicTaste().get(t);
                int cCout = clusterUser.getMusicTaste().get(t);
                int def = tCout - cCout;
                sum = sum + (Math.pow(def, 2));
            }
            distnaceScore = (double) (1 / (1 + sum));
        }
        return distnaceScore;
    }

    private static double getAvarageDistance(HashMap<User, Double> usersInfo) {
        int totalUserCount = usersInfo.size();
        double avarage = 0;
        double distanceSum = 0;
        for (User user : usersInfo.keySet()) {
            distanceSum += usersInfo.get(user);
        }
        return distanceSum / totalUserCount;
    }

    public static boolean systemOnline(){
        return systemInitiated;
    }
}
