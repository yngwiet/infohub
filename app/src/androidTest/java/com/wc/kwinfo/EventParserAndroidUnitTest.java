package com.wc.kwinfo;

import android.support.test.runner.AndroidJUnit4;

import com.wc.kwinfo.events.EventItem;
import com.wc.kwinfo.events.EventParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by wang on 2015/11/1.
 */
@RunWith(AndroidJUnit4.class)
public class EventParserAndroidUnitTest {
    private List<EventItem> expectedEventItemArrayList;
    private EventParser eventParser;
    private JSONObject jsonObject;
    private String jsonStr;

    @Before
    public void setUp(){
        eventParser = EventParser.getInstance();
        jsonStr = "{\"data\":[{\"id\":\"123\", \"name\":\"Test Event\", \"description\":\"This is description\"," +
                "\"start_time\":\"3000-11-14T11:00:00-0500\", \"place\":{\"name\":\"Test Place\", " +
                "\"location\":{\"city\":\"Kitchener\", \"street\":\"425 Bingemans Centre Drive\"," +
                "\"zip\":\"N2B 3X7\"}}, \"attending_count\":1, \"maybe_count\":2, \"declined_count\":3}, " +
                "{\"id\":\"124\", \"name\":\"Test Event2\", \"description\":\"This is description 2\"," +
                "\"start_time\":\"3000-11-11T19:00:00-0500\", \"place\":{\"name\":\"Waterloo\"}, " +
                "\"cover\":{\"source\":\"https://scontent.xx.fbcdn.net/hphotos-xpt1/v/t1.0-9/12074904_7502077784" +
                "24535_8387069140983153837_n.jpg?oh=1183aebce62d22573b3fe42e97c41ece&oe=56B46FB1\"}, " +
                "\"attending_count\":4, \"maybe_count\":2, \"declined_count\":1}]}";
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expectedEventItemArrayList = new ArrayList<>();
        expectedEventItemArrayList.add(new EventItem("124", "Test Event2", "Waterloo"+"\n", "This is description 2",
                "https://scontent.xx.fbcdn.net/hphotos-xpt1/v/t1.0-9/12074904_7502077784" +
                        "24535_8387069140983153837_n.jpg?oh=1183aebce62d22573b3fe42e97c41ece&oe=56B46FB1",
                "3000-11-11 19:00", "end time not available", 4,2,1));
        expectedEventItemArrayList.add(new EventItem("123", "Test Event", "Test Place"+"\n"+"425 Bingemans Centre Drive"+
        ", Kitchener"+", N2B 3X7", "This is description", "", "3000-11-14 11:00", "end time not available", 1,2,3));
    }

    @Test
    public void testEventParser(){
        List<EventItem> actualEventItemArrayList = eventParser.parseJsonObject(jsonObject);
        assertThat(actualEventItemArrayList.get(0).id, is(expectedEventItemArrayList.get(0).id));
        assertThat(actualEventItemArrayList.get(0).name, is(expectedEventItemArrayList.get(0).name));
        assertThat(actualEventItemArrayList.get(0).location, is(expectedEventItemArrayList.get(0).location));
        assertThat(actualEventItemArrayList.get(0).description, is(expectedEventItemArrayList.get(0).description));
        assertThat(actualEventItemArrayList.get(0).coverLink, is(expectedEventItemArrayList.get(0).coverLink));
        assertThat(actualEventItemArrayList.get(0).startTime, is(expectedEventItemArrayList.get(0).startTime));
        assertThat(actualEventItemArrayList.get(0).endTime, is(expectedEventItemArrayList.get(0).endTime));
        assertThat(actualEventItemArrayList.get(0).attendingCount, is(expectedEventItemArrayList.get(0).attendingCount));
        assertThat(actualEventItemArrayList.get(0).maybeCount, is(expectedEventItemArrayList.get(0).maybeCount));
        assertThat(actualEventItemArrayList.get(0).declinedCount, is(expectedEventItemArrayList.get(0).declinedCount));
        assertThat(actualEventItemArrayList.get(1).id, is(expectedEventItemArrayList.get(1).id));
        assertThat(actualEventItemArrayList.get(1).name, is(expectedEventItemArrayList.get(1).name));
        assertThat(actualEventItemArrayList.get(1).location, is(expectedEventItemArrayList.get(1).location));
        assertThat(actualEventItemArrayList.get(1).description, is(expectedEventItemArrayList.get(1).description));
        assertThat(actualEventItemArrayList.get(1).coverLink, is(expectedEventItemArrayList.get(1).coverLink));
        assertThat(actualEventItemArrayList.get(1).startTime, is(expectedEventItemArrayList.get(1).startTime));
        assertThat(actualEventItemArrayList.get(1).endTime, is(expectedEventItemArrayList.get(1).endTime));
        assertThat(actualEventItemArrayList.get(1).attendingCount, is(expectedEventItemArrayList.get(1).attendingCount));
        assertThat(actualEventItemArrayList.get(1).maybeCount, is(expectedEventItemArrayList.get(1).maybeCount));
        assertThat(actualEventItemArrayList.get(1).declinedCount, is(expectedEventItemArrayList.get(1).declinedCount));
    }



}
