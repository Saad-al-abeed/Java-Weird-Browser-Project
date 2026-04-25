package org.custombrowser.network;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class NetworkManager {

    private HttpClient httpClient;

    public CompletableFuture<String> fetchPage(String rawUrl) {
        //Input Validation & Sanitization
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            System.err.println("Error: URL cannot be null or empty.");
            return CompletableFuture.completedFuture(null);
        }

        if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            rawUrl = "https://" + rawUrl;
        } // appending protocol prefix if absent

        //String to URI Conversion
        URI uri;
        try {
            uri = new URI(rawUrl);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL Format");
            return CompletableFuture.completedFuture(null);
        }

        //Client Initialization (Lazy Loading)
        if (httpClient == null) {
            httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }

        //Request Construction
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .GET()
                .build();

        //The Asynchronous Execution Pipeline
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    // Pipeline Stage A & B (Success & Consume)
                    System.out.println("Status: " + response.statusCode());
                    return response.body();
                })
                .exceptionally(ex -> {
                    // Pipeline Stage C (Error Handling)
                    System.err.println("Network connection failed: " + ex.getMessage());
                    return null;
                });
    }
}
