/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectModels;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sajith
 */
public class LastFMUser extends User {

    private List<Song> likedSongs;

    public LastFMUser(String username) {
        super(username);
        likedSongs = new ArrayList<>();
    }

    public List<Song> getSongList() {
        return this.likedSongs;
    }

    public void addSong(Song song) {
        likedSongs.add(song);
    }

}
