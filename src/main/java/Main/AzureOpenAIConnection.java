package Main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.message.Message;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AzureOpenAIConnection {
    public String azureOpenAIConnection(String diff_patches) {
        String endpointUrl = "https://sdlcopenai.openai.azure.com/openai/deployments/GEN_AI_SDLC_Model/chat/completions?api-version=2023-05-15";        
        String apiKey = "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";
        String codeReview="";

        try {
            
            String reviewCodeSystemPrompt = "You are an expert software engineer. Below is the code updated in a Git pull request, perform a code review. Provide constructive feedback and identify potential issues or improvements in the code. Consider aspects such as code style, best practices, logic errors, and any potential security concerns Additionally, suggest ways to enhance code readability, maintainability, and efficiency.";
            String processedUserPrompt = diff_patches;

            
            Gson gson = new Gson();	             	   
            // Construct the JSON payload
            JsonObject promptObject = new JsonObject();
            promptObject.addProperty("role", "system");
            promptObject.addProperty("content", reviewCodeSystemPrompt);
            JsonObject userPromptObject = new JsonObject();
            userPromptObject.addProperty("role", "user");
            userPromptObject.addProperty("content", processedUserPrompt);
            JsonObject jsonPayload = new JsonObject();
            jsonPayload.add("messages", new Gson().toJsonTree(new JsonObject[]{promptObject, userPromptObject}));

            
            
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set request method
            connection.setRequestMethod("POST");
            
            // Set request headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("api-key", apiKey);
            
            // Enable output and set request body
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            //outputStream.write(jsonPayload.getBytes());
            outputStream.write(jsonPayload.toString().getBytes());
            outputStream.flush();
            outputStream.close();
            
            // Fetch response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // Fetch response body
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            
            String line;
            StringBuilder responseBody = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();
            
            // Parse response body and extract code review
            JSONObject jsonObject = new JSONObject(responseBody.toString());
            JSONArray choicesArray = jsonObject.getJSONArray("choices");
            if (choicesArray.length() > 0) {
                JSONObject choice = choicesArray.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                codeReview = message.getString("content");                
                System.out.println("Code Review: " + codeReview);                
            } else {
                System.out.println("No code review available.");
                codeReview="No Code review available";
                
            }
            //System.out.println("Response Body: " + responseBody.toString());
            
            // Disconnect the connection
            connection.disconnect();
            return codeReview;
        } catch (Exception e) {
            e.printStackTrace();
            return codeReview;
        }    	     }
}
