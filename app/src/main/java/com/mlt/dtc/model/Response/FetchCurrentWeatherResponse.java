package com.mlt.dtc.model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public
class FetchCurrentWeatherResponse {

    @SerializedName("fetchCurrentWeatherInfrormationResult")
    @Expose
    private FetchCurrentWeatherInfrormationResult fetchCurrentWeatherInfrormationResult;

    public FetchCurrentWeatherInfrormationResult getFetchCurrentWeatherInfrormationResult() {
        return fetchCurrentWeatherInfrormationResult;
    }

    public void setFetchCurrentWeatherInfrormationResult(FetchCurrentWeatherInfrormationResult fetchCurrentWeatherInfrormationResult) {
        this.fetchCurrentWeatherInfrormationResult = fetchCurrentWeatherInfrormationResult;
    }


    public class FetchCurrentWeatherInfrormationResult {

        @SerializedName("ServiceCode")
        @Expose
        private String serviceCode;
        @SerializedName("ServiceMessage")
        @Expose
        private String serviceMessage;
        @SerializedName("ServiceStatus")
        @Expose
        private String serviceStatus;
        @SerializedName("response")
        @Expose
        private List<Response> response = null;

        public String getServiceCode() {
            return serviceCode;
        }

        public void setServiceCode(String serviceCode) {
            this.serviceCode = serviceCode;
        }

        public String getServiceMessage() {
            return serviceMessage;
        }

        public void setServiceMessage(String serviceMessage) {
            this.serviceMessage = serviceMessage;
        }

        public String getServiceStatus() {
            return serviceStatus;
        }

        public void setServiceStatus(String serviceStatus) {
            this.serviceStatus = serviceStatus;
        }

        public List<Response> getResponse() {
            return response;
        }

        public void setResponse(List<Response> response) {
            this.response = response;
        }

    }

    public class Response {

        @SerializedName("City")
        @Expose
        private String city;
        @SerializedName("Country")
        @Expose
        private String country;
        @SerializedName("Humidity")
        @Expose
        private Integer humidity;
        @SerializedName("Sunrise")
        @Expose
        private String sunrise;
        @SerializedName("Sunset")
        @Expose
        private String sunset;
        @SerializedName("TempMax")
        @Expose
        private Double tempMax;
        @SerializedName("TempMin")
        @Expose
        private Double tempMin;
        @SerializedName("Temperature")
        @Expose
        private Integer temperature;
        @SerializedName("Weather")
        @Expose
        private String weather;
        @SerializedName("WindSpeed")
        @Expose
        private Double windSpeed;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public Double getTempMax() {
            return tempMax;
        }

        public void setTempMax(Double tempMax) {
            this.tempMax = tempMax;
        }

        public Double getTempMin() {
            return tempMin;
        }

        public void setTempMin(Double tempMin) {
            this.tempMin = tempMin;
        }

        public Integer getTemperature() {
            return temperature;
        }

        public void setTemperature(Integer temperature) {
            this.temperature = temperature;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public Double getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(Double windSpeed) {
            this.windSpeed = windSpeed;
        }

    }
}
