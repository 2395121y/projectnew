// File: UrlExtractor.java
// Original Source Code: https://github.com/shekhargulati/urlcleaner
// Author: Shawn Yeng Wei Xen (2395121Y)
// This file assists for unshortening the URL.

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface UrlExtractor {

    Pattern URL_PATTERN = Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE);

    static List<String> extractUrls(final String text) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            urls.add(text.substring(matcher.start(0), matcher.end(0)));
        }
        return urls;
    }
}
