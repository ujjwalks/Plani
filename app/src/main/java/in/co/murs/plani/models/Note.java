package in.co.murs.plani.models;

import java.io.Serializable;

/**
 * Created by Ujjwal on 7/11/2016.
 */
public class Note implements Serializable{
    private long _id;
    private String title;
    private String description;
    private long time;
    private long event;

    public Note(){}

    public Note(String title, String description, long time, long event) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getEvent() {
        return event;
    }

    public void setEvent(long event) {
        this.event = event;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
