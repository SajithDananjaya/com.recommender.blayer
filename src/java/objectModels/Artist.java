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
public class Artist {

    private String name;
    private List<Tag> tagList;

    public Artist(String name) {
        this.name = name;
        tagList = new ArrayList<>();
        tagList.add(new Tag(0, "6521432"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void addTag(Tag tag) {
        if (!tagList.contains(tag)) {
            this.tagList.add(tag);
        }
    }
    
    public void addTagSet(List<Tag> tagSet){
        if(tagSet.size() >0){
            tagList.addAll(tagSet);
        }
    }

    @Override
    public String toString() {
        return "Artist: " + this.name + " " + this.tagList;
    }

}
