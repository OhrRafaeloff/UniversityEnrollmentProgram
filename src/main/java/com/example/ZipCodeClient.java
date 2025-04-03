//Ohr Rafaeloff 

package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ZipCodeClient {
    
    // Calling API 
    public static ZipCode getZipCodeInfo(String zip) throws IOException {
        String apiUrl = "https://api.zippopotam.us/us/" + zip;
        
        // Create a URL object using the API URL
        URL url = new URL(apiUrl);

        // Open a connection to the URL and cast it to HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to GET to retrieve information
        connection.setRequestMethod("GET");
        
        StringBuilder response;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            response = new StringBuilder();
            
            // Read the response line by line and append it to the response StringBuilder
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        
        // Create a Gson object to handle JSON parsing
        Gson gson = new Gson();
        // Deserialize the JSON response into a ZipCode object
        return gson.fromJson(response.toString(), ZipCode.class);
    }
    
    // ZipCode and Place classes remain the same
    public static class ZipCode implements Serializable {
        @SerializedName("post code")
        private String postCode;
        private String country;
        @SerializedName("country abbreviation")
        private String countryabbreviation;
        private Place[] places;

        public String getPostCode() {
            return postCode;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryAbbreviation() {
            return countryabbreviation;
        }

        public Place[] getPlaces() {
            return places;
        }

        public static class Place implements Serializable {
            @SerializedName("place name")
            private String placename;
            private String longitude; // Might delete
            private String state;
            @SerializedName("state abbreviation")
            private String stateabbreviation;
            private String latitude; // Might delete

            public String getPlaceName() {
                return placename;
            }

            public String getState() {
                return state;
            }

            public String getStateAbbreviation() {
                return stateabbreviation;
            }
        }
    }
}
