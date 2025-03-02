import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

abstract class getFunctionsImpl {
    protected HttpClient client = HttpClient.newHttpClient();
    protected String receivingInfo(HttpRequest request){
        final String[] output = new String[1];
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    output[0] =response;
                })
                .exceptionally(e -> {
                    System.err.println("Error: " + e.getMessage());
                    return null;
                }).join();
        return output[0];
    }

    protected   HttpRequest requestBuilder(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return request;
    }


}
