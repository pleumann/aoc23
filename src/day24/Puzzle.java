package day24;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Day 24 "Never Tell Me The Odds".
 */
public class Puzzle {

    /**
     * The min/max values for the accepted range.
     */
    static final double MIN = 200000000000000d;
    static final double MAX = 400000000000000d;

    /**
     * A quick and dirty implementation of Gaussian elimination.
     */
    class Gauss {
        /**
         * The values describing our equations.
         */
        double[][] values;
        
        /**
         * Creates a new solver for the given number of equations.
         */
        Gauss(int size) {
            values = new double[size][size + 1];            
        }

        /**
         * Gets a value.
         */
        double get(int x, int y) {
            return values[x][y];
        }

        /**
         * Sets a value.
         */
        void set(int x, int y, double d) {
            values[x][y] = d;
        }

        /**
         * Divides the given row by the given value.
         */
        void div(int row, double d) {
            for (int i = 0; i < values.length + 1; i++) {
                values[row][i] /= d;
            }
        }

        /**
         * Adds a multiple of a source row to a destination row.
         */
        void addmul(int dst, int src, double d) {
            for (int i = 0; i < values.length + 1; i++) {
                values[dst][i] += values[src][i] * d;
            }
        }
        
        /**
         * Implements the overall solving, returns true on success.
         */
        boolean solve() {
            for (int i = 0; i < values.length; i++) {
                if (Math.abs(values[i][i]) < 0.00001) {
                    for (int j = i + 1; j < values.length; j++) {
                        if (Math.abs(values[j][i]) >= 0.00001) {
                            double[] tmp = values[i];
                            values[i] = values[j];
                            values[j] = tmp;
                            break;
                        }
                    }
                    
                    if (Math.abs(values[i][i]) < 0.00001) {
                        return false;
                    }
                }
                
                div(i, values[i][i]);
                
                for (int j = 0; j < values.length; j++) {
                    if (j != i) {
                        addmul(j, i, -values[j][i]);
                    }
                }
            }
            
            return true;
        }
        
        /**
         * Dumps the values.
         */
        void dump() {
            for (int i = 0; i < values.length; i++) {
                System.out.print("| ");
                for (int j = 0; j <= values.length; j++) {
                    System.out.printf("%23.3f | ", values[i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }
    
    /**
     * Represents a hailstone in 3D, consisting of location and velocity
     * vectors.
     */
    class Hailstone {
        /**
         * The six values describing our hailstone.
         */
        long px, py, pz, dx, dy, dz;
        
        /**
         * Parses a hailstone from the textual representation used in the input
         * file.
         */
        Hailstone(String s) {
            String[] a = s.split("\\s*(,|@)\\s*");
            px = Long.parseLong(a[0]);
            py = Long.parseLong(a[1]);
            pz = Long.parseLong(a[2]);
            
            dx = Long.parseLong(a[3]);
            dy = Long.parseLong(a[4]);
            dz = Long.parseLong(a[5]);
        }

        /**
         * Checks whether this hailstone collides with another given hailstone
         * at some point in time and within the range limits. Treats the two
         * tailstones as equations. Uses the Gauss solver to solve them.
         */
        boolean intersects(Hailstone other) {
            Gauss g = new Gauss(2);
            
            g.set(0, 0, dx);
            g.set(0, 1, -other.dx);
            g.set(0, 2, other.px - px);

            g.set(1, 0, dy);
            g.set(1, 1, -other.dy);
            g.set(1, 2, other.py - py);
            
            g.dump();
            
            if (g.solve()) {
                g.dump();
                double t = g.get(0, 2);
                double s = g.get(1, 2);
                
                double x1 = px + t * dx;
                double y1 = py + t * dy;

                double x2 = other.px + s * other.dx;
                double y2 = other.py + s * other.dy;
                
                System.out.printf("Solvable! x=%22.3f y=%22.3f ", x1, y1);
                
                if (t >= 0 && s >= 0 && MIN <= x1 && x1 <= MAX && MIN <= y1 && y1 <= MAX) {
                    System.out.println("and within limits.\033[K");
                    return true;
                } else {
                    System.out.println("but beyond limits.\033[K");
                    return false;
                }
            } else {
                g.dump();
                System.out.println("Unsolvable!\033[K");
                return false;
            }
        }
    }
    
    /**
     * Calls Mathematica on a nearby Pi to solve the non-linear equation system
     * for part 2.
     */
    long mathematica(String equation) throws IOException {
        long a = 0;
        long b = 0;
        long c = 0;

        Runtime rt = Runtime.getRuntime();
        String[] commands = { "/usr/bin/ssh", "pi@raspberrypi", "echo '"+ equation + "' > in.txt && wolfram < in.txt" };
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new 
             InputStreamReader(proc.getInputStream()));
        
        String ss = stdInput.readLine();
        while (ss != null) {
            if (ss.startsWith("In[1]")) {
                System.out.println(ss + equation);
            } else if (ss.startsWith("Out[1]")) {
                System.out.println(ss);

                String[] sss = ss.split("-> |,");

                a = Long.parseLong(sss[1]);
                b = Long.parseLong(sss[3]);
                c = Long.parseLong(sss[5]);
            } else {
                System.out.println(ss);
            }
            
            ss = stdInput.readLine();
        }

        if (a * b * c == 0) {
            System.out.println("No response from Raspberry Pi. :( Please copy the following into Mathematica:");
            System.out.println();
            System.out.println(equation);

            return 0;
        }
        
        return a + b + c;
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        ArrayList<Hailstone> list = new ArrayList();
                
        String s = r.readLine();
        while (s != null) {
            list.add(new Hailstone(s));
            s = r.readLine();
        }
        
        int part1 = 0;
        
        System.out.print("\n".repeat(9));
        
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                System.out.printf("\033[9ARunning Gaussian elimination for hailstones %3d and %3d...\n\n", i, j);
                if (list.get(i).intersects(list.get(j))) {
                    //System.out.print('#');
                    part1++;
                } else {
                    //System.out.print('.');
                }
            }
        }
        
        Hailstone h1 = list.get(0);
        Hailstone h2 = list.get(1);
        Hailstone h3 = list.get(2);
        
        StringBuilder sb = new StringBuilder("Solve[{");
        
        sb.append("a+t*d==").append(h1.px).append("+t*(").append(h1.dx).append("),");
        sb.append("b+t*e==").append(h1.py).append("+t*(").append(h1.dy).append("),");
        sb.append("c+t*f==").append(h1.pz).append("+t*(").append(h1.dz).append("),");

        sb.append("a+s*d==").append(h2.px).append("+s*(").append(h2.dx).append("),");
        sb.append("b+s*e==").append(h2.py).append("+s*(").append(h2.dy).append("),");
        sb.append("c+s*f==").append(h2.pz).append("+s*(").append(h2.dz).append("),");

        sb.append("a+u*d==").append(h3.px).append("+u*(").append(h3.dx).append("),");
        sb.append("b+u*e==").append(h3.py).append("+u*(").append(h3.dy).append("),");
        sb.append("c+u*f==").append(h3.pz).append("+u*(").append(h3.dz).append(")");
        
        sb.append("}]");

        System.out.println();
        System.out.println("Part 2 is non-linear, so let's get some help...");
        System.out.println();

        long part2 = mathematica(sb.toString());
        
        System.out.println();
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.24 Never Tell Me The Odds ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
