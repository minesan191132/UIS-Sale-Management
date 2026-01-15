package org.example.controller;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.example.util.MultipartBodyPublisher;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class fxcontroller {
    @FXML
    private void handleImport() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = chooser.showOpenDialog(null);
        if (file != null) {
            uploadToBackend(file);
        }
    }

    private void uploadToBackend(File file) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String boundary = "----JavaFXBoundary";
            byte[] body = MultipartBodyPublisher.build(file, boundary);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/orders/import"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
