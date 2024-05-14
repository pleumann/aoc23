package day04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Day 4 "Scratchcards" simple, mostly procedural solution.
 */
public class Puzzle {
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
        
        HashMap<Integer, Integer> cards = new HashMap();   // Instances per card
        
        String s = r.readLine();
        while (s != null) {
            String[] a = s.split("[\\:\\|] ");
            
            int id = Integer.parseInt(a[0].substring(4).trim());
            
            // Store numbers in sets for quick access
            HashSet<String> wins = new HashSet(Arrays.asList(a[1].split(" +")));
            HashSet<String> mine = new HashSet(Arrays.asList(a[2].split(" +")));
            
            // Determine current card's score
            wins.retainAll(mine);
            int hits = wins.size();
            
            // Calculate effect on part1 and part2.
            if (hits != 0) {
                part1 += 1 << (hits - 1);
            }

            cards.put(id, cards.getOrDefault(id, 1));
            
            for (int i = id + 1; i <= id + hits; i++) {
                cards.put(i, cards.getOrDefault(i, 1) + cards.getOrDefault(id, 1));
            }
            
            s = r.readLine();
        }

        int part2 = cards.values().stream().reduce(0, Integer::sum);
        
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
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
