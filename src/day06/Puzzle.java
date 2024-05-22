package day06;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Day 6 "Wait For It".
 */
public class Puzzle {

    /**
     * Return the number of solutions for the given race. Iterates through all
     * seconds and counts the value for which the distance beats the benchmark.
     */
    long simpleWays(long time, long distance) {
        long result = 0;
        
        for (long i = 1; i < time; i++) {
            if (i * (time - i) > distance) {
                result++;
            }
        }
        
        return result;
    }

    /**
     * Return the number of solutions for the given race. Uses the "pq formula"
     * to solve the quadratic equation describing the race.
     */
    long cleverWays(long time, long distance) {
        double p = -time;
        double q = distance + 1;
        
        double d = Math.sqrt(p * p / 4.0 - q);
                
        long x1 = (long)(-p / 2.0 + d);
        long x2 = (long)(-p / 2.0 - d);
        
        long result = Math.abs(x2 - x1);
        
        return result;
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    void solve(BufferedReader r) throws IOException {
        String s = r.readLine();
        String t = r.readLine();
        
        String[] a = s.split(" +");
        String[] b = t.split(" +");
        
        String c = "";
        String d = "";
        
        long part1 = 1;

        for (int i = 1; i < a.length; i++) {
            int time = Integer.parseInt(a[i]);
            int distance = Integer.parseInt(b[i]);
        
            c += time;
            d += distance;
            
            part1 *= cleverWays(time, distance);
        }

        long part2 = cleverWays(Long.parseLong(c), Long.parseLong(d));
        
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.06 Wait For It ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
