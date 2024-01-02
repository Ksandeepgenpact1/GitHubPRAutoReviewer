package Main;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class GitHubPullRequestChangedFilesReview {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final String GITHUB_API_URL = "https://api.github.com";
	public static final String ACCESS_TOKEN = "YOUR_ACCESS_TOKEN";
	public static final String OWNER = "your-username";
	public static final String REPO = "your-repo";
	public static final int PULL_NUMBER = 1234; // Replace with the actual pull request number

	public static void main(String[] args) {
		reviewPullRequest();
	}

	public static void reviewPullRequest() {
		// Step 1: List pull request files
		JSONArray files = listPullRequestFiles();

		// Step 2: Review each file individually
		for (int i = 0; i < files.length(); i++) {
			JSONObject file = files.getJSONObject(i);
			String filename = file.getString("filename");
			int additions = file.getInt("additions");
			int deletions = file.getInt("deletions");
			// Add logic to review the file, examine changes, lines, or sections

			// Step 3: Add review comments
			addReviewComment(filename, additions, deletions);
		}
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
		String url = GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO + "/commits/" + commitId + "/files";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + ACCESS_TOKEN)
				.header("Accept", "application/vnd.github.v3+json").build();

		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				String responseBody = response.body().string();
				return new JSONArray(responseBody);
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
			JSONArray commitFiles = listCommitFiles(commitId);
			files.putAll(commitFiles);
		}

		return files;
	}

	public static void addReviewComment(String filename, int additions, int deletions) {
		// Implement logic to add review comments
	}
}