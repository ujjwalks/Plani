package in.co.murs.plani.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ujjwal on 7/11/2016.
 */
public class Event implements Serializable, Comparable<Event>{
    private long _id;
    private String title;
    private String description;
    private long startTime;
    private long endTime;
    private Address location;
    private List<Note> notes;

    public Event(){}

    public Event(String title, String description, long startTime, long endTime, Address location) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public int compareTo(Event event) {
        if(this.startTime < event.getStartTime())
            return 1;
        else if (this.startTime == event.getStartTime())
            return 0;
        else
            return -1;
    }
}
