package io.github.mthli.Tweetin.Unit;

import com.twitter.Autolink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TextParser {
    private static final String TAG_A = "a";
    private static final String TAG_FONT = "font";
    private static final String ATTR_CLASS = "class";
    private static final String ATTR_HREF = "href";
    private static final String ATTR_REL = "rel";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_COLOR = "color";
    private static final String ATTR_COLOR_VALUE = "#37AAD4";

    public static String getHighlight(String text) {
        Autolink autolink = new Autolink();
        text = autolink.autoLink(text);
        Document document = Jsoup.parse(text);
        Elements elements = document.getElementsByTag(TAG_A);
        elements.removeAttr(ATTR_CLASS);
        elements.removeAttr(ATTR_HREF);
        elements.removeAttr(ATTR_REL);
        elements.removeAttr(ATTR_TITLE);
        elements.tagName(TAG_FONT);
        elements.attr(ATTR_COLOR, ATTR_COLOR_VALUE);

        return document.body().html();
    }
}
