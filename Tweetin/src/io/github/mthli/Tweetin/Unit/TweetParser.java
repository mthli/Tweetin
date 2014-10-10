package io.github.mthli.Tweetin.Unit;

import com.twitter.Extractor;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TweetParser {

    public static List<String> getTweetShortURLs(String text) {
        Extractor extractor = new Extractor();

        return extractor.extractURLs(text);
    }

    public static List<String> getTweetExpandURLs(List<String> shortURLs) {
        List<String> expandURLs = new ArrayList<String>();
        for (String shortURL :shortURLs) {
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        new URL(shortURL).openConnection();
                connection.setInstanceFollowRedirects(false);
                String expandURL = connection.getHeaderField("Location");
                connection.disconnect();
                expandURLs.add(expandURL);
            } catch (Exception e) {
                /* Do nothing */
            }
        }

        return expandURLs;
    }
}
