/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectModels;

import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Sajith
 */
public abstract class User {

    private String username;
    private HashMap<Tag, Integer> musicTaste;
    
    public User(){
        musicTaste = new HashMap<>();
    }

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

    public void filterTaste() {
        HashMap<Tag, Integer> tempTasteMap = new HashMap<>();
        for (Tag t : this.musicTaste.keySet()) {
            int tagCount = this.musicTaste.get(t);
            if (tagCount > 2) {
                tempTasteMap.put(t, tagCount);
            }
        }
        this.musicTaste = tempTasteMap;
    }

    public String getTasteString(int totalTagCount) {
        String[] tasteArray = new String[totalTagCount];
        Arrays.fill(tasteArray, "0");
        for (Tag t : musicTaste.keySet()) {
            tasteArray[t.getID() - 1] = musicTaste.get(t) + "";
        }
        return arrayToString(tasteArray);
    }

    private String arrayToString(String[] tasteArray) {
        String array = "";
        for (String s : tasteArray) {
            array = array + "," + s;
        }
        return array;
    }

    @Override
    public String toString() {
        return "[" + this.getUsername() + "]";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
