/* 
 * The MIT License
 *
 * Copyright 2018 Manish Joshi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.HashMap;
import edu.princeton.cs.algs4.In;

/**
 * This class provides WordNet processing API via public methods. It builds the
 * WordNet with the help of two files provided to the construction containing
 * lexical data in a particular format.
 *
 * @author Manish Joshi
 */
public final class WordNet {

    private SAP pathFinder;             // Shortest ancestral path object reference
    private ArrayList<String> synSet;   // integer indexed array
    private HashMap<String, Bag<Integer>> id;   // noun keyed map of synset IDs of noun

    /**
     * The constructor takes two file names as two strings.
     *
     * @param synsets Address of synsets.txt as string
     * @param hypernyms Address of hypernyms.txt as string
     */
    public WordNet(String synsets, String hypernyms) {

        if (synsets == null || hypernyms == null) {
            throw new java.lang.IllegalArgumentException("Null found");
        }

        // Initialise the Data structures
        synSet = new ArrayList<>();
        id = new HashMap<>();

        // Initialise an int  numID which will be used for
        // - the current id during scan
        // - The number of IDs after scan
        int numID = 0;

        // read in synsets, build data structures and set numID
        numID = readSynsets(synsets);

        // build a Digraph with max ID value
        Digraph G = new Digraph(numID + 1);

        readHypernyms(hypernyms, G);
        
        // check whether the Digraph is a rooted DAG or not
        edu.princeton.cs.algs4.Topological tSort
                = new edu.princeton.cs.algs4.Topological(G);
        if (!tSort.hasOrder() || !isRooted(G)) {
            throw new java.lang.IllegalArgumentException(
                    "NOT a DAG, provided graph has cycles or is NOT rooted");
        }

        // load up the SAP object with Digraph G
        pathFinder = new SAP(G);
    }

    /**
     * Reads synsets file and builds appropriate data structures to hold the
     * relevant data
     *
     * @param synsets
     * @return max ID of synsets in file equal to ... number of synsets - 1
     */
    private int readSynsets(String synsets) {
        int numID = 0;  // used to temporarily hold id values and return max ID value

        // Set up a scanner reference
        In scan = null;
        scan = new In(synsets);

        // read synsets
        while (scan.hasNextLine()) {

            String[] line = scan.readLine().split(",");
            numID = Integer.parseInt(line[0]);
            synSet.add(numID, line[1]);
            String[] words = line[1].split(" ");
            for (String word : words) {
                if (id.containsKey(word)) {
                    id.get(word).add(numID);    // add IDs of a noun to the id hashmap
                } else {
                    Bag<Integer> ids = new Bag<>();
                    ids.add(numID);
                    id.put(word, ids);
                }
            }
        }
        return numID;
    }

    /**
     * Reads hypernyms file and builds a corresponding Digraph
     *
     * @return the number of vertices with zero outDegree (referred as root
     * here)
     */
    private void readHypernyms(String hypernyms, Digraph G) {
        In scan = new In(hypernyms);
        // read hypernyms
        while (scan.hasNextLine()) {
            String[] line = scan.readLine().split(",");
            int v = Integer.parseInt(line[0]);
            for (String s : line) {
                {
                    int w = Integer.parseInt(s);
                    if (v != w) {
                        G.addEdge(v, w);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the digraph G is rooted !
     * @param G The Digraph
     * @return yes if rooted
     */
    private boolean isRooted(Digraph G)
    {
        int numOfRoots = 0;
        for (int v = 0; v < G.V(); ++v)
            if (G.outdegree(v) == 0)
                numOfRoots++;
        return numOfRoots == 1;
    }
    
    /**
     * The set of nouns as No duplicates Iterable
     *
     * @return Iterable list of all WordNet nouns
     */
    public Iterable<String> nouns() {
        Bag<String> nouns = new Bag<>();
        for (String noun : id.keySet()) {
            nouns.add(noun);
        }
        return nouns;
    }

    /**
     * Is the word a WordNet noun ?
     *
     * @param word the word to test
     * @return true if it's a WordNet noun
     */
    public boolean isNoun(String word) {
        if (word == null)
            throw new java.lang.IllegalArgumentException("Null not allowed");
        return id.containsKey(word);
    }

    /**
     * Common Ancestral Distance between nounA and nounB
     *
     * @param nounA
     * @param nounB
     * @return common ancestral distance b/w nounA and nounB in the WordNet
     * Graph
     */
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new java.lang.IllegalArgumentException(nounA + " is NOT a WordNet Noun");
        } 
        if (!isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException(nounB + " is not a WordNet Noun");
        }
        
        Bag<Integer> v = id.get(nounA);
        Bag<Integer> w = id.get(nounB);
        return pathFinder.length(v, w);
    }

    /**
     * <b> Weird Name : Pay attention</b>
     * a synset (second field of synsets.txt) that is the common ancestor of
     * nounA and nounB in a shortest ancestral path (defined below)
     *
     * @param nounA First noun
     * @param nounB Second noun
     * @return common ancestor Synset (nearest)
     */
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new java.lang.IllegalArgumentException(nounA + " is NOT a WordNet Noun");
        } 
        if (!isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException(nounB + " is not a WordNet Noun");
        }
        Bag<Integer> v = id.get(nounA);
        Bag<Integer> w = id.get(nounB);
        int ancestorID = pathFinder.ancestor(v, w);
        return synSet.get(ancestorID);
    }

    // for informal unit testing of the class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            /*String word = StdIn.readLine();
            if (wn.isNoun(word)) {
                StdOut.println(word + " : Now that's a noun");
            } else {
                StdOut.println("Cannot find " + word);
            }*/
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            StdOut.println(wn.sap(nounA, nounB) + " : " + wn.distance(nounA, nounB));
        }
    }
}
