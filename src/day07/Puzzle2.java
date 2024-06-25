package day07;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Day 7 "Camel Cards" part 2.
 */
public class Puzzle2 extends Puzzle1 {
    
    @Override
    int order(char c) {
        int i = "AKQT98765432J".indexOf(c);
        
        if (i == -1) {
            throw new RuntimeException("Oops! " + c);
        }
        
        return i;
    }

    
    @Override
    int getType(String s) {
        int[] counts = new int[6];
        
        char c = s.charAt(0);
        int n = 1;
        for (int i = 1; i < 5; i++) {
            if (s.charAt(i) == c) {
                n++;
            } else {
                counts[n]++;
                c = s.charAt(i);
                n = 1;
            }
        }
        
        if (c != 'J') {
            counts[n]++;
        }
        
        if (counts[5] != 0) {
            if (c == 'J') {
                return HIGH_CARD;
            } else {
                return FIVE_OF_A_KIND;
            }
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
          
    int upvote(int type) {
        switch (type) {
            case FIVE_OF_A_KIND: return FIVE_OF_A_KIND;
            case FOUR_OF_A_KIND: return FIVE_OF_A_KIND;
            case THREE_OF_A_KIND: return FOUR_OF_A_KIND;
            case TWO_PAIRS: return FULL_HOUSE;
            case ONE_PAIR: return THREE_OF_A_KIND;
            case HIGH_CARD: return ONE_PAIR;
        }
        
        throw new RuntimeException("Oops: " + type);
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    @Override
    long solve(BufferedReader r) throws IOException {
        ArrayList<String> all = new ArrayList();
        
        String s = r.readLine();
        while (s != null) {
            String cards = s.substring(0, 5);
            String bid = s.substring(6);

            String sorted = sort(cards);
            int jokers = 5 - sorted.replace("J", "").length();
            int t = getType(sorted);
            System.out.println(sorted + " is type " + t + " and has " + jokers + " jokers");
            for (int i = 0; i < jokers; i++) {
                t = upvote(t);
            }

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
