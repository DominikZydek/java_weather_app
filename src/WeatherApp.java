import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeather(String locationName) {
        // get coordinates
        JSONArray locationData = getLocation(locationName);

        // extract latitude and longitude
        JSONObject location = (JSONObject)locationData.get(0);
        double lat = (double)location.get("latitude");
        double lon = (double)location.get("longitude");

        // build API request URL
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon +
                           "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try {
            // try calling API
            HttpURLConnection connection = fetchAPIResponse(urlString);

            // check response status (200 - success)
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API.");
                return null;
            } else {
                // store API results
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                // read each line
                while(scanner.hasNext()) {
                    resultJSON.append(scanner.nextLine());
                }

                // close everything !!!
                scanner.close();
                connection.disconnect();

                // parse into an object
                JSONParser parser = new JSONParser();
                JSONObject resultJSONObject = (JSONObject)parser.parse(String.valueOf(resultJSON));

                // get hourly data
                JSONObject hourly = (JSONObject)resultJSONObject.get("hourly");

                // get current hour data
                JSONArray time = (JSONArray)hourly.get("time");
                int index = currentTimeIndex(time);

                // get temperature
                JSONArray temperatureData = (JSONArray)hourly.get("temperature_2m");
                double temperature = (double)temperatureData.get(index);

                // get weather code
                JSONArray weatherCode = (JSONArray)hourly.get("weather_code");
                String weatherCondition = convertWeatherCode((long)weatherCode.get(index));

                // get humidity
                JSONArray relativeHumidity = (JSONArray)hourly.get("relative_humidity_2m");
                long humidity = (long)relativeHumidity.get(index);

                // get windspeed
                JSONArray windspeedData = (JSONArray)hourly.get("wind_speed_10m");
                double windspeed = (double)windspeedData.get(index);

                // build json file for frontend
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);

                return weatherData;
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static JSONArray getLocation(String locationName) {
        // replace space with + (ex. Los Angeles -> Los+Angeles)
        locationName = locationName.replaceAll(" ", "+");

        // build API URL with location
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName +
                           "&count=10&language=en&format=json";

        try {
            // try calling API
            HttpURLConnection connection = fetchAPIResponse(urlString);

            // check response status (200 - success)
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API.");
                return null;
            } else {
                // store API results
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                // read each line
                while(scanner.hasNext()) {
                    resultJSON.append(scanner.nextLine());
                }

                // close everything !!!
                scanner.close();
                connection.disconnect();

                // parse into an object
                JSONParser parser = new JSONParser();
                JSONObject resultsJSONObject = (JSONObject)parser.parse(String.valueOf(resultJSON));

                // get the list of location data
                JSONArray locationData = (JSONArray)resultsJSONObject.get("results");
                return locationData;
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static HttpURLConnection fetchAPIResponse(String urlString) {
        try {
            // try to connect
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            // set request method
            connection.setRequestMethod("GET");

            // connect to API
            connection.connect();
            return connection;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static int currentTimeIndex(JSONArray timeList) {
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to API's format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatter);

        // get matching result from json array
        for(int i = 0; i < timeList.size(); i++) {
            String time = (String)timeList.get(i);
            if (time.equalsIgnoreCase(formattedDateTime)) {
                return i;
            }
        }
        return 0;
    }

    public static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if(weathercode == 0L) {
            weatherCondition = "Clear";
        } else if(weathercode <= 3L && weathercode > 0L) {
            weatherCondition = "Cloudy";
        } else if((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)) {
            weatherCondition = "Rain";
        } else if(weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
