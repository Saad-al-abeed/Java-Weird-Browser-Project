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
import org.custombrowser.cssomparser.CssomParser;
import org.custombrowser.network.NetworkManager;
import org.jsoup.nodes.Document;
import com.helger.css.decl.CascadingStyleSheet;
import java.util.List;

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
        CssomParser cssomParser = new CssomParser();

        TextField addressBar = new TextField();
        
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        VBox vBox = new VBox(addressBar, webView); // stacks the addressbar and webview together
        VBox.setVgrow(webView, Priority.ALWAYS); // Ensures the webview fills the remaining window space
        

        // page-load pipeline (follows the standard Critical Rendering Path):
        //   Step 1: Fetch   — download raw HTML over the network
        //   Step 2: DOM     — parse the HTML into a DOM tree
        //   Step 3: CSSOM   — extract <style> blocks from the DOM and parse into CSSOM
        //   Step 4: JS      — (TODO) extract <script> blocks and execute JavaScript
        //   Render          — hand the result to the WebEngine for display

        addressBar.setOnAction(event -> {
            String url = addressBar.getText();
            webEngine.loadContent("<h2>Fetching: " + url + "...</h2>");
            
            // step 1: Fetch
            networkManager.fetchPage(url)
                .thenAccept(html -> {
                    if (html == null) {
                        Platform.runLater(() -> webEngine.loadContent("<h2>Error: Failed to fetch the page.</h2>"));
                        return;
                    }

                    try {
                        // step 2: Build DOM Tree
                        Document dom = domParser.parse(html);
                        if (dom == null) {
                            Platform.runLater(() -> webEngine.loadContent("<h2>Error: Could not parse the HTML.</h2>"));
                            return;
                        }

                        // step 3: Build CSSOM Tree (extracted from the DOM)
                        List<CascadingStyleSheet> cssom = cssomParser.parse(dom);

                        // step 4: Execute JavaScript (TODO)
                        // Extract <script> blocks from the DOM, parse and execute them.
                        // JS can modify both the DOM and CSSOM, so it runs after both are built.

                        // Render
                        Platform.runLater(() -> {
                            System.out.println("DOM + CSSOM ready. Rendering page...");
                            System.out.println("  DOM:   " + dom.getAllElements().size() + " elements");
                            System.out.println("  CSSOM: " + cssom.size() + " stylesheets");
                            System.out.println("  JS:    not yet implemented");
                            webEngine.loadContent(dom.outerHtml());
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> webEngine.loadContent("<h2>Error parsing page: " + e.getMessage() + "</h2>"));
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
