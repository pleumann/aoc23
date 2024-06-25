package day09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Day 9 "Mirage Maintenance".
 */
public class Puzzle {
   
    record Pair(int left, int right) {
    
        public String toString() {
            return ("(" + left + ", " + right + ")");
        }
        
    };

    /**
     * Parses an input line into an int array.
     */
    int[] parse(String s) {
        String[] array = s.split(" ");            
        int length = array.length;
        int[] values = new int[length];

        for (int i = 0; i < length; i++) {
           values[i] = Integer.parseInt(array[i]);
        }
        
        return values;
    }

    /**
     * Recursively extrapolates the given array by first reducing it to all
     * zeros, then adding left/right numbers.
     */    
    Pair extra(int[] values) {
        int[] diffs = new int[values.length - 1];
        
        boolean zero = true;
        for (int i = 0; i < diffs.length; i++) {
            diffs[i] = values[i + 1] - values[i];
            if (diffs[i] != 0) {
                zero = false;
            }
            
            System.out.print(diffs[i] + " ");
        }
        System.out.println("(zero=" + zero + ")");
        
        Pair p = zero ? new Pair(0, 0) : extra(diffs);
        System.out.print(p + " => ");
        
        return new Pair(values[0] - p.left, values[values.length - 1] + p.right);
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
        int part2 = 0;
        
        String s = r.readLine();
        while (s != null) {
            System.out.println(s);
            
            Pair p = extra(parse(s));
            part1 += p.right;
            part2 += p.left;
            
            System.out.println(p);
            System.out.println();
            
            s = r.readLine();
        }
        
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.09 Mirage Maintenance ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
