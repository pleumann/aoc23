package day03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Day 03 "Gear Ratios"
 */
public class Puzzle {

    /**
     * Width and height of our (square) input.
     */
    int size;
    
    /**
     * The input as read from the file plus a border of 1 character.
     */
    char[][] schematic;

    /**
     * Helps us keep track of which numbers have already been extracted.
     */
    HashSet<String> seen = new HashSet();

    /**
     * Loads the input from the given Reader.
     */
    void load(BufferedReader r) throws IOException {
        String s = r.readLine();
        
        size = s.length();
        schematic = new char[size + 2][];
        
        schematic[0] = " ".repeat(size + 2).toCharArray();
        
        for (int i = 1; i <= size; i++) {
            s = " " + s + " ";
            schematic[i] = s.toCharArray();
            s = r.readLine();
        }

        schematic[size + 1] = schematic[0].clone();
    }
    
    /**
     * Extracts a number in line i, starting at column j, going  both backward
     * and forward. Returns the number if successful. Returns 0 if there is no
     * number to extract or we've already extracted that number.
     */
    int extract(int i, int j) {
        int start = j;
        int end = j;
        
        while (Character.isDigit(schematic[i][start])) {    // To the left
            start--;
        }

        while (Character.isDigit(schematic[i][end])) {      // To the right
            end++;
        }
        
        if (start != end) {
            String key = i + "," + start + "-" + end;       // e.g. "5,12-14"
            if (!seen.contains(key)) {
                seen.add(key);

                System.out.print('_');
                
                String s = new String(schematic[i], start + 1, end - start - 1);
                
                return Integer.parseInt(s);
            }
        } 
            
        return 0;
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
        int part2 = 0;
        
        load(r);

        /*
         * Part 1: Find symbols and try to extract numbers in all adjacent
         * locations. Don't "put back" the numbers because we don't want to see
         * any number more than once.
         */
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                char c = schematic[i][j];
                
                if (c != '.' && !Character.isDigit(c)) {
                    System.out.print(c);
                    
                    for (int k = - 1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            part1 += extract(i + k, j + l);
                        }
                    }
                }
            }
        }

        seen.clear();

        /*
         * Part 2: Find asterisks, extract numbers in all adjacent locations
         * and add them if the count is exactly 2. "Put back" numbers because
         * the same number might go into another gear ratio.
         */
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                char c = schematic[i][j];
                
                if (c == '*' ) {
                    System.out.print('*');
                    
                    int count = 0;
                    int ratio = 1;
                    
                    for (int k = - 1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            int m = extract(i + k, j + l);
                            if (m != 0) {
                                ratio = ratio * m;
                                count++;
                            }
                        }
                    }
                    
                    if (count == 2) {
                        part2 += ratio;
                    }

                    seen.clear();
                }
            }
        }
        
        System.out.println();
        System.out.println();
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.03 Gear Ratios ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
