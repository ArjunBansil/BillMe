package data_objects;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Arjun Bansil on 5/19/2017.
 */

public class Bill implements Serializable {
    private String chamber, leg_day, string_url, consideration, description;

    public Bill(String chamber, String leg_day, String consideration, String description, String string_url){
        this.chamber = chamber;
        this.leg_day = leg_day;
        this.consideration = consideration;
        this.description = description;
        this.string_url = string_url;
    }

    public String getChamber(){
        return chamber;
    }

    public String getLegDay(){
        return leg_day;
    }

    public String getConsideration(){
        return consideration;
    }

    public String getDescription(){
        return description;
    }

    public String getURL(){
        return string_url;
    }


}
