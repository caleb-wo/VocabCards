package edu.byui.cse310.VocabCard;

import edu.byui.cse310.Enums.Genre;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class VocabCard {
    /*
    * This class is what will hold the vocab cards. Using Lombok, I was able to
    * easily generate my Getters and Setters as well as an all arguments constructor. This
    * will make is much easier when I need to make these classes from a csv file.
    * I also am using the @Builder annotation so that I can use a Builder class in order to make
    * these objects.
    * */
    private static Dotenv env = Dotenv.load();
    private static final String PATH = env.get("CARDS_CSV");
    private String name;
    private String definition;
    @Builder.Default
    private int correctTally = 0;
    @Builder.Default
    private int incorrectTally = 0;
    @Builder.Default
    private double accuracyRating = 0;
    private Genre genre;
    @Builder.Default
    private boolean isRetired = false;


//    These 2 methods save all stored vocab cards.
    public static void saveVocabCards( List<VocabCard> list ){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH))) {
            // Write CSV header
            writer.write("name,definition,correctTally,incorrectTally,accuracyRating,genre,isRetired");
            writer.newLine();

            for (VocabCard card : list) {
                writer.write(String.format("%s,%s,%d,%d,%.2f,%s,%b",
                        escapeCsv(card.getName()),
                        escapeCsv(card.getDefinition()),
                        card.getCorrectTally(),
                        card.getIncorrectTally(),
                        card.getAccuracyRating(),
                        card.getGenre(),
                        card.isRetired()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save vocab cards: " + e.getMessage());
        }
    }


    // Escapes commas and quotes to safely handle CSV format
    private static String escapeCsv(String input) {
        if (input.contains(",") || input.contains("\"")) {
            input = input.replace("\"", "\"\"");
            return "\"" + input + "\"";
        }
        return input;
    }

//    These 2 load them all and return them in a list.
    public static List<VocabCard> loadVocabCards() {
        List<VocabCard> cards = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PATH))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip CSV header
                    continue;
                }

                String[] tokens = parseCsvLine(line);

                if (tokens.length < 7) continue; // Invalid line

                VocabCard card = VocabCard.builder()
                        .name(tokens[0])
                        .definition(tokens[1])
                        .correctTally(Integer.parseInt(tokens[2]))
                        .incorrectTally(Integer.parseInt(tokens[3]))
                        .accuracyRating(Double.parseDouble(tokens[4]))
                        .genre(Genre.valueOf(tokens[5].toUpperCase().replace(" ", "_")))
                        .isRetired(Boolean.parseBoolean(tokens[6]))
                        .build();

                cards.add(card);
            }

        } catch (IOException e) {
            System.err.println("Failed to load vocab cards: " + e.getMessage());
        }

        return cards;
    }

    // Simple CSV parser that handles quoted strings
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }
}