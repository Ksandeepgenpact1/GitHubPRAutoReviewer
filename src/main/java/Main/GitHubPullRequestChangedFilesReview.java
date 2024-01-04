package Main;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GitHubPullRequestChangedFilesReview {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final String GITHUB_API_URL = "https://api.github.com";
	public static final String ACCESS_TOKEN = "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";
	public static final String OWNER = "Ksandeepgenpact1";
	public static final String REPO = "eclipse-plugin";
	public static final int PULL_NUMBER = 1; // Replace with the actual pull request number

	public static void main(String[] args) {
			boolean mergeable=checkPullRequestMergeability();
			if(mergeable) {
			reviewPullRequest();
			}
	}

	public static void reviewPullRequest() {
		// Step 1: List pull request files
		JSONArray files = listPullRequestFiles();

		// Step 2: Review each file individually
		for (int i = 0; i < files.length(); i++) {
			JSONObject file = files.getJSONObject(i);
			String filename = file.getString("filename");
			System.out.println(filename);
//			int additions = file.getInt("additions");
//			int deletions = file.getInt("deletions");
//			// Add logic to review the file, examine changes, lines, or sections
//
//			// Step 3: Add review comments
//			addReviewComment(filename, additions, deletions);
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

		for (int i = 0; i < commits.length(); i++) {
			JSONObject commit = commits.getJSONObject(i);
			String commitId = commit.getString("sha");
			System.out.println(commitId);
			JSONArray commitFiles = listCommitFiles(commitId);
			files.putAll(commitFiles);
		}

		return files;
	}

	public static void addReviewComment(String filename, int additions, int deletions) {
		// Implement logic to add review comments
	}
}