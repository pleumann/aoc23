package day07;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Day 7 "Camel Cards" part 1.
 */
public class Puzzle1 {

    /**
     * The different ranks that can occur.
     */
    public static final int FIVE_OF_A_KIND  = 6;
    public static final int FOUR_OF_A_KIND  = 5;
    public static final int FULL_HOUSE      = 4;
    public static final int THREE_OF_A_KIND = 3;
    public static final int TWO_PAIRS       = 2;
    public static final int ONE_PAIR        = 1;
    public static final int HIGH_CARD       = 0;

    /**
     * Returns the order for a given card.
     */
    int order(char c) {
        int i = "AKQJT98765432".indexOf(c);
        
        if (i == -1) {
            throw new RuntimeException("Oops! " + c);
        }
        
        return i;
    }

    /**
     * Sorts the hand, so all identical cards are neighbours.
     */
    String sort(String s) {
        StringBuilder b = new StringBuilder();
        
        for (int i = 0; i < 5; i++) {
            char c = s.charAt(i);
            
            int j = 0;
            while (j < b.length() && order(c) > order(b.charAt(j))) {
                j++;
            }
            
            b.insert(j, c);
        }
        
        return b.toString();
    }
    
    /**
     * Translates the hand to something that we can easily sort later.
     */
    String translate(String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = s.charAt(i);
            b.append((char)('z' - order(c)));
        }
        
        return b.toString();
    }
    
    /**
     * Returns the rank of this hand.
     */
    int getType(String s) {
        int[] counts = new int[6];
        
        char c = s.charAt(0);
        int n = 1;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                n++;
            } else {
                counts[n]++;
                c = s.charAt(i);
                n = 1;
            }
        }
        counts[n]++;
        
        if (counts[5] != 0) {
            return FIVE_OF_A_KIND;
        } else if (counts[4] != 0) {
            return FOUR_OF_A_KIND;
        } else if (counts[3] != 0) {
            if (counts[2] != 0) {
                return FULL_HOUSE;
            } else {
                return THREE_OF_A_KIND;
            }
        } else if (counts[2] == 2) {
            return TWO_PAIRS;
        } else if (counts[2] == 1) {
            return ONE_PAIR;
        } else {
            return HIGH_CARD;
        }
        
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    long solve(BufferedReader r) throws IOException {
        ArrayList<String> all = new ArrayList();
        
        String s = r.readLine();
        while (s != null) {
            String cards = s.substring(0, 5);
            String bid = s.substring(6);

            String sorted = sort(cards);
            int t = getType(sorted);

            cards = ("" + (char)('a' + t)) + ' ' + translate(cards) + ' ' + bid;

            System.out.println(s + " -> " + sorted + " -> " + t + " -> " + cards);

            all.add(cards);
            
            s = r.readLine();
        }
        
        System.out.println();
        
        Collections.sort(all);

        long score = 0;
        long rank = 1;

        for (String ss: all) {
            System.out.println(ss);
            score += rank * Long.parseLong(ss.substring(8));
            rank++;
        }

        System.out.println();
        
        return score;
    }
    
}
