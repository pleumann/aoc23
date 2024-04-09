package day01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Day 01 "Trebuchet?!". Straightforward solution, mostly based on regular
 * expressions.
 */
public class Puzzle {

    /**
     * Digits.
     */
    static final Pattern PART_1_REGEX = Pattern.compile("[0-9]");

    /**
     * Digits and text representations of digits.
     */
    static final Pattern PART_2_REGEX = Pattern.compile("[0-9]|one|two|three|four|five|six|seven|eight|nine");

    /**
     * Returns the digit value for the given string.
     */
    int digit(String s) {
        return switch (s) {
            case "one"   -> 1;
            case "two"   -> 2;
            case "three" -> 3;
            case "four"  -> 4;
            case "five"  -> 5;
            case "six"   -> 6;
            case "seven" -> 7;
            case "eight" -> 8;
            case "nine"  -> 9;
                
            default -> Integer.parseInt(s);
        };
    }
    
    /**
     * Returns the number for the given string and regex pattern.
     */
    int number(String s, Pattern p) {
        if ("".equals(s)) return 0;
        
        Matcher m = p.matcher(s);
        
        if (m.find()) {
            int first = digit(m.group());
            int last = first;

            while (m.find(m.start() + 1)) {
                last = digit(m.group());
            }
           
            return 10 * first + last;
        } else {
            return 0;        
        }        
    }

    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
        int part2 = 0;
        
        String s = r.readLine();
        while (s != null) {    
            System.out.print('.');
            part1 += number(s, PART_1_REGEX);
            part2 += number(s, PART_2_REGEX);
            s = r.readLine();
        }

        System.out.println();
        System.out.println();
        System.out.printf("Part 1: %6d\n", part1);
        System.out.printf("Part 2: %6d\n", part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.01 Trebuchet?! ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
