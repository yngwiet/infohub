package com.wc.kwinfo.news;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2015/10/18.
 */
public class RSSReader {
    public static class Item{
        public final String title;
        public final String link;
        public final String description;
        public final String imgLink;

        public Item(String title, String link, String description, String imgLink) {
            this.title = title;
            this.link = link;
            this.description = description;
            this.imgLink = imgLink;
        }
    }

    private static final RSSReader ourInstance = new RSSReader();

    public static RSSReader getInstance() {
        return ourInstance;
    }

    private RSSReader() {
    }

    private final String TAG = RSSReader.class.getSimpleName();

    public List<Item> start(String urlString){
        List<Item> newsList = new ArrayList();
        InputStream inputStream = null;
        try {
            inputStream = downloadUrl(urlString);
            newsList = parse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newsList;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    private List<Item> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Item> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Item> items = new ArrayList();
        parser.require(XmlPullParser.START_TAG, null, "rss"); // first start tag begin with <rss>
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "channel");// second is <channel>
        while (parser.next() != XmlPullParser.END_TAG) { // if encounter </channel>, stop
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d(TAG, "start tag: " + name);
            // Starts by looking for the item tag
            if (name.equals("item")) {
                items.add(readItem(parser));
            } else {
                skipTag(parser);
            }
        }
        return items;
    }

    private Item readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        String title = null;
        String link = null;
        String description = null;
        String imgLink = null;
        parser.require(XmlPullParser.START_TAG, null, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else if (name.equals("description")) {
                String[] desResult = readDes(parser);
                description = desResult[0];
                imgLink = desResult[1];
//                Log.d(TAG, imgLink);
            } else {
                skipTag(parser);
            }
        }
        return new Item(title, link, description, imgLink);
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = extractText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "link");
        String link = extractText(parser);
        parser.require(XmlPullParser.END_TAG, null, "link");
        Log.d(TAG, "link: "+link);
        return link;
    }

    private String[] readDes(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "description");
        Log.d(TAG, "readDes");
        String[] des = new String[2];
        String desText = extractText(parser);
        String newDesText = "";
        if (desText.contains("<p>")){
            Log.d(TAG, "contains <p>");
            String[] split = new String[2];
            split = desText.split("<p>");
            String temp = split[1];
            Log.d(TAG, temp);
            if (temp.contains("</p>")){
                Log.d(TAG, "contains </p>");
                String[] nextSplit = new String[2];
                nextSplit = temp.split("</p>");
                newDesText = nextSplit[0];
                parser.require(XmlPullParser.END_TAG, null, "description");
                des[0] = newDesText;
            }
        } else{
            des[0] = desText;
        }
        if (desText.contains("src")){
            Log.d(TAG, "contains img src");
            int startIndex = desText.indexOf("src");
            int endIndex = desText.indexOf("/>");
            String link = desText.substring(startIndex+5, endIndex-2); // use endIndex - 2 may not be safe
            Log.e(TAG, "link: "+link);
            des[1] = link;
        } else{
            des[1] = null;
        }
        parser.require(XmlPullParser.END_TAG, null, "description");
        return  des;
    }

    private String extractText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = null;
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.nextTag();
        }
        return text;
    }

    private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
