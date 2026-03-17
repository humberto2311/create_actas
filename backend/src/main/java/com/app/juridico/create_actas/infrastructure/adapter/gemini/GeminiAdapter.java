package com.app.juridico.create_actas.infrastructure.adapter.gemini;

import com.app.juridico.create_actas.domain.services.GeminiService;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeminiAdapter implements GeminiService {

    private final String apiKey = "";

    // Cambiado a gemini-pro que es más estable
    // Cambia esta línea:
    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

    @Override
    public String askQuestion(String prompt) {
        try {
            // Escapar comillas en el prompt para evitar JSON malformado
            String escapedPrompt = prompt.replace("\"", "\\\"");
            String jsonBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + escapedPrompt + "\"}]}]}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            System.out.println("Enviando petición a Gemini...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseTextResponse(response.body());
            } else {
                return "Error de API (" + response.statusCode() + "): " + response.body();
            }
        } catch (Exception e) {
            return "Error de conexión: " + e.getMessage();
        }
    }

    private String parseTextResponse(String body) {
        try {
            // Mejorar el parsing para manejar diferentes formatos de respuesta
            if (body.contains("\"text\":\"")) {
                int start = body.indexOf("\"text\":\"") + 8;
                int end = body.indexOf("\"", start);
                return body.substring(start, end).replace("\\n", "\n");
            } else if (body.contains("\"text\": \"")) {
                int start = body.indexOf("\"text\": \"") + 9;
                int end = body.indexOf("\"", start);
                return body.substring(start, end).replace("\\n", "\n");
            } else {
                return "Respuesta: " + body.substring(0, Math.min(200, body.length()));
            }
        } catch (Exception e) {
            return "Error parseando respuesta: " + body.substring(0, Math.min(100, body.length()));
        }
    }
}