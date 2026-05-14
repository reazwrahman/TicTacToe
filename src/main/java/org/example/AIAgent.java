package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AIAgent {
    private static final String API_ENDPOINT = "http://0.0.0.0:8000/next_move";

    private static String callAPI(String[][] board, String nextMoveAs) throws IOException {
        // Create payload map
        Map<String, Object> payload = new HashMap<>();
        payload.put("board_config", board);
        payload.put("next_move_as", nextMoveAs);

        // Serialize to JSON
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload);

        // Send HTTP POST request
        URL url = new URL(API_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                return scanner.useDelimiter("\\A").next();
            }
        } else {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }

    public static Map<String, Integer> getNextMove(String[][] board, String nextMoveAs) throws IOException {
        String response = callAPI(board, nextMoveAs);
        Gson gson = new Gson();
        return gson.fromJson(response, new TypeToken<Map<String, Integer>>(){}.getType());
    }
}
