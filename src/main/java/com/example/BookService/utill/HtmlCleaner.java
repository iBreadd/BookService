package com.example.BookService.utill;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

public final class HtmlCleaner {
    private HtmlCleaner(){}

    public static String cleanedHtml(String html) {
        var document = Jsoup.parse(stripIllegalChars(html), "UTF-8");
        document.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + document.html();
    }

    /**
     * XML 1.0 valid character ranges
     * #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
     */
    private static String stripIllegalChars(String html) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < html.length(); i++) {
            int codePoint = html.codePointAt(i);
            if (codePoint > 0xFFFF) {
                i++;
            }
            if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                    || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                    || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                    || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
                sb.appendCodePoint(codePoint);
            }
        }
        return sb.toString();
    }
}
