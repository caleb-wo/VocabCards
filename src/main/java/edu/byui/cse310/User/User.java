package edu.byui.cse310.User;

import edu.byui.cse310.Enums.TitleOfHonor;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;

public class User {
    /*
    * This class is the class which holds user data. It has a manually coded builder class (to show
    * competency). It holds the user's score and their title of honor.
    * */
    private static Dotenv env = Dotenv.load();
    private static final String PATH = env.get("USER_CSV");
    private String username = "Caleb";
    private int score;
    private TitleOfHonor titleOfHonor;

    // Manual Getters
    public int getScore() {
        return score;
    }

    public TitleOfHonor getTitleOfHonor() {
        return titleOfHonor;
    }

    public String getUsername() {
        return username;
    }

    public void addScore(){
        if ( score % 10 == 0){
            score = score + 10;
            System.out.println("Plus 10 points!");
        } else {
            score = getScore() + 5;
            System.out.println("Plus 5 points!");
        }
    }

    private User(Builder builder) {
        this.score = builder.score;
        this.titleOfHonor = builder.titleOfHonor;
    }
    // Manual Builder Class
    public static class Builder {
        private int score;
        private TitleOfHonor titleOfHonor;

        public Builder score(int score) {
            this.score = score;
            return this;
        }

        public User build() {
            for (TitleOfHonor title : TitleOfHonor.values()) {
                if (score > title.getValue()){
                    titleOfHonor = title;
                }
            }
            return new User(this);
        }

    }

    public static void saveUser(User user) {
        // Save's user score.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH))) {
            // Write user data to CSV
            writer.write(String.format("%d", user.getScore()));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to save user: " + e.getMessage());
        }
    }

    public static User loadUser() {
        // Load user. Only needs score.
        User user = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(PATH))) {
            String line = reader.readLine(); // Read the first line only

            if (line != null) {
                String[] parts = line.split(",");
                int score = Integer.parseInt(parts[0]); // Parse score

                // Use the Builder to create the User object
                user = new User.Builder().score(score).build();
            }
        } catch (IOException e) {
            System.err.println("Failed to load user: " + e.getMessage());
        }

        return user;
    }
}
