package hangman;

import com.sun.source.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    private ArrayList<String> dictonary = new ArrayList<>();
    public int guessesLeft = 0;
    private SortedSet<Character> guessedLetters = new TreeSet<>();
    private Map<String, Set<String>> wordsLeft = new HashMap<String, Set<String>>();
    private String dashGuess = "nothing";
    private String possibleCategoryWithoutGuess = "";
    private String possibleShortestGuess = "";


    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        dictonary.clear();
        guessedLetters.clear();
        wordsLeft.clear();

        if (wordLength == 0) {
            throw new EmptyDictionaryException();
        }

        Scanner sc = new Scanner(dictionary);


        while (sc.hasNextLine()) { //adds words to dictonary
            String next = sc.nextLine();
            int lengthOfWord = next.length();

            if (lengthOfWord == wordLength) {
                dictonary.add(next);
            }
        }

        if (dictonary.size() == 0) {
            throw new EmptyDictionaryException();
        }

        StringBuilder initialDashGuess = new StringBuilder();

        for (int i = 0; i < wordLength; i++) {
            initialDashGuess.append('-');
        }

        dashGuess = initialDashGuess.toString();

    }


    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

        guess = Character.toLowerCase(guess);



        if (guessedLetters.contains(guess)) {
            throw new GuessAlreadyMadeException();
        }

        guessedLetters.add(guess);


        Set<String> wordsToReturn = new HashSet<>();
        //Set<String> wordsWithGuess = new HashSet<>();
        CharSequence guessChar = new StringBuilder(1).append(guess);

        //decide on which category to use
        for (int i = 0; i < dictonary.size(); i++) {
            String word = dashGuess(dictonary.get(i), guess);


            if (wordsLeft.containsKey(word)) {
                Set<String> values = wordsLeft.get(word); //assigns set to be equal to set in map
                if (values != null) {
                    values.add(dictonary.get(i)); //adds to that set
                    wordsLeft.put(word, values);
                }

            } else {
                Set<String> newValues = new HashSet<>();
                newValues.add(dictonary.get(i));
                wordsLeft.put(word, newValues);
            }

        }

        //find which category is biggest
        int max = 0;
        String biggestNumKey = "";
        Boolean equalCategories = false;
        for (Map.Entry<String, Set<String>> entry : wordsLeft.entrySet()) { //iterate through map
            int nextNum = entry.getValue().size();

            if (nextNum > max) {
                max = nextNum;
                biggestNumKey = entry.getKey();
                equalCategories = false;
            } else if (nextNum == max) {
                equalCategories = true;
            }
        }
        dictonary.clear();

        //decides which category to use
        if (equalCategories) {

            if (categoryWithoutGuess(guess)) {
                biggestNumKey = possibleCategoryWithoutGuess;
            } else if (wordWithFewestGuessedLetters(guess)) {
                biggestNumKey = possibleShortestGuess;
            } else {
                biggestNumKey = rightmostGuess(guess);
            }

        }

        //assign new dictonary values to indicated set
        dictonary.addAll(wordsLeft.get(biggestNumKey));
        dashGuess = biggestNumKey;

        //to tell user if the guess is in the word
        if (!dashGuess.contains(guessChar)) {
            System.out.println("Sorry there are no " + guess);
            guessesLeft = -1;
        } else {
            System.out.println("Yes, there is " + guessCount(dashGuess, guess) + " " + guess);
            guessesLeft = 0;
        }
        Set<String> returnSet = wordsLeft.get(biggestNumKey);
        wordsLeft.clear();

        //returns dictonary
        return returnSet; //returns the new set of possible words

    }


    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    //turns words into dashed words
    public String dashGuess(String s, char guess) {
        StringBuilder r = new StringBuilder(s); //turn s to stringbuilder to change

        for (int i = 0; i < s.length(); i++) {

            //if (s.charAt(i) != guess) {
            if (!guessedLetters.contains(s.charAt(i))) {
                r.setCharAt(i, '-');
            }
        }

        String v = r.toString();
        return v;
    }


    public String returnDashGuess() {
        return dashGuess;
    }

    public ArrayList<String> getDictonary() {
        return dictonary;
    }

    //Choose the group in which the letter does not appear at all.
    public Boolean categoryWithoutGuess(char guess) {
        CharSequence seqChar = new StringBuilder(1).append(guess);


        Boolean containsChar = false;
        for (Map.Entry<String, Set<String>> entry1 : wordsLeft.entrySet()) {
            String tester = entry1.getKey();

            if (!tester.contains(seqChar)) {
                containsChar = true;
                possibleCategoryWithoutGuess = tester;
            }
        }

        return containsChar;
    }

    //finds guess withrightmost guess, does tester 1 have a more rightmost guess?
    public String rightmostGuess(char guess) {
        String tester2 = "";
        String rightMostGuess = "";
        for (Map.Entry<String, Set<String>> entry1 : wordsLeft.entrySet()) { //for testing word
            String tester = entry1.getKey();

            for (Map.Entry<String, Set<String>> entry : wordsLeft.entrySet()) { //for word to test agasint
                tester2 = entry.getKey();

                for (int i = tester.length(); i > 0; i--) { //to interate through chars of each word
                    try {
                        char compare1 = tester.charAt(i - 1);
                        char compare2 = tester2.charAt(i - 1);

                        if (compare2 == guess && compare1 == '-') {
                            rightMostGuess = tester2;
                            break;
                        }
                        if (compare2 == '-' && compare1 == guess) {
                            rightMostGuess = tester;
                            break;
                        }

                    } catch (Exception ex) {
                        System.out.println("here");
                    }

                }

            }

        }

        return rightMostGuess;
    }

    public Boolean wordWithFewestGuessedLetters(char guess){
        CharSequence seqChar = new StringBuilder(1).append(guess);

        int min = 100;
        Boolean containsChar = false;
        for (Map.Entry<String, Set<String>> entry1 : wordsLeft.entrySet()) {
            String tester = entry1.getKey();

            if (tester.contains(seqChar)) {


                if (guessCount(tester, guess) < min) { //finds category with fewest number of guesses
                    min = guessCount(tester, guess);
                    containsChar = true;
                    possibleShortestGuess = tester;
                } else if (guessCount(tester, guess) == min && min != 0) { //if they are equal says false
                    containsChar = false;
                }

            }
        }


        return containsChar;
    }


    //tally up number of guesses
    public int guessCount(String tester, char guess) {
        StringBuilder r = new StringBuilder(tester);

        int guessCount = 0;
        for (int i = 0; i < tester.length(); i++) {
            if (r.charAt(i) == guess) {
                guessCount++;
            }
        }

        return guessCount;
    }


}