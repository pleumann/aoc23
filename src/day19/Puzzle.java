package day19;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Day 19 "Aplenty".
 */
public class Puzzle {

    /**
     * Represents a rule inside a workflow.
     */
    class Rule {

        /**
         * The index of the field this rule applies to. x=0, m=1, a=2, s=3.
         */
        int field;
        
        /**
         * The relational operator. Either "less than" or "greater than". 
         */
        char relation;
        
        /**
         * The value to compare to.
         */
        int value;
        
        /**
         * The name of the workflow to go to if the comparison is positive.
         */
        String then;

        /**
         * Creates a rule from the textual representation.
         */
        public Rule(String s) {
            String[] a = s.splitWithDelimiters("[\\<\\>\\:]", 0);
            
            field    = "xmas".indexOf(a[0].charAt(0));
            relation = a[1].charAt(0);
            value    = Integer.parseInt(a[2]);
            then     = a[4];
        }
    }
        
    /**
     * Represents a workflow.
     */
    class Workflow {

        /**
         * The name of the workflow.
         */
        String name;

        /**
         * The rules contained in this workflow.
         */
        Rule[] rules;
        
        /**
         * The workflow to jump to if none of the rules matches.
         */
        String otherwise;

        /**
         * Creates a workflow from the textual representation.
         */
        public Workflow(String s) {
            int p = s.indexOf('{');

            name = s.substring(0, p);
            String[] a = s.substring(p + 1, s.length() - 1).split(",");

            rules = new Rule[a.length - 1];

            for (int i = 0; i < a.length - 1; i++) {
                rules[i] = new Rule(a[i]);
            }
            
            otherwise = a[a.length - 1];
        }
    }

    /**
     * Holds all our workflows "by name".
     */
    HashMap<String, Workflow> workflows = new HashMap();

    /**
     * Represents a simple part.
     */
    class Part {
        
       /**
        * The values of the four variables.
        */
       int[] values;
       
       /**
        * Creates a part from its textual representation.
        */
       Part(String s) {
           values = new int[4];
           
           String[] a = s.substring(1, s.length() - 1).split(",");
           for (int i = 0; i < 4; i++) {
               String[] b = a[i].split("=");
               values[i] = Integer.parseInt(b[1]);
           }
        }
        
        @Override
        public String toString() {
            String s = "[";
            for (int i = 0; i < 4; i++) {
                if (!"[".equals(s)) {
                    s = s + ',';
                }
                s = s + values[i];
            }
            s = s + "]";
            return s;
        }         
       
        /**
         * Returns the total rating of this single part.
         */
        int rating() {
            int result = 0;

            for (int i = 0; i < 4; i++) {
                result = result + values[i];
            }

            return result;
        }

        /**
         * Checks whether the part matches a given rule.
         */
        boolean matches(Rule r) {
            if (r.relation == '<') {
                return values[r.field] < r.value;
            } else {
                return values[r.field] > r.value;
            }
        }

        /**
         * Recursively moves the part through the workflows until it is either
         * accepted or rejected. Returns the rating if accepted, or 0 otherwise.
         */
        int filter(String s) {
            if ("A".equals(s)) {
                int i = rating();
                System.out.printf("Part      %45s is \033[42;37maccepted\033[0m.  --> %5d\n", toString(), i);
                return i;
            } else if ("R".equals(s)) {
                System.out.printf("Part      %45s is \033[41;37mrejected\033[0m.\n", toString());
                return 0;
            } else {
                Workflow wf = workflows.get(s);
                
                for (Rule r: wf.rules) {
                    if (matches(r)) {
                        return filter(r.then);
                    }
                }
                
                return filter(wf.otherwise);
            }
        }

    }
    
    /**
     * Represents a whole 4D-range of parts. 
    */
    class MultiPart {

        /**
         * Low x/m/a/s value.
         */
        int[] min = new int[4];

        /**
         * High x/m/a/s value. Note: In contrast to the input this is exclusive!
         */
        int[] max = new int[4];

        /**
         * Creates a new multipart for given values.
         */
        MultiPart(int x1, int x2, int y1, int y2, int z1, int z2, int a1, int a2) {
            min[0] = x1; max[0] = x2;
            min[1] = y1; max[1] = y2;
            min[2] = z1; max[2] = z2;
            min[3] = a1; max[3] = a2;
            
        }

        /**
         * Clones another multipart.
         */
        MultiPart(MultiPart other) {
            for (int i = 0; i < 4; i++) {
                min[i] = other.min[i];
                max[i] = other.max[i];
            }
        }
        
        /**
         * Returns the number of parts contained in this multipart.
         */
        long volume() {
            long result = 1;
            
            for (int i = 0; i < 4; i++) {
                result = result * Math.max(max[i] - min[i], 0);
            }
            
            return result;
        }
        
        @Override
        public String toString() {
            String s = "[";
            for (int i = 0; i < 4; i++) {
                if (!"[".equals(s)) {
                    s = s + ',';
                }
                s = s + min[i] + "-" + max[i];
            }
            s = s + "]";
            return s;
        }         

        /**
         * Splits the multipart into two according to the given rule. The split
         * will always affect exactly one dimension. The new part is returned.
         * It represents everything that matches the rules. The part on which
         * the method is called represents the remains.
         */
        MultiPart split(Rule r) {
            MultiPart mp2 = new MultiPart(this);

            if (r.relation == '>') {
                max[r.field] = r.value + 1;
                mp2.min[r.field] = r.value + 1;
            } else {
                min[r.field] = r.value;
                mp2.max[r.field] = r.value;
            }

            return mp2;
        }

        /**
         * Recursively explores all workflows, splitting multiparts into two
         * repeatedly on the way, until each multipart is either accepted or
         * rejected. Returns the total number of equivalent simple parts that
         * were accepted.
         */
        long explore(String s) {
            if ("A".equals(s)) {
                System.out.printf("Multipart %45s is \033[42;37maccepted\033[0m.  --> %15d\n", toString(), volume());
                return volume();
            } else if ("R".equals(s)) {
                System.out.printf("Multipart %45s is \033[41;37mrejected\033[0m.\n", toString());
                return 0;
            } else {
                Workflow wf = workflows.get(s);
                
                long result = 0;

                for (Rule r: wf.rules) {
                    System.out.printf("Multipart %45s is split up.\n", this);
                    MultiPart mp2 = split(r);
                    result += mp2.explore(r.then);
                }

                result += explore(wf.otherwise);

                return result;
            }
        }
        
    }

    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        String s = r.readLine();
        while (!"".equals(s)) {
            Workflow wf = new Workflow(s);
            System.out.printf("Workflow  %45s has %d rules.\n", s, wf.rules.length);
            workflows.put(wf.name, wf);
            s = r.readLine();
        }

        System.out.println();

        int part1 = 0;
        s = r.readLine();
        
        while (s != null) {
            Part p = new Part(s);
            part1 = part1 + p.filter("in");
            s = r.readLine();
        }

        System.out.println();
        
        MultiPart mp = new MultiPart(1, 4001, 1, 4001, 1, 4001, 1, 4001);
        long part2 = mp.explore("in");
                
        System.out.println();
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.19 Aplenty ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
