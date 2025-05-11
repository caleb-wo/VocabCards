package edu.byui.cse310.StudyGame;

import edu.byui.cse310.Enums.Genre;
import edu.byui.cse310.User.User;
import edu.byui.cse310.VocabCard.VocabCard;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StudyGame {
    /*
    * This is the Study Game class that facilitates the
    * flash card practice. It is a singleton. It enables the user to practice
    * their vocab words.*/
    // Singleton
    private static StudyGame studyGame;
    private StudyGame() {} // This ensures only one exists.
    public static StudyGame getInstance() {
        if (studyGame == null) {
            studyGame = new StudyGame();
        }
        return studyGame;
    }
//    Fields
    private Dotenv env = Dotenv.load();
    private final String USERS = env.get("USER_CSV");
    private final String CARDS = env.get("CARDS_CSV");
    private final String CSV_DIR = env.get("CSV_DIR");
    private final User USER = User.loadUser();
    private List<VocabCard> vocabCards = VocabCard.loadVocabCards();
    // Static fields
    private static Scanner scanner = new Scanner(System.in);

    // Main Loop
    public static void run() throws IOException, InterruptedException {
        int gameState = 0;
        while (gameState == 0){
            gameState = getInstance().runCode(getInstance().displayMenuAndGetCode());
        }
    }

//    Study Loop
    public int displayMenuAndGetCode(){
        /*
        * This method displays the menu and returns the user's choice,
        * or code.
        * */
//        Display Menu
        System.out.println("|============STUDY GAME============|");
        System.out.println("|       1. Study Cards             |");
        System.out.println("|       2. Save User               |");
        System.out.println("|       3. Show Stats              |");
        System.out.println("|       4. New Card                |");
        System.out.println("|       5. Exit                    |");
        System.out.println("|==================================|");
        System.out.println("Enter your choice: ");
//        Get user choice
        int choice = scanner.nextInt();

        return choice;
    }

    public void studyGame() throws InterruptedException, IOException {
        // This is the main game loop. It gets the time for the user
        // and then uses an executor paired with an AtomicInteger to
        // time-out the study session.
        AtomicInteger elapsedSeconds = new AtomicInteger(0);

        System.out.print("Enter study game duration in minutes: ");
        int gameLength = scanner.nextInt() * 60;

        // ScheduledExecutorService to run a task every second
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable gameClock = () -> {
            elapsedSeconds.getAndIncrement();
        };

        // Schedule the timer task at a fixed rate of 1 second
        scheduler.scheduleAtFixedRate(gameClock, 1, 1, TimeUnit.SECONDS);

        // Main loop runs until timer completes
        while (elapsedSeconds.get() < gameLength) {
            Random random = new Random();
            VocabCard workingCard = vocabCards.get(random.nextInt(vocabCards.size()));

            for( int i = 0; i < 50; i++ ){
                if ( i % 2 == 0){
                    System.out.print("—");
                } else {
                    System.out.print(" ");
                }
                Thread.sleep(25);
            }
            for( int i = 0; i < 11; i++ ){
                if( i == 6 ){
                    int padding = (50 - workingCard.getName().length()) / 2;
                    StringBuilder line = new StringBuilder();

                    line.append("|");
                    for (int j = 0; j < (padding - 1); j++) {
                        line.append(" ");
                    }

                    line.append(workingCard.getName());

                    while (line.length() < 49) {
                        line.append(" ");
                    }
                    line.append("|");

                    System.out.println(line);
                } else {
                    System.out.println("|                                                |");
                }
                Thread.sleep(25);
            }
            for( int i = 0; i < 50; i++ ){
                if ( i % 2 == 0){
                    System.out.print("—");
                } else {
                    System.out.print(" ");
                }
                Thread.sleep(50);
            }

            System.out.println();
            System.out.println();
            System.out.println("Click [ENTER] to move on.");
            scanner.nextLine();
            System.out.println("Answer: "+workingCard.getDefinition());

            System.out.println("Were you right (y/n): ");
            boolean isRight = scanner.nextLine().trim().equalsIgnoreCase("y") ? true : false;
            if (isRight) {
                workingCard.setCorrectTally(workingCard.getCorrectTally() + 1);
                getInstance().USER.addScore();
            } else {
                workingCard.setIncorrectTally(workingCard.getIncorrectTally() + 1);
            }

            System.out.println("Word: "+workingCard.getName());
            System.out.println("Genre: "+workingCard.getGenre());
            System.out.println("Definition: "+workingCard.getDefinition());
            System.out.println("Correct Tally: "+workingCard.getCorrectTally());
            System.out.println("Incorrect Tally: "+workingCard.getIncorrectTally());
            System.out.println("Accuracy Rating: "+workingCard.getAccuracyRating());
            System.out.println("Click [ENTER] to see the next card.");
            scanner.nextLine();
        }
        System.out.println("Time's up! Good job!");
    }

    public int runCode(int code) throws IOException, InterruptedException {
        switch (code) {
            case 1: // Study Card Game
                studyGame();
                return 0;

            case 2: // Save User
                User.saveUser(getInstance().USER);
                System.out.println("User saved!");
                return 0;

            case 3: // Display User Details
                System.out.println("   Score: " + getInstance().USER.getScore());
                System.out.println("   Title of Honor: " + getInstance().USER.getTitleOfHonor().getStringVal());
                return 0;

            case 4: // New Card
                System.out.println("Enter vocab word or phrase: ");
                scanner.nextLine(); // consume any leftover newline
                String vocabWord = scanner.nextLine().trim();

                System.out.println("Enter definition: ");
                String def = scanner.nextLine().trim();

                System.out.println();
                int count = 1;
                for (Genre genre : Genre.values()) {
                    System.out.println(count + ". " + genre);
                    count++;
                }
                System.out.println();
                System.out.print("Enter genre number: ");
                int genreNumber = scanner.nextInt() - 1; // Adjusting for zero-based index
                Genre genre = Genre.values()[genreNumber];

                vocabCards.add(
                        VocabCard.builder()
                                .name(vocabWord)
                                .definition(def)
                                .genre(genre)
                                .build()
                );

                System.out.println("Card has been added!");
                VocabCard.saveVocabCards(vocabCards);
                return 0;

            case 5: // Exit Program
                VocabCard.saveVocabCards(vocabCards);
                System.out.println("Goodbye! See you next time!");
                return 1;

            default:
                System.out.println("Wrong code!");
                return 0;
        }
    }


//    Game Utilities
    public void clearTerminal(){
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command = "";

            if (os.contains("win")) {
                command = "cls"; // Windows command to clear the terminal
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                command = "clear"; // Unix/Linux/macOS command to clear the terminal
            }

            // Execute the command
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
