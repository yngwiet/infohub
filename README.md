# infohub
An information (news & events) platform on Android platform that helps people get latest news and events according to their perference easily and conveniently. 

The data source for news is from CBC News RSS feed, which is then parsed by Android XmlPullParser. The app fetches events info based on search key using Facebook Graph API, the received data is in JSON format, for which I made a filter to filter out invalid events and sort valid events chronologically.

Apart from browsing information, the app allows users to set news categories preference, search for the events and have their own account to manage the info they are interested in.
