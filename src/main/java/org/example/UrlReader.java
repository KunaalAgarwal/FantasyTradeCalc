package org.example;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UrlReader {
    private static BufferedReader brReader;

    public static String readURL() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int text;
        while ((text = brReader.read()) != -1) {
            stringBuilder.append((char) text);
        }
        return stringBuilder.toString();
    }

    public static JSONObject convertToJSON(URL url) throws IOException, JSONException {
        try (InputStream isReader = url.openStream()) {
            brReader = new BufferedReader(new InputStreamReader(isReader, StandardCharsets.UTF_8));
            String jsonText = readURL();
            return new JSONObject(jsonText);
        }
    }
}
