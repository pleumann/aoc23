package day04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 * Day 4 "Scratchcards" simple, mostly procedural solution.
 */
public class PuzzleOld {
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
        int part2 = 0;
        
        HashMap<Integer, Integer> scores = new HashMap();   // Score per card
        Stack<Integer> cards = new Stack();                 // Our cards
        
        String s = r.readLine();
        while (s != null) {
            String[] a = s.split("[\\:\\|] ");
            
            int id = Integer.parseInt(a[0].substring(4).trim());
            
            String[] winning = a[1].split(" +");
            String[] numbers = a[2].split(" +");

            // Store winning numbers in has set for quick access
            HashSet<String> quick = new HashSet(Arrays.asList(winning));
            for (String t: winning) {
                quick.add(t);
            }
            
            // Determine current card's score
            int score = 0;
            for (String t: numbers) {
                if (quick.contains(t)) {
                    score++;
                }
            }
            
            scores.put(id, score);
            if (score != 0) {
                part1 += 1 << (score - 1);
            }
            
            // Push card to stack for part 2.
            cards.push(id);
            
            s = r.readLine();
        }
        
        // While we still have cards, apply part 2 rules.
        while (!cards.isEmpty()) {
            part2++;
            
            int i = cards.pop();
            int j = scores.get(i);
            
            for (int k = 0; k < j; k++) {
                cards.push(i + k + 1);
            }
        }
        
        System.out.println();
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.04 Scratchcards ***");
        System.out.println();
        
        new PuzzleOld().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
