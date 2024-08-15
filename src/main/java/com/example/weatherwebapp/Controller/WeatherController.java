package com.example.weatherwebapp.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

@Controller
public class WeatherController {

    private static final String API_KEY = "a66030a74d3efb1ed367423518349b89";

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/getWeather")
    public String getWeather(@RequestParam("city") String city, Model model) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            Scanner scanner = new Scanner(reader);
            StringBuilder responseContent = new StringBuilder();

            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }

            scanner.close();

            // Parse the JSON response
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

            // Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();

            // Temperature
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int temperatureCelsius = (int) (temperatureKelvin - 273.15);

            // Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

            // Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            // Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // Set the data in the model to be accessed in Thymeleaf template
            model.addAttribute("date", date);
            model.addAttribute("city", city);
            model.addAttribute("temperature", temperatureCelsius);
            model.addAttribute("weatherCondition", weatherCondition);
            model.addAttribute("humidity", humidity);
            model.addAttribute("windSpeed", windSpeed);

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "weather";
    }
}
