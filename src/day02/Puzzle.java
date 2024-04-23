package day02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Day 02 "Cube Conundrum".
 */
public class Puzzle {

    /**
     * Represents a single game.
     */
    class Game {

        int number; // Game number 
        
        int red;    // Minimum red cubes
        
        int green;  // Minimum green cubes
        
        int blue;   // Minimum blue cubes
        
        /**
         * Creates the Game object from the given line of text.
         */
        Game(String s) {
            String[] a = s.split("[\\:;] ");

            number = Integer.parseInt(a[0].split(" ")[1]);
            
            for (int i = 1; i < a.length; i++) {
                String[] b = a[i].split(", ");

                for (String t: b) {
                    String[] c = t.split(" ");
                    
                    int count = Integer.parseInt(c[0]);
                    String color = c[1];
                    
                    switch (color) {
                        case "red"   -> red   = Math.max(red,   count);
                        case "green" -> green = Math.max(green, count);
                        case "blue"  -> blue  = Math.max(blue,  count);
                    }
                     
                }
                
            }
        }

        /**
         * Returns this game's number, so all data is encapsulated nicely.
         */
        int getNumber() {
            return number;
        }

        /**
         * Reflects whether this game is possible according the part 1 rules.
         */
        boolean isPossible() {
            return red <= 12 && green <= 13 && blue <= 14;
        }

        /**
         * Returns the power of this game according to part 2 rules.
         */
        int getPower() {
            return red * green * blue;
        }

        @Override
        public String toString() {
            return String.format(
                    "%3d %2d %2d %2d \033[%s;37m%4d\033[0m", 
                    number, red, green, blue, isPossible() ? "42" : "41", getPower());
        }
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
        int part2 = 0;
        
        System.out.print("Num  R  G  B  P+P");
        System.out.println(" | Num  R  G  B  P+P".repeat(3));
        System.out.println("-".repeat(77));
        
        String s = r.readLine();
        while (s != null) {
            Game g = new Game(s);
            System.out.print(g.toString());
            if (g.getNumber() % 4 == 0) {
                System.out.println("   ");
            } else {
                System.out.print(" | ");
            }
            
            if (g.isPossible()) {
                part1 += g.getNumber();
            }
            part2 += g.getPower();
            
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
        System.out.println("*** AoC 2023.02 Cube Conundrum ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
