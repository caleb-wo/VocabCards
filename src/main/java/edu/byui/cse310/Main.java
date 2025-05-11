package edu.byui.cse310;

import edu.byui.cse310.StudyGame.StudyGame;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            StudyGame.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}