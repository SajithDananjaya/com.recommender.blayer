/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectModels;

/**
 *
 * @author Sajith
 */
public class Song {

    private String mbid;
    private String artistName;
    private String trackName;
    private String imageURL;
    private String trackURL;

    public Song() {
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTrackURL() {
        return trackURL;
    }

    public void setTrackURL(String trackURL) {
        this.trackURL = trackURL;
    }
    
    public String toString(){
        return "["+this.artistName+" : "+this.trackName+"]";
    }

}
