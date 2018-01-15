package data_objects;

import java.io.Serializable;

/**
 * Created by Arjun Bansil on 5/19/2017.
 */

public class Bill implements Serializable {
    private String sponsor, leg_day, string_url, subject, title;

    private static String serialVersionUID;

    public Bill(String sponsor, String leg_day, String subject, String title, String string_url){
        this.sponsor = sponsor;
        this.leg_day = leg_day;
        this.subject = subject;
        this.title = title;
        this.string_url = string_url;
        serialVersionUID = sponsor +leg_day+ subject + title +string_url;
    }

    public String getSponsor(){
        return sponsor;
    }

    public String getLegDay(){
        return leg_day;
    }

    public String getSubject(){
        return subject;
    }

    public String getTitle(){
        return title;
    }

    public String getURL(){
        return string_url;
    }

    public boolean equals(Bill b){
        if(!b.getTitle().equals(getTitle())) return false;
        else if(!b.getSponsor().equals(getSponsor())) return false;
        else if(!b.getLegDay().equals(getLegDay())) return false;
        else if(!b.getSubject().equals(getSubject())) return false;
        else if(!b.getURL().equals(getURL())) return false;

        return true;
    }


}
