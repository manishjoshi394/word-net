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

import dependencies.*;

/**
 * The class implements the Shortest ancestral path API the methods provide a
 * way to get SAP in the most lucid way possible in Java
 *
 * @author Manish Joshi
 */
public class SAP {

    private BreadthFirstDirectedPaths fromV;
    private BreadthFirstDirectedPaths fromW;
    private final Digraph G;

    /**
     * The constructor takes a Directed Graph as an Argument.
     *
     * @param G a directed Graph to be processed
     */
    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    /**
     * Length of SAP between two vertices
     *
     * @param v First vertex
     * @param w Second vertex
     * @return Length of the shortest ancestral path between v and w
     */
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        fromV = new BreadthFirstDirectedPaths(G, v);
        fromW = new BreadthFirstDirectedPaths(G, w);

        int nearestAncestor = getAncestor(fromV, fromW);
        if (nearestAncestor == -1) {
            return -1;
        }
        return fromV.distTo(nearestAncestor) + fromW.distTo(nearestAncestor);
    }

    /**
     * Nearest common ancestor vertex between two vertices
     *
     * @param v first vertex
     * @param w second vertex
     * @return Nearest common ancestor of v and w
     */
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        fromV = new BreadthFirstDirectedPaths(G, v);
        fromW = new BreadthFirstDirectedPaths(G, w);
        return getAncestor(fromV, fromW);
    }

    /**
     * Length of SAP between any vertex from set v to any vertex to set w
     *
     * @param v First set of vertices
     * @param w second set of vertices
     * @return the length of SAP between any vertex from v to any from w
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateIterable(v);
        validateIterable(w);

        fromV = new BreadthFirstDirectedPaths(G, v);
        fromW = new BreadthFirstDirectedPaths(G, w);

        int nearestAncestor = getAncestor(fromV, fromW);
        if (nearestAncestor == -1) {
            return -1;
        }
        return fromV.distTo(nearestAncestor) + fromW.distTo(nearestAncestor);
    }

    /**
     * Nearest common ancestor vertex between any of the vertex from set v to
     * any from set w
     *
     * @param v First set of vertices as an Iterable
     * @param w Second set of vertices as another iterable
     * @return the nearest common ancestor from v and w
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateIterable(v);
        validateIterable(w);
        fromV = new BreadthFirstDirectedPaths(G, v);
        fromW = new BreadthFirstDirectedPaths(G, w);
        return getAncestor(fromV, fromW);
    }

    /**
     * The function calculates ancestor vertex if given the couple of BFS
     * objects which were created using the two children vertices v and w
     * <p>
     * The BFS search data objects are used to determine the nearest ancestor
     *
     * @param fromV BFS object created with some source vertex v
     * @param fromW BFS object created with some source vertex w
     * @return the nearest ancestor for v and w
     */
    private int getAncestor(BreadthFirstDirectedPaths fromV,
            BreadthFirstDirectedPaths fromW) {

        boolean ancestorExists = false;
        int nearestAncestor = 0;    // lets say 0 is nearest common ancestor
        for (int i = 0; i < G.V(); ++i) {
            if (fromV.hasPathTo(i) && fromW.hasPathTo(i)) {
                ancestorExists = true;

                // Path length through i
                int disOfi = fromV.distTo(i) + fromW.distTo(i);

                // path length through current nearest ancestor
                int disOfNearest;
                if (fromV.hasPathTo(nearestAncestor) && fromW.hasPathTo(nearestAncestor)) {
                    disOfNearest = fromV.distTo(nearestAncestor)
                            + fromW.distTo(nearestAncestor);
                } else {
                    disOfNearest = Integer.MAX_VALUE;
                }

                // update nearest ancestor
                if (disOfi < disOfNearest) {
                    nearestAncestor = i;
                }
            }
        }

        if (ancestorExists) {
            return nearestAncestor;
        }
        return -1;
    }

    // Verfication methods to verify input data
    private void validateVertex(int v) {
        if (v < 0 || v >= G.V()) {
            throw new java.lang.IllegalArgumentException("Vertex "
                    + v + ", Out of range");
        }
    }

    private void validateIterable(Iterable<Integer> set) {
        if (set == null) {
            throw new java.lang.IllegalArgumentException("Null not allowed");
        }

        for (int v : set) {
            validateVertex(v);
        }
    }

    /**
     * For unit testing of this class
     *
     * @param args
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
