/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectModels;

import java.util.Date;

/**
 *
 * @author Sajith
 */
public class FacebookUser extends User {

    private String accessToken;
    private int songListMax = 10;
    private String accountID;
    private int clusterID;
    private Date tokenExDate;

    public FacebookUser(String username) {
        super(username);
    }

    public FacebookUser(String username, String accessToken) {
        this(username);
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getSongListMax() {
        return songListMax;
    }

    public void setSongListMax(int songListMax) {
        this.songListMax = songListMax;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

}
