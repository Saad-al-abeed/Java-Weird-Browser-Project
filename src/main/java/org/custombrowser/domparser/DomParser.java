package org.custombrowser.domparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DomParser {

    public Document parse(String html) {
        if (html == null || html.trim().isEmpty()) {
            System.err.println("Error: HTML content is empty.");
            return null;
        }

        // Basic logic to simulate DOM parsing
        System.out.println("Parsing HTML content...");

        Document doc = Jsoup.parse(html); // building the DOM tree

        System.out.println("Title of the HTML: " + doc.title());
        
        return doc;
    }

}
