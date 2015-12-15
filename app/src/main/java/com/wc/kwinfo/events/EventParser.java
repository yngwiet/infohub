package com.wc.kwinfo.events;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wang on 2015/10/26.
 */
public class EventParser {

    private static final String TAG = EventParser.class.getSimpleName();

    private static final EventParser outInstance = new EventParser();
    public static EventParser getInstance(){
        return outInstance;
    }
    private EventParser(){}

    public ArrayList<EventItem> parseJsonObject(JSONObject jsonObject){
        ArrayList<EventItem> eventItemArrayList = new ArrayList<>();
//        JSONObject jsonObject;
        JSONArray jsonArray = null;
        try {
//            jsonObject = new JSONObject(jsonObjectStr);
            jsonArray = jsonObject.getJSONArray("data");
            Log.e(TAG, "json array: " + jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray != null){
            Log.e(TAG, "jsonArray length: "+jsonArray.length());
            Calendar calendar = Calendar.getInstance();
            Date curDateTime = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA
            );
            SimpleDateFormat dateFormat1 = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.CANADA
            );
            for (int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    // event filter start; name and start_time are compulsory(no need to check)
                    if (jsonObject1.has("place") && jsonObject1.has("description")){
                        String id = jsonObject1.getString("id");
                        String name = jsonObject1.getString("name");
                        String description = jsonObject1.getString("description");
                        int attendingCount = jsonObject1.getInt("attending_count");
                        int maybeCount = jsonObject1.getInt("maybe_count");
                        int declinedCount = jsonObject1.getInt("declined_count");
                        String startTime = jsonObject1.getString("start_time");
                        String endTime = "end time not available";
                        String coverLink = "";
                        if (jsonObject1.has("cover")){
                            coverLink = jsonObject1.getJSONObject("cover").getString("source");
//                            Log.e(TAG, "coverLink: "+coverLink);
                        }

                        /* date time filter start */
                        // The time zone on the smartphone must be Ametica Eastern Time
                        if (jsonObject1.has("end_time")){// first compare end_time to current time
                            endTime = jsonObject1.getString("end_time");
                            Date eventEndDateTime;
                            if (!endTime.contains("T")){
                                eventEndDateTime = dateFormat1.parse(endTime);
                                if (eventEndDateTime.compareTo(curDateTime) <= 0){// when > 0,
                                                                                  // don't have to adjust format
                                    continue;
                                }
                            } else{
                                eventEndDateTime = dateFormat.parse(endTime);
                                if (eventEndDateTime.compareTo(curDateTime) > 0){
                                    // pass date time filter
                                    String[] split = endTime.split("T");
                                    endTime = split[0] + " " + split[1].substring(0,5);
                                    String[] split1 = startTime.split("T");
                                    startTime = split1[0] + " " + split1[1].substring(0,5);
                                } else {
                                    continue;
                                }
                            }
                        } else { // no end_time, compare start_time to current time
//                            Log.e(TAG, "start_time: " + startTime);
                            Date eventStartDateTime;
                            if (!startTime.contains("T")){
                                eventStartDateTime = dateFormat1.parse(startTime);
                                if (eventStartDateTime.compareTo(curDateTime) <= 0){ // when > 0,
                                                                                     // don't have to adjust format
                                    continue;
                                }
                            } else {
                                eventStartDateTime = dateFormat.parse(startTime);
//                            Log.e(TAG, "start_time after format: "+eventStartDateTime.toString());
                                if (eventStartDateTime.compareTo(curDateTime) > 0){
                                    // pass date time filter
                                    String[] split = startTime.split("T");
                                    startTime = split[0] + " " + split[1].substring(0,5);
                                } else {
                                    continue;
                                }
                            }
                        }
                        /* date time filter end */
                        Log.e(TAG, "start: "+startTime+" end: "+endTime);

                        /* place filter start*/
                        JSONObject place = jsonObject1.getJSONObject("place");
                        // name is compulsory
                        String location = place.getString("name") + "\n";
                        if (place.has("location")){
                            JSONObject loc = place.getJSONObject("location");
                            if (loc.has("street")){
                                location += loc.getString("street");
                            }
                            if (loc.has("city")){
                                location += ", " + loc.getString("city");
                            }
                            if (loc.has("state")){
                                location += ", " + loc.getString("state");
                            }
                            if (loc.has("country")){
                                location += ", " + loc.getString("country");
                            }
                            if (loc.has("zip")){
                                location += ", " + loc.getString("zip");
                            }
                        }
                        /* place filter end */

                        // add the filtered EventItem into ArrayList
                        eventItemArrayList.add(
                            new EventItem(id, name, location, description, coverLink, startTime, endTime, attendingCount
                            , maybeCount, declinedCount)
                        );

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // sort EventItem ArrayList
            Collections.sort(eventItemArrayList);

        }
        return eventItemArrayList;
    }


}
