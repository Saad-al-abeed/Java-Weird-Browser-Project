package org.custombrowser;

import java.util.Scanner;

import org.custombrowser.cssomparser.CssomParser;
import org.custombrowser.domparser.DomParser;
import org.custombrowser.network.NetworkManager;
import org.jsoup.nodes.Document;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        NetworkManager networkManager = new NetworkManager();
        DomParser domParser = new DomParser();
        CssomParser cssomParser = new CssomParser();

        // Example usage: Fetching a page
        String url;
        System.out.println("Enter a url:");
        url = scanner.nextLine();
        System.out.println("Fetching: " + url);
        
        networkManager.fetchPage(url)
                .thenAccept(html -> {
                    if (html != null) {
                        Document doc = domParser.parse(html); // Parses only when HTML string is ready
                        cssomParser.parse(doc);
                    }
                }).join();

        scanner.close(); // closes and deallocates the scanner object
    }

}