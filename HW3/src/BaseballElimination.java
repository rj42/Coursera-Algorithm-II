import java.util.HashMap;

/**
 * Created by lb_k on 3/28/14.
 */
public class BaseballElimination {
    private int N;
    private int[] w, l, r;
    private int[][] g;
    private String[] name;
    private HashMap<String, Integer> teams;
    private int lastCalculated = -1;
    private Stack<String> st;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        N = in.readInt();
        w = new int[N];
        l = new int[N];
        r = new int[N];
        g = new int[N][N];
        name = new String[N];
        teams = new HashMap<String, Integer>();
        for (int i = 0; i < N; i++) {
            name[i] = in.readString();
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();
            for (int j = 0; j < N; j++)
                g[i][j] = in.readInt();
            teams.put(name[i], i);
        }
        in.close();
    }
    public int numberOfTeams() {
        return N;
    }
    public Iterable<String> teams() {
        Queue<String> queue = new Queue<String>();
        for (int i = 0; i < N; i++)
            queue.enqueue(name[i]);
        return queue;
    }
    public int wins(String team) {
        Integer i = teams.get(team);
        if (i == null)
            throw new IllegalArgumentException();
        return w[i];
    }
    public int losses(String team) {
        Integer i = teams.get(team);
        if (i == null)
            throw new IllegalArgumentException();
        return l[i];
    }
    public int remaining(String team) {
        Integer i = teams.get(team);
        if (i == null)
            throw new IllegalArgumentException();
        return r[i];
    }                // number of remaining games for given team
    public int against(String team1, String team2) { // number of remaining games between team1 and team2
        Integer i = teams.get(team1);
        Integer j = teams.get(team2);
        if (i == null || j == null)
            throw new IllegalArgumentException();
        return g[i][j];
    }

    public boolean isEliminated(String team) {
        st = null;
        Integer I = teams.get(team);
        if (I == null)
            throw new IllegalArgumentException();

        lastCalculated = I;
        int n = N-1;

        FlowNetwork G = new FlowNetwork(n*(n+1)/2+2);
        int p = 0, q = 0;
        int s = 0;
        for (int i = 0; i < N; i++) {
            if (i == I) continue;
            for (int j = i+1; j < N; j++) {
                if (j == I) continue;
                p = i; q = j;
                if (i > I) p--;
                if (j > I) q--;
                G.addEdge(new FlowEdge(0, 1 + s, g[i][j]));
                G.addEdge(new FlowEdge(1+s, 1+n*(n-1)/2+p, Integer.MAX_VALUE));
                G.addEdge(new FlowEdge(1+s, 1+n*(n-1)/2+q, Integer.MAX_VALUE));
                s++;
            }
        }

        st = new Stack<String>();

        for (int i = 0; i < N; i++) {
            if (i == I) continue;
            p = i;
            if (i > I) p--;
            if (w[I]+r[I]-w[i] >= 0)
                G.addEdge(new FlowEdge(1+n*(n-1)/2+p, 1+n*(n+1)/2, w[I]+r[I]-w[i]));
            else {
                st.push(name[p >= I ? p+1 : p]); //trivial case
                return true;
            }
        }

        boolean isEliminated = false;

        FordFulkerson ff = new FordFulkerson(G, 0, n*(n+1)/2 + 1);

        for (FlowEdge fe : G.adj(0)) {
            if (fe.capacity() != fe.flow())
                isEliminated = true;
        }

        if (!isEliminated)
            st = null;
        else {
            for (FlowEdge fe : G.adj(n*(n+1)/2+1)) {
                int tmp = fe.from()-1-n*(n-1)/2;
                if (tmp >= I) tmp++;
                if (ff.inCut(fe.from()))
                    st.push(name[tmp]);
                //StdOut.println(fe.capacity() + " " + fe.flow() + " " + name[tmp]);
            }
        }

        return isEliminated;
    }

    public Iterable<String> certificateOfElimination(String team) { // subset R of teams that eliminates given team; null if not eliminated
        Integer I = teams.get(team);
        if (I == null)
            throw new IllegalArgumentException();

        if (I != lastCalculated)
            isEliminated(team);

        return st;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
        //String team = "Yale";
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
