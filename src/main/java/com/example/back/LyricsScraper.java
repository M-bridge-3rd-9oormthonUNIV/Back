package com.example.back;

import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

//가사 스크래핑
@Service
public class LyricsScraper {
    private static final Pattern REMOVE_SECTION_PATTERN = Pattern.compile("\\[.*?\\]");

    public String scrapeLyrics(String lyricsUrl) {
        try {
            Document doc = Jsoup.connect(lyricsUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .timeout(10000)
                .get();

            Elements lyricsElements = doc.select("div[data-lyrics-container=true]");
            lyricsElements.select("a, span, div").remove();

            //정제
            String lyrics = lyricsElements.html().replaceAll(REMOVE_SECTION_PATTERN.pattern(), "").trim();
            lyrics = lyrics.replaceAll("(?i)(<br>\\s*){1,}", "<br>").trim();
            lyrics = lyrics.replaceFirst("(?i)^<br\\s*/?>", "");

            return lyrics;
        } catch (Exception e) {
            e.printStackTrace();
            return "Could not fetch lyrics.";
        }
    }
}
