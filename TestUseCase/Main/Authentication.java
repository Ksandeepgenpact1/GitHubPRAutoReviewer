package Main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Authentication {

    private static Map<String, String> users = new HashMap<>();

    public static void main(String[] args) {
        // Register a user
        registerUser("john123", "password123");
        
        // Prompt for username and password
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        scanner.close();

        // Authenticate user
        if (authenticateUser(username, password)) {
            System.out.println("Authentication successful!");
        } else {
            System.out.println("Authentication failed!");
        }
    }

    private static void registerUser(String username, String password) {
        users.put(username, password);
    }

    private static boolean authenticateUser(String username, String password) {
        if (users.containsKey(username)) {
            String storedPassword = users.get(username);
            return storedPassword.equals(password);
        }
        return false;
    }
}