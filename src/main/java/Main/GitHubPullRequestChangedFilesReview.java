package Main;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GitHubPullRequestChangedFilesReview {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final String GITHUB_API_URL = "https://api.github.com";
	public static final String ACCESS_TOKEN = " ";//provide access token
	public static final String OWNER = "Ksandeepgenpact1";
	public static final String REPO = "eclipse-plugin";
	public static final int PULL_NUMBER = 2; // Replace with the actual pull request number

	public static void main(String[] args) {
			boolean mergeable=checkPullRequestMergeability();
			if(mergeable) {			
			reviewPullRequest();
			}
	}

	public static void reviewPullRequest() {
		// Step 1: List pull request files
		JSONArray files = listPullRequestFiles();
	}
			
	    
	     
	     // Fetching the Difference Patches From changed Commit Files.
	public static String fetchDiffPatchesFromPullRequest() {
        String url = GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO + "/pulls/" + PULL_NUMBER + "/files";        
        String accessToken = ACCESS_TOKEN;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github.v3+json")
                .build();
        String codeReview="";
        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();
            String responseBody = response.body().string();
            System.out.println("Response Status Code: " + statusCode);
            System.out.println("Response Body: " + responseBody);

            if (response.isSuccessful()) {
                Gson gson = new Gson();
                PullRequestFile[] files = gson.fromJson(responseBody, PullRequestFile[].class);

                // Process the files and extract the diff patches
                for (PullRequestFile file : files) {
                    String diffPatch = file.getPatch();
                    System.out.println("File: " + file.getFilename());
                    System.out.println("Diff Patch:");
                    System.out.println(diffPatch);
                    System.out.println("---------------");
                    
                    // raising request to the llm Model.
                    AzureOpenAIConnection llmModel= new AzureOpenAIConnection();
                    codeReview=llmModel.azureOpenAIConnection(diffPatch);
                    return codeReview;
                }
            } else {
                System.out.println("Failed to fetch pull request files");
            }
        } catch (IOException e) {
            e.printStackTrace();            
        }		
        return codeReview;
    }

    public static class PullRequestFile {
        private String filename;
        private String patch;

        public String getFilename() {
            return filename;
        }

        public String getPatch() {
            return patch;
        }
    }
	
    
    // Adding Comments to the changed commit files.
	public static void addCommentToChangedFile(String filename, String comment,String commitId) {
        String url = GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO + "/pulls/" + PULL_NUMBER + "/comments";
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String bodyContent = "{\"body\": \"" + comment + "\", \"path\": \"" + filename + "\", \"commit_id\": \"" + commitId + "\", \"position\": 1}";

        @SuppressWarnings("deprecation")
		RequestBody body = RequestBody.create(mediaType, bodyContent);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("Accept", "application/vnd.github.v3+json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();
            String responseBody = response.body().string();
            System.out.println("Response Status Code: " + statusCode);
            System.out.println("Response Body: " + responseBody);

            if (response.isSuccessful()) {
                System.out.println("Comment added successfully");
            } else {
                System.out.println("Failed to add comment");
            }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	
	 public static boolean checkPullRequestMergeability() {
	        String url = GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO + "/pulls/" + PULL_NUMBER;
	        OkHttpClient client = new OkHttpClient();
	        Request request = new Request.Builder()
	                .url(url)
	                .header("Authorization", "Bearer " + ACCESS_TOKEN)
	                .header("Accept", "application/vnd.github.v3+json")
	                .build();

	        try {
	            Response response = client.newCall(request).execute();
	            if (response.isSuccessful()) {
	                String responseBody = response.body().string();
	                JSONObject pullRequest = new JSONObject(responseBody);
	                return pullRequest.getBoolean("mergeable");
	            } else {
	                System.out.println("Error retrieving pull request details.");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return false;
	    }
	 
	public static JSONArray listPullRequestCommits() {
		String url = GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO + "/pulls/" + PULL_NUMBER + "/commits";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + ACCESS_TOKEN)
				.header("Accept", "application/vnd.github.v3+json").build();

		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				String responseBody = response.body().string();
				System.out.println("I retrieved pull request commit");
				return new JSONArray(responseBody);
			} else {
				System.out.println("Error retrieving pull request commits.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new JSONArray();
	}

	public static JSONArray listCommitFiles(String commitId) {
	    String url = GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO + "/commits/" + commitId;
	    OkHttpClient client = new OkHttpClient();
	    Request request = new Request.Builder()
	            .url(url)
	            .header("Authorization", "Bearer " + ACCESS_TOKEN)
	            .header("Accept", "application/vnd.github.v3+json")
	            .build();

	    try {
	        Response response = client.newCall(request).execute();
	        if (response.isSuccessful()) {
	            String responseBody = response.body().string();
	            JSONObject commit = new JSONObject(responseBody);
	            System.out.println("Retrieved the commit files");
	            return commit.getJSONArray("files");
	        } else {
	            System.out.println("Error retrieving commit files.");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return new JSONArray();
	}


	public static JSONArray listPullRequestFiles() {
		JSONArray commits = listPullRequestCommits();
		JSONArray files = new JSONArray();
		AzureOpenAIConnection llmModel= new AzureOpenAIConnection();
		String codeReview="";

		for (int i = 0; i < commits.length(); i++) {
			JSONObject commit = commits.getJSONObject(i);
			String commitId = commit.getString("sha");
			System.out.println(commitId);
			JSONArray commitFiles = listCommitFiles(commitId);
		
			
			files.putAll(commitFiles);
			//Trying comment addition to changed files
			for (int j = 0; j < files.length(); j++) {
				JSONObject file = files.getJSONObject(j);				 				        
				String filename = file.getString("filename");
				System.out.println(filename);			
				String diffPatch = file.get("patch").toString();
				System.out.println(diffPatch);
				// raising request to the llm Model.                
                codeReview=llmModel.azureOpenAIConnection(diffPatch);			
                
				//String comment = "This is a test comment for file at position 1 trail 2: " + filename;
				
		        addCommentToChangedFile(filename, codeReview,commitId);
		}
		}
		return files;
	}

	public static void addReviewComment(String filename, int additions, int deletions) {
		// Implement logic to add review comments
	}
}