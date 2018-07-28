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
import edu.princeton.cs.algs4.StdIn;
import java.util.Scanner;

/**
 * Finds the odd ones out of the provided nouns, The class maintains a WordNet
 * beneath it and uses it to help the client identify the most odd word (called
 * outcast) among a given set.
 *
 * @author Manish Joshi
 */
public class Outcast {

    private WordNet wordnet;
    
    /**
     * The constructor takes a WordNet object
     * @param wordnet fully initialized word-net object
     */
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {

        // create a distance array used to store, 
        // for every vertex - sum of distances to other vertices
        int[] d = new int[nouns.length];

        // generate sum of distance values
        for (int i = 0; i < nouns.length; ++i) {
            for (int j = 0; j < nouns.length; ++j) {
                d[i] += wordnet.distance(nouns[i], nouns[j]);
            }
        }

        // Get noun with maximum sum of distances 
        int t = 0;  // let that noun be nouns[t] at index t = 0
        for (int i = 0; i < d.length; ++i) {
            if (d[t] < d[i]) {
                t = i;
            }
        }
        return nouns[t];    // return the noun with max distance sum
    }
    
    /**
     * For unit testing of the class
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Outcast mj = new Outcast(new WordNet(args[0], args[1]));
        int n;

        Scanner scan = new Scanner(System.in);
        while (!StdIn.isEmpty()) {
            n = StdIn.readInt();
            String[] input = new String[n];
            for (int i = 0; i < n; ++i) {
                input[i] = StdIn.readString();
            }
            System.out.println("\n" + mj.outcast(input) + "   -   is an Outcast");
        }
    }
}
