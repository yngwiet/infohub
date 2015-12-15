package com.wc.kwinfo.events;

/**
 * Created by wang on 2015/10/26.
 */
public class EventItem implements Comparable<EventItem>{
    public final String id;
    public final String name;
    public final String location;
    public final String description;
    public final String coverLink;
    public final String startTime;
    public final String endTime;
    public final int attendingCount;
    public final int maybeCount;
    public final int declinedCount;

    public EventItem(String id, String name, String location, String description,
                      String coverLink, String startTime, String endTime,
                     int attendingCount, int maybeCount, int declinedCount){
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.coverLink = coverLink;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendingCount = attendingCount;
        this.maybeCount = maybeCount;
        this.declinedCount = declinedCount;
    }

    @Override
    public int compareTo(EventItem another) {
        return this.startTime.compareTo(another.startTime);
    }
}
