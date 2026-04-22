package org.custombrowser.domparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

        //Optional code for testing
        Elements hyperlinks = doc.select("a"); // Selects all the hyperlinks in the html & returns an arraylist

        System.out.println("Found " + hyperlinks.size() + " number of hyperlinks in this page");

        System.out.println("Listing them below...");

        for (Element elem : hyperlinks) {
            System.out.println(elem);
        }
        
        return doc;
    }

}
