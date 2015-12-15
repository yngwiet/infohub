package com.wc.kwinfo;

import android.support.test.runner.AndroidJUnit4;

import com.wc.kwinfo.news.RSSReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by wang on 2015/11/1.
 */
@RunWith(AndroidJUnit4.class)
public class RSSReaderAndroidUnitTest {
    private RSSReader rssReader;
    private List<RSSReader.Item> expectedNewsList;
    private String testUrl;
    private String testUrl2;

    @Before
    public void setUp(){
        rssReader = RSSReader.getInstance();
        expectedNewsList = new ArrayList<>();
        testUrl = "https://ece.uwaterloo.ca/~x569wang/test.xml";
        expectedNewsList.add(new RSSReader.Item("Max's website", "https://ece.uwaterloo.ca/~x569wang/",
                "Hello. This is Max's personal website. Currently I don't have pictures on my website.",
                null));
        testUrl2 = "https://ece.uwaterloo.ca/~x569wang/test2.xml";
    }

    @Test
    public void testRSSReaderWithValidRSSFeed(){
        List<RSSReader.Item> actualNewsList = rssReader.start(testUrl);
        assertThat(actualNewsList.get(0).title, is(expectedNewsList.get(0).title));
        assertThat(actualNewsList.get(0).link, is(expectedNewsList.get(0).link));
        assertThat(actualNewsList.get(0).description, is(expectedNewsList.get(0).description));
        assertThat(actualNewsList.get(0).imgLink, is(expectedNewsList.get(0).imgLink));
    }

    @Test
    public void testRSSReaderWithInvalidRSSFeed(){
        List<RSSReader.Item> actualNewsList = rssReader.start(testUrl2);
        assertEquals(actualNewsList.size(), 0);
    }
}
