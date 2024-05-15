package day05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Day 5 "If You Give A Seed A Fertilizer" solution based on "range arithmetic".
 */
public class Puzzle {

    /**
     * Represents a range. Start is inclusive. End is exclusive.
     */
    record Range(long start, long end) {

        /**
         * Determines the intersection between this and another range.
         */
        Range intersect(Range other) {
            return new Range(Math.max(start, other.start), Math.min(end, other.end));
        }

        /**
         * Checks whether this range is empty.
         */        
        boolean isEmpty() {
            return start >= end;
        }
    
        /**
         * Returns a printable representation.
         */
        @Override
        public String toString() {
            return start + " ... " + end;
        }
        
    };

    /**
     * Represents an entry in one of the mappings.
     */
    record Entry(long start, long end, long delta) { };
    
    /**
     * Represents one of the mappings.
     */
    class Mapping {
        
        String name;
        
        ArrayList<Entry> entries = new ArrayList();
        
        public Mapping(String s) {
            String[] a = s.split(" ");
            name = a[0];
        }
        
        public void parseEntry(String s) {
            String[] a = s.split(" ");
            long start = Long.parseLong(a[1]);
            long end = start + Long.parseLong(a[2]);
            long delta = Long.parseLong(a[0]) - start;

            entries.add(new Entry(start, end, delta));
        }
        
        public long mapValue(long i) {
            for (Entry e: entries) {
                if (i >= e.start() && i < e.end()) {
                    return i + e.delta();
                }
            }
            
            return i;
        }
        
        /**
         * Sends a list of ranges through this mapping, result in new ranges.
         * The reason that it looks a bit complicated at first is that we want
         * the resulting ranges to be disjoint.
         */
        public ArrayList<Range> mapRanges(ArrayList <Range> in) {
            ArrayList<Range> out = new ArrayList();

            for (Range r: in) {
                ArrayList<Range> pieces = new ArrayList();
                pieces.add(r);
                
                for (Entry e: entries) {
                    ArrayList<Range> newPieces = new ArrayList();

                    for (Range rr: pieces) {
                        Range common = rr.intersect(new Range(e.start(), e.end()));
                        Range before = rr.intersect(new Range(Long.MIN_VALUE, e.start()));
                        Range after = rr.intersect(new Range(e.end(), Long.MAX_VALUE));

                        if (!common.isEmpty()) {
                            out.add(new Range(common.start() + e.delta(), common.end() + e.delta()));
                        }

                        if (!before.isEmpty()) {
                            newPieces.add(before);
                        }

                        if (!after.isEmpty()) {
                            newPieces.add(after);
                        }
                    }

                    pieces = newPieces;
                }

                out.addAll(pieces);
            }
            
            return out;
        }
        
    }    
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */    
    void solve(BufferedReader r) throws IOException {
        ArrayList<Mapping> mappings = new ArrayList();
        
        String s = r.readLine();
        String[] a = s.split(" ");
        long[] values = new long[a.length - 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = Long.parseLong(a[i + 1]);
        }
        
        s = r.readLine();
        s = r.readLine();
        while (s != null) {
            Mapping m = new Mapping(s);
            mappings.add(m);
            s = r.readLine();
            while (s != null && !"".equals(s)) {
                m.parseEntry(s);
                s = r.readLine();
            }
            s = r.readLine();
        }

        Long part1 = Long.MAX_VALUE;
        for (long i: values) {
            System.out.print(i);
            for (Mapping m: mappings) {
                i = m.mapValue(i);
                System.out.print(" -> " + i);
            }
            System.out.println();
            
            part1 = Math.min(part1, i);
        }

        System.out.println();

//        Long part2 = Long.MAX_VALUE;
//        for (int i = 0; i < values.length; i += 2) {
//            System.out.println(values[i] + " " + values[i + 1]);
//            for (long j = values[i]; j < values[i] + values[i + 1]; j++) {
//                long k = j;
//                for (Mapping m: mappings) {
//                    k = m.mapValue(k);
//                }
//
//                part2 = Math.min(part2, k);
//            }
//        }

        Long part2 = Long.MAX_VALUE;
        for (int i = 0; i < values.length; i += 2) {
            ArrayList<Range> ranges = new ArrayList();
            Range source = new Range(values[i], values[i] + values[i + 1]);
            ranges.add(source);
            System.out.print(source);
            for (Mapping m: mappings) {
                ranges = m.mapRanges(ranges);
                System.out.print(" -> " + ranges.size() + "x");
            }
            
            long smallest = Long.MAX_VALUE;
            for (Range rr: ranges) {
                smallest = Math.min(smallest, rr.start());
            }
            System.out.println(" -> " + smallest);
            part2 = Math.min(part2, smallest);
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
        System.out.println("*** AoC 2023.05 If You Give A Seed A Fertilizer ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
