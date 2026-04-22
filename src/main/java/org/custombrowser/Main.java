package org.custombrowser;

import java.util.Scanner;

import org.custombrowser.domparser.DomParser;
import org.custombrowser.network.NetworkManager;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        NetworkManager networkManager = new NetworkManager();
        DomParser domParser = new DomParser();

        // Example usage: Fetching a page
        String url;
        System.out.println("Enter a url:");
        url = scanner.nextLine();
        System.out.println("Fetching: " + url);
        
        networkManager.fetchPage(url)
                .thenAccept(html -> {
                    if (html != null) {
                        domParser.parse(html); // Parses only when HTML string is ready
                    }
                }).join();

        scanner.close(); // closes and deallocates the scanner object
    }

}