package org.custombrowser.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.custombrowser.domparser.DomParser;
import org.custombrowser.network.NetworkManager;
import org.jsoup.nodes.Document;

public class BrowserWindow extends Application { // Inheriting from Application class

    @Override
    public void stop() throws Exception { // Must override this virtual function
        super.stop();
        System.exit(0); // Forcibly kills all background threads (like HttpClient's pool) when the window is closed
    }

    @Override
    public void start(Stage primaryStage) { // Must override this virtual function

        // Instantiating the required objects
        NetworkManager networkManager = new NetworkManager();
        DomParser domParser = new DomParser();

        TextField addressBar = new TextField();
        
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        VBox vBox = new VBox(addressBar, webView); // stacks the addressbar and webview together
        VBox.setVgrow(webView, Priority.ALWAYS); // Ensures the webview fills the remaining window space
        

        // browser usage pipeline in the event listener
        addressBar.setOnAction(event -> {
            String url = addressBar.getText();
            // Show a simple loading message in the WebView
            webEngine.loadContent("<h2>Fetching: " + url + "...</h2>");
            
            // fetching for the webpage
            networkManager.fetchPage(url)
                .thenAccept(html -> {
                    if (html != null) {
                        try {
                            Document doc = domParser.parse(html);
                            
                            // Hand the result back to the main GUI thread safely
                            Platform.runLater(() -> {
                                if (doc != null) {
                                    // Render the HTML visually using WebEngine!
                                    webEngine.loadContent(doc.outerHtml());
                                } else {
                                    webEngine.loadContent("<h2>Couldn't find anything to write bro</h2>");
                                }
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> webEngine.loadContent("<h2>Error parsing HTML: " + e.getMessage() + "</h2>"));
                        }
                    } else {
                        Platform.runLater(() -> {
                            webEngine.loadContent("<h2>Error: Failed to fetch the page.</h2>");
                        });
                    }
                })
                .exceptionally(ex -> {
                    // Catch any hidden errors in the CompletableFuture chain
                    Platform.runLater(() -> webEngine.loadContent("<h2>Connection/Unexpected Error: " + ex.getMessage() + "</h2>"));
                    return null;
                });
        });
        
        Scene scene = new Scene(vBox, 800, 600); // setting window resolutions
        
        primaryStage.setTitle("Weird Browser");
        primaryStage.setScene(scene);
        
        // Ensure the window triggers the stop() method when the 'X' button is clicked
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
        });
        
        primaryStage.show();
    }
}
