package day12;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Day 12 "Hot Springs". Solution transforms the right side numbers into strings
 * of hash signs and tries to parse left and right simultaneously, recursively
 * handling all permutations on the way.
 */
public class Puzzle {

    /**
     * Provides a much needed cache for speeding up part 2.
     */
    HashMap<String, Long> seen = new HashMap();
    
    /**
     * Solves the given left/right sides recursively. Returns the number of
     * possible solutions. The "group" parameter specifies whether we are
     * currently in such a group (basically whether the previous character was
     * a hash sign aka a broken spring).
     */
    long solve(String l, String r, boolean group) {
        String key = l + "-" + r + "-" + group;         // Consult cache
        if (seen.containsKey(key)) {
            return seen.get(key);
        }
        
        String heads = "" + l.charAt(0) + r.charAt(0);  // Heads to consider
        
        if (r.length() > l.length()) {                  // Shortcut
            return 0;
        }
        
        String ll = l.substring(1);                     // Left side tail
        String rr = r.substring(1);                     // Right side tail
        
        long result;
        
        result = switch (heads) {                       // Recursive stuff
            case "$$" -> 1;
            case ".$" -> solve(ll, r, false);
            case "?$" -> solve(ll, r, false);
            case "##" -> solve(ll, rr, true);
            case "#," -> group ? 0 : solve(ll, rr, false);
            case "?#" -> group ? solve(ll, rr, true) : solve(ll, r, false) + solve(ll, rr, true);
            case ".#" -> group ? 0 : solve(ll, r, false);
            case "?," -> solve(ll, rr, false);                
            case ".," -> solve(ll, rr, false);
            default   -> result = 0;
        };
        
        seen.put(key, result);                          // Update cache
        
        return result;
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        long part1 = 0;
        long part2 = 0;
        
        String s = r.readLine();
        while (s != null) {
            String[] a = s.split(" ");          // Split input lines in halves

            String left = a[0];                 // Take left side as-is

            String[] t = a[1].split(",");
            
            String right = "";                  // Convert numbers to characters
            for (String t1 : t) {
                if (right.length() != 0) {
                    right = right + ',';
                }
                right = right + "#".repeat(Integer.parseInt(t1));
            }

            long result1 = solve(left + "$", right + "$", false);
            System.out.printf("%35s -> %20s -> %5d", s, right, result1);
            
            left = left + ("?" + left).repeat(4) + "$";
            right = right + ("," + right).repeat(4) + "$";
                        
            long result2 = solve(left, right, false);
            System.out.printf(" %15d\n", result2);
            
            part1 += result1;
            part2 += result2;
           
            s = r.readLine();
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
        System.out.println("*** AoC 2023.12 Hot Springs ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
