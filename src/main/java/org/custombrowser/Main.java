package org.custombrowser;

import java.util.Scanner;

import org.custombrowser.cssomparser.CssomParser;
import org.custombrowser.domparser.DomParser;
import org.custombrowser.network.NetworkManager;
import org.jsoup.nodes.Document;
import javafx.application.Application;
import org.custombrowser.ui.BrowserWindow;


public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            Scanner scanner = new Scanner(System.in);
            NetworkManager networkManager = new NetworkManager();
            DomParser domParser = new DomParser();
            CssomParser cssomParser = new CssomParser();

            System.out.println("Enter a url:");
            String url = scanner.nextLine();
            System.out.println("Fetching: " + url);
            
            networkManager.fetchPage(url)
                    .thenAccept(html -> {
                        if (html != null) {
                            Document doc = domParser.parse(html);
                            cssomParser.parse(doc);
                        }
                    }).join();

            scanner.close();
        } else {
            // Launch the JavaFX application
            Application.launch(BrowserWindow.class, args);
        }
    }

}