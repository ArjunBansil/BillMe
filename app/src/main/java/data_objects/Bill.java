package data_objects;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Arjun Bansil on 5/19/2017.
 */

public class Bill implements Serializable {
    private String chamber, leg_day, string_url, consideration, description;

    private static String serialVersionUID;

    public Bill(String chamber, String leg_day, String consideration, String description, String string_url){
        this.chamber = chamber;
        this.leg_day = leg_day;
        this.consideration = consideration;
        this.description = description;
        this.string_url = string_url;
        serialVersionUID = chamber+leg_day+consideration+description+string_url;
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

    public boolean equals(Bill b){
        if(!b.getDescription().equals(getDescription())) return false;
        else if(!b.getChamber().equals(getChamber())) return false;
        else if(!b.getLegDay().equals(getLegDay())) return false;
        else if(!b.getConsideration().equals(getConsideration())) return false;
        else if(!b.getURL().equals(getURL())) return false;

        return true;
    }


}
