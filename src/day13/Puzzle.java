package day13;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Day 13 "Point of Incidence".
 */
public class Puzzle {

    /**
     * Returns all symmetries for a given string, as a set of integers denoting
     * the reflection points.
     */
    HashSet<Integer> getSymmetries(String s) {
        HashSet<Integer> result = new HashSet();
        String t = new StringBuilder(s).reverse().toString();
        int l = s.length();

        for (int i = 1; i < l; i++) {
            String a = s.substring(i);
            String b = t.substring(l - i);

            if (b.startsWith(a) || a.startsWith(b)) {
                result.add(i);
            }
        }

        return result;
    }
    
    /**
     * Returns the one symmetry for a given list of strings, either vertical or
     * horizontal, taking into account a single no-go value (that we later use
     * for "the old symmetry"). The return value already takes into account the
     * rule for result values (i.e. * 100 for horizontal reflections).
     */
    int getNormalSymmetry(ArrayList<String> a, int nogo) {
        for (String s: a) {
            System.out.println(s);
        }
        
        HashSet<Integer> h = getSymmetries(a.get(0));
        for (int i = 1; i < a.size(); i++) {
            h.retainAll(getSymmetries(a.get(i)));
        }

        Iterator <Integer> it = h.iterator();
        while (it.hasNext()) {
            int x = it.next();
            if (x != nogo) {
               System.out.println("Vertical at " + x);
               return x;
            }
        }
         
        h.clear();

        for (int i = 0; i < a.get(0).length(); i++) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < a.size(); j++) {
                sb.append(a.get(j).charAt(i));
            }

            if (i == 0) {
                h = getSymmetries(sb.toString());
            } else {
                h.retainAll(getSymmetries(sb.toString()));
            }
        }

        it = h.iterator();
        while (it.hasNext()) {
            int x = it.next();
            if (100 * x != nogo) {
                System.out.println("Horizontal at " + x);
                return 100 * x;
            }
        }

        return 0;
    }
    
    /**
     * Tries all possibilities of "repairing" a smudge until a new symmetry
     * is detected. The old symmetry value can be passed as a no-go, so it wil
     * be ignored.
     */
    int getSmudgeSymmetry(ArrayList<String> a, int nogo) {
        for (int i = 0; i < a.size(); i++) {
            StringBuilder sb = new StringBuilder(a.get(i));
            for (int j = 0; j < a.get(0).length(); j++) {
                System.out.println("" + i + "/" + j);
                char c = sb.charAt(j);
                char d = c == '.' ? '#' : '.';

                sb.setCharAt(j, d);
                a.set(i, sb.toString());

                int sym = getNormalSymmetry(a, nogo);
                if (sym != 0) {
                    return sym;
                }

                sb.setCharAt(j, c);
                a.set(i, sb.toString());
            }
        }
        
        throw new RuntimeException("WTF!?");
    }

    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
        int part2 = 0;
                
        String s = r.readLine();
        
        while (s != null) {
            ArrayList<String> a = new ArrayList();
            a.add(s);
            s = r.readLine();
            while(s != null && !"".equals(s)) {
                a.add(s);
                s = r.readLine();
            }

            int old = getNormalSymmetry(a, 0);
            
            part1 += old;
            part2 += getSmudgeSymmetry(a, old);

            s = r.readLine();
            System.out.println();
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
        System.out.println("*** AoC 2023.13 Point of Incidence ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
