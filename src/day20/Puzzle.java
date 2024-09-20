package day20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 * Day 20 "Pulse Propagation".
 */
public class Puzzle {

    /**
     * Represents a module (flip-flop, conjunction or broadcaster).
     */
    class Module {

        /**
         * The type of the module. % is flip-flop, & is conjunction, everything
         * else is broadcaster.
         */
        char type = ' ';

        /**
         * Name of the module.
         */
        String id;
        
        /**
         * Sources of this module, together with the most recent values.
         */
        HashMap<Module, Boolean> inputs = new HashMap();
        
        /**
         * Targets of this module.
         */
        ArrayList<Module> outputs = new ArrayList();
        
        /**
         * Most recent state of the module.
         */
        boolean state;

        /**
         * Creates a module with the given name.
         */
        Module(String s) {
            id = s;
        }

        /**
         * Sets the type of the module.
         * @param c 
         */
        void setType(char c) {
            type = c;
        }
        
        /**
         * Adds an output to this module.
         */
        void addOutput(Module m) {
            outputs.add(m);
            m.inputs.put(this, false);
        }
        
        /**
         * Dumps the module.
         */
        void dump() {
            String i = "";
            for (Module m: inputs.keySet()) {
                i = i.isEmpty() ? m.id : i + "," + m.id;
            }

            String o = "";
            for (Module m: outputs) {
                o = o.isEmpty() ? m.id : o + "," + m.id;
            }
            
            System.out.printf("%s %-15s : %-35s -> %-25s\n", type, id, i, o);
        }
        
        /**
         * Sends the given value to all outputs.
         * @param value 
         */
        void send(boolean value) {
            for (Module m: outputs) {
                queue.add(new Pulse(this, m, value));
            }
        }

        /**
         * Receives a signal from a given source module.
         */
        void receive(Module m, boolean value) {
            if (type == '%') {
                if (!value) {
                    state = !state;
                    send(state);
                }
            } else if (type == '&') {
                inputs.put(m, value);
                
                state = inputs.values().stream().allMatch(b -> b);
                send(!state);
            } else {
                send(value);
            }
            
        }
        
        /**
         * Resets the module.
         */
        void reset() {
            state = false;
            for (Module m: inputs.keySet()) {
                inputs.put(m, false);
            }
        }

    }
    
    /**
     * Represents a pulse coming from a module and going to another one. We need
     * this in collectible form.
     */
    record Pulse (Module from, Module to, boolean value) {
        
        @Override
        public String toString() {
            return from.id + " -" + (value ? "high" : "low") + "-> " + to.id; 
        }
        
    };

    /**
     * The message queue of pulses that still need to be processed.
     */
    Queue<Pulse> queue = new ArrayDeque();
    
    /**
     * All known modules, by name.
     */
    HashMap<String, Module> modules = new HashMap();

    /**
     * Returns the module with the given name, creating it if necesary, which is
     * concenient during the parsing phase.
     */
    Module getModule(String s) {
        Module m = modules.get(s);
        if (m == null) {
            m = new Module(s);
            modules.put(s, m);
        }
        return m;
    }
    
    /**
     * Loads the puzzle input and builds the network of modules.
     */
    void load(BufferedReader r) throws IOException {
        String s = r.readLine();
        while (s != null) {
            String[] a = s.split(" -> |, ");

            char c = a[0].charAt(0);
            
            if (c == '%' || c == '&') {
                a[0] = a[0].substring(1);
            } else {
                c = ' ';
            }
            
            Module m = getModule(a[0]);
            m.setType(c);
            
            for (int i = 1; i < a.length; i++) {
                m.addOutput(getModule(a[i]));
            }
            
            s = r.readLine();
        }
        
    }
    
    /**
     * Dumps all modules.
     */
    void dump() {
        for (Module m: modules.values()) {
            m.dump();
        }
        System.out.println();
    }
    
    /**
     * Solves part 1 of the puzzle for the input coming from the given reader.
     */
    long part1() {
        Module bc = modules.get("broadcaster");
        
        int low = 0;
        int high = 0;
        
        for (int i = 0; i < 1000; i++) {
            queue.add(new Pulse(null, bc, false));
            
            while (!queue.isEmpty()) {
                Pulse p = queue.remove();

                if (p.value()) {
                    high++;
                } else {
                    low++;
                }

                p.to.receive(p.from, p.value);
            }
        }

        System.out.println("Low is " + low + ", high is " + high + ".");
        System.out.println();
        
        return low * high;
    }

    
    /**
     * Finds a cycle for the given module, that is, the number of button
     * presses needed to make this module send a single low pulse.
     */
    int findCycle(Module m) {
        int count = 0;
        
        if (m != null) {
            Module bc = modules.get("broadcaster");

            for (Module n: modules.values()) {
                n.reset();
            }

            boolean done = false;
            while (!done) {
                count++;
                queue.add(new Pulse(null, bc, false));

                while (!queue.isEmpty()) {
                    Pulse p = queue.remove();
                    p.to.receive(p.from, p.value);
                    if (p.to == m && !p.value) {
                        done = true;
                    }
                }
            }

            System.out.println("Cycle for '" + m.id + "' node is " + count + ".");
        }
        
        return count;
    }
    
    /**
     * Solves part 2 of the puzzle for the input coming from the given reader.
     * Is specifically constructed for the given input, where the cycle of the
     * output module depends on cycles of its four input modules match.
     */
    long part2() {
        long xf = findCycle(modules.get("xf"));
        long cm = findCycle(modules.get("cm"));
        long gc = findCycle(modules.get("gc"));
        long sz = findCycle(modules.get("sz"));
        
        System.out.println();
        
        return xf * cm * gc * sz;
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.20 Pulse Propagation ***");
        System.out.println();
        
        Puzzle p = new Puzzle();
        p.load(new BufferedReader(new FileReader(args[0])));
        p.dump();
        
        long part1 = p.part1();
        long part2 = p.part2();
        
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
        System.out.println();
    }
}
