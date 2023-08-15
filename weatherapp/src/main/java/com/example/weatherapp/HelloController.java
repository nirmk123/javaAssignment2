package com.example.weatherapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class HelloController {

    private static final String API_KEY = "EX4FUW3WHZYN7JTPMMYBXHB8B";
    private static final String BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";

    @FXML
    private TextField cityInput;

    @FXML
    private Button fetchButton;

    @FXML
    private Label temperatureOutput;

    @FXML
    private Label humidityOutput;

    @FXML
    private Label weatherOutput;

    // Add ImageView for weather icons (not implemented in this example)
    // @FXML
    // private ImageView weatherImage;

    @FXML
    public void initialize() {
        fetchButton.setOnAction(event -> {
            String cityName = cityInput.getText();
            try {
                fetchWeatherData(cityName);
            } catch (Exception e) {
                weatherOutput.setText("Error fetching data: " + e.getMessage());
            }
        });
    }

    private void fetchWeatherData(String cityName) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BASE_URL + cityName + "?key=" + API_KEY).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                handleErrorResponse(response.code());
                return;
            }

            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.has("days")) {
                JSONObject day = jsonResponse.getJSONArray("days").getJSONObject(0);
                double tempMin = day.getDouble("tempmin");
                double tempMax = day.getDouble("tempmax");
                double humidity = day.getDouble("humidity");

                temperatureOutput.setText(String.format("Temperature: %.2f°C - %.2f°C", tempMin, tempMax));
                humidityOutput.setText(String.format("Humidity: %.2f%%", humidity));
                weatherOutput.setText(jsonResponse.getString("description"));
                // Add code here to change the weatherImage based on the weather conditions
            } else {
                weatherOutput.setText("Weather data not available for the specified location.");
            }
        }
    }

    private void handleErrorResponse(int code) {
        switch (code) {
            case 404:
                weatherOutput.setText("City not found or API endpoint has changed.");
                break;
            case 401:
                weatherOutput.setText("Unauthorized request. Check your API key.");
                break;
            default:
                weatherOutput.setText("Error fetching data with code: " + code);
                break;
        }
    }
}
