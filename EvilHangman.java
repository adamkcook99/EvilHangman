package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangman {


    public void print(int i, SortedSet<Character> j, String dashGuess) {
        System.out.println("You have " + i + " guesses left");
        System.out.println("Used letters: " + j);
        System.out.println("Word: " + dashGuess);
        System.out.print("Enter guess: ");

    }



    public static void main(String[] args) throws IOException, EmptyDictionaryException, GuessAlreadyMadeException {
        EvilHangman hangman = new EvilHangman();

        String dictionaryFileName = args[0];
        String wordLength = args[1];
        String guessesString = args[2];

        int guesses = Integer.parseInt(guessesString);
        int wordLengthReal = Integer.parseInt(wordLength);

        File file = new File(dictionaryFileName);

        EvilHangmanGame newGame = new EvilHangmanGame();
        newGame.startGame(file, wordLengthReal);



        String dashGuess = "";
        Boolean gameWon = false;
        for (int i = guesses; i > 0;) {

            hangman.print(i, newGame.getGuessedLetters(), newGame.returnDashGuess()); //prints each level
            Scanner sc = new Scanner(System.in);



            try {

                String input = "";
                input = sc.next();
                input = input.toLowerCase(Locale.ROOT); //to make it lower case

                do {


                    //to test if guess has already been made
                    if (newGame.getGuessedLetters().contains(input.charAt(0))) {

                        System.out.print("Guess already made! Enter guess: ");
                        input = sc.next();
                        input = input.toLowerCase(Locale.ROOT);
                    } else if (!Character.isLetter(input.charAt(0))) {

                        System.out.print("Invalid input! Enter guess: ");
                        input = sc.next();
                        input = input.toLowerCase(Locale.ROOT);
                    }

                    } while (newGame.getGuessedLetters().contains(input.charAt(0)) || !Character.isLetter(input.charAt(0))) ;

                    newGame.makeGuess(input.charAt(0));


            } catch (GuessAlreadyMadeException ex){ //if guess already made

            }


            i = i + newGame.guessesLeft; //to decrement or not decrement

            //testing to see if only one word is left meaing user won
            if (!newGame.returnDashGuess().contains("-") ) {
                gameWon = true;
                break;
            }

            System.out.println();
        }

        if (gameWon) {
            System.out.println("You win! You guessed the word: " + newGame.getDictonary().get(0));
        } else {
            System.out.println("Sorry, you lost! The word was: " + newGame.getDictonary().get(0));
        }


    }

}
