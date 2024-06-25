package day07;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Day 7 "Camel Cards".
 */
public class Puzzle {
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.07 Camel Cards ***");
        System.out.println();

        long part1 = new Puzzle1().solve(new BufferedReader(new FileReader(args[0])));
        long part2 = new Puzzle2().solve(new BufferedReader(new FileReader(args[0])));

        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
        
        System.out.println();
    }
}
