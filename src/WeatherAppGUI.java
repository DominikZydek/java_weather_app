import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI() {
        // gui setup
        this.setTitle("Weather App");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(450, 650);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
        this.addGUIComponents();
    }

    private void addGUIComponents() {
        // text field
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 350, 45);
        searchTextField.setFont(new Font("Lato", Font.PLAIN, 24));
        this.add(searchTextField);

        // weather image
        JLabel weatherImage = new JLabel(new ImageIcon("src/assets/cloudy.png"));
        weatherImage.setBounds(0, 125, 450, 215);
        weatherImage.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(weatherImage);

        // temperature text
        JLabel temperatureText = new JLabel("15 C");
        temperatureText.setBounds(0, 350, 450, 55);
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        temperatureText.setFont(new Font("Lato", Font.BOLD, 48));
        this.add(temperatureText);

        // condition description
        JLabel conditionDesc = new JLabel("Cloudy");
        conditionDesc.setBounds(0, 405, 450, 35);
        conditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        conditionDesc.setFont(new Font("Lato", Font.PLAIN, 32));
        this.add(conditionDesc);

        // humidity image
        JLabel humidityImage = new JLabel(new ImageIcon("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 75, 65);
        this.add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html>Humidity<br>100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Lato", Font.PLAIN, 16));
        this.add(humidityText);

        // windspeed image
        JLabel windspeedImage = new JLabel(new ImageIcon("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 75, 65);
        this.add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html>Windspeed<br>20 km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Lato", Font.PLAIN, 16));
        this.add(windspeedText);

        // search button
        JButton searchButton = new JButton(new ImageIcon("src/assets/search.png"));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 15, 45, 45);

        // adding functionality to search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location
                String userInput = searchTextField.getText();

                // validate input
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                // retrieve weather data
                weatherData = WeatherApp.getWeather(userInput);

                // update GUI
                String weatherCondition = (String)weatherData.get("weather_condition");

                switch(weatherCondition) {
                    case "Clear":
                        weatherImage.setIcon(new ImageIcon("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherImage.setIcon(new ImageIcon("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherImage.setIcon(new ImageIcon("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherImage.setIcon(new ImageIcon("src/assets/snow.png"));
                        break;
                }

                double temperature = (double)weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                conditionDesc.setText(weatherCondition);

                long humidity = (long)weatherData.get("humidity");
                humidityText.setText("<html>Humidity<br>" + humidity + "%</html>");

                double windspeed = (double)weatherData.get("windspeed");
                windspeedText.setText("<html>Windspeed<br>" + windspeed + " km/h</html>");

            }
        });

        this.add(searchButton);

    }

}