public class SAP {
    private Digraph G;
    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Stack<Integer> st1 = new Stack<Integer>();
        Stack<Integer> st2 = new Stack<Integer>();
        st1.push(v);
        st2.push(w);

        return length(st1, st2);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        for (int i : v)
            if (i < 0 || i >= G.V())
                throw new java.lang.IndexOutOfBoundsException();

        for (int i : w)
            if (i < 0 || i >= G.V())
                throw new java.lang.IndexOutOfBoundsException();

        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
                int tmp = bfs1.distTo(i) + bfs2.distTo(i);
                if (min > tmp)
                    min = tmp;
            }
        }

        if (min == Integer.MAX_VALUE)
            return -1;
        return min;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Stack<Integer> st1 = new Stack<Integer>();
        Stack<Integer> st2 = new Stack<Integer>();
        st1.push(v);
        st2.push(w);

        return ancestor(st1, st2);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
    {
        for (int i : v)
            if (i < 0 || i >= G.V())
                throw new java.lang.IndexOutOfBoundsException();

        for (int i : w)
            if (i < 0 || i >= G.V())
                throw new java.lang.IndexOutOfBoundsException();

        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
        int anc = -1;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
                int tmp = bfs1.distTo(i) + bfs2.distTo(i);
                if (min > tmp) {
                    min = tmp;
                    anc = i;
                }
            }
        }

        return anc;
    }

    public static void main(String[] args) {
	    In in = new In(args[0]);
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);
	    while (!StdIn.isEmpty()) {
	        int v = StdIn.readInt();
	        int w = StdIn.readInt();
	        int length   = sap.length(v, w);
	        int ancestor = sap.ancestor(v, w);
	        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
	    }
	}
}
