package org.custombrowser.jsparser;

import org.graalvm.polyglot.Context;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsParser {

    private Context context;

    public JsParser() {
        // Initialize the GraalVM JavaScript context
        // We allow host access so JS can interact with Java objects (like the DOM)
        this.context = Context.newBuilder("js")
                              .allowHostAccess(org.graalvm.polyglot.HostAccess.ALL)
                              .build();
    }

    public void parseAndExecute(Document dom) {
        if (dom == null) return;

        // Expose the Jsoup Document object to the JavaScript context as 'document'
        context.getBindings("js").putMember("document", dom);

        // Inject Polyfills for common Browser Web APIs
        String polyfills = 
            "window = globalThis;\n" +
            "window.document = document;\n" +
            "navigator = {\n" +
            "    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'\n" +
            "};\n" +
            "if (!document.querySelector) {\n" +
            "    document.querySelector = function(selector) { return document.selectFirst(selector); };\n" +
            "}\n" +
            "if (!document.querySelectorAll) {\n" +
            "    document.querySelectorAll = function(selector) { return document.select(selector); };\n" +
            "}\n" +
            "window.setTimeout = function(fn, delay) { try { fn(); } catch(e) {} };\n" +
            "window.clearTimeout = function(id) { };\n";
        try {
            context.eval("js", polyfills);
            System.out.println("Injected JS Polyfills successfully.");
        } catch (Exception e) {
            System.err.println("Failed to inject Polyfills: " + e.getMessage());
        }

        // Find all <script> tags in the DOM
        Elements scripts = dom.getElementsByTag("script");

        for (Element script : scripts) {
            String scriptContent = script.html();
            String src = script.attr("src");

            if (!src.isEmpty()) {
                // TODO: Fetch external script via NetworkManager
                System.out.println("Found external script: " + src + " (fetching not yet implemented)");
            } else if (!scriptContent.trim().isEmpty()) {
                System.out.println("Executing inline JavaScript...");
                try {
                    // Evaluate the JavaScript
                    context.eval("js", scriptContent);
                } catch (Exception e) {
                    System.err.println("JavaScript execution error: " + e.getMessage());
                }
            }
        }
    }
    
    public void close() {
        if (context != null) {
            context.close();
        }
    }
}
