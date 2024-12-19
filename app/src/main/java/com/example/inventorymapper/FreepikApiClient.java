package com.example.inventorymapper;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FreepikApiClient {

    private static final String API_KEY = "FPSXfd86550720924b33ad5236cd9cd01137";
    private static final String DOWNLOAD_URL = "https://api.freepik.com/v1/icons";

    public static void main(String[] args) {
        int iconId = 123456; // Replace with the actual icon ID
        String format = "png"; // Desired format: png or svg
        int size = 512; // Desired size for PNG format
        try {
            String downloadLink = getDownloadLink(iconId, format, size);
            downloadImage(downloadLink, "icon." + format);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDownloadLink(int iconId, String format, int size) throws IOException {
        String url = DOWNLOAD_URL + "/" + iconId + "/download?format=" + format + "&png_size=" + size;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("x-freepik-api-key", API_KEY);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonResponse);
                return rootNode.path("data").path("url").asText();
            }
        }
    }

    public static void downloadImage(String downloadUrl, String outputFileName) throws IOException {
        try (InputStream in = new URL(downloadUrl).openStream();
             FileOutputStream out = new FileOutputStream(outputFileName)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, bytesRead);
            }
        }
    }
}