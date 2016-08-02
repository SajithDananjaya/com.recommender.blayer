/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectModels;

import java.util.HashMap;

/**
 *
 * @author Sajith
 */
public abstract class User {

    private String username;
    private HashMap<Tag, Integer> musicTaste;

    public User(String username) {
        this.username = username;
        musicTaste = new HashMap<>();
    }

    public void addTag(Tag t) {
        if (this.musicTaste.containsKey(t)) {
            int currentCount = musicTaste.get(t);
            currentCount++;
            this.musicTaste.replace(t, currentCount);
        } else {
            this.musicTaste.put(t, 1);
        }
    }

    public HashMap<Tag, Integer> getMusicTaste() {
        return this.musicTaste;
    }

    public int getSpiecificTagCount(Tag t) {
        if (this.musicTaste.containsKey(t)) {
            return this.musicTaste.get(t);
        }
        return 0;
    }
}
