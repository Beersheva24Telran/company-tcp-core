package telran.net;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class HttpCustomClient implements NetworkClient{
    private String baseUri;
    static private HttpClient client = HttpClient.newHttpClient();
    public HttpCustomClient(String host, int port) {
        baseUri = String.format("http://%s:%d/",host, port);
    }
    @Override
    public String sendAndReceive(String requestType, String requestData) {
        String uri = baseUri + requestType;
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(uri)).POST(BodyPublishers.ofString(requestData)).build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if(response.statusCode() >= 400) {
                throw new Exception(response.body());
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
