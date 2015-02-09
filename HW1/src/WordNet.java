import java.util.HashMap;

public class WordNet {
    // constructor takes the name of the two input files
    private TST<Bag<Integer>> dict = new TST<Bag<Integer>>();
    //private ST<Integer, String> ids = new ST<Integer, String>(); //definiton by id
    private HashMap<Integer, Queue<String>> map = new HashMap<Integer, Queue<String>>(); //name by id
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        In in = new In(synsets);
        while (!in.isEmpty()) {
            String str = in.readLine();
            String[] s = str.split(",");
            int id = Integer.parseInt(s[0]);
            String mid = s[1];
            //String def = s[2];

            s = mid.split(" ");
            Queue<String> words = new Queue<String>();
            for (String x : s)
                words.enqueue(x);

            for (String x : words) {
                Bag<Integer> set;
                if (!dict.contains(x)) {
                    set = new Bag<Integer>();
                    dict.put(x, set);
                }
                else
                    set = dict.get(x);
                set.add(id);
            }

            //ids.put(id, def);
            map.put(id, words);
        }
        in.close();

        int[] isRoot = new int[dict.size()];
        Digraph G = new Digraph(dict.size());

        in = new In(hypernyms);
        while (!in.isEmpty()) {
            String str = in.readLine();
            String[] s = str.split(",");
            int fr = Integer.parseInt(s[0]);
            isRoot[fr] = 1;
            for (int i = 1; i < s.length; i++) {
                int tmp = Integer.parseInt(s[i]);
                G.addEdge(fr, tmp);
                if (isRoot[tmp] == 0)
                    isRoot[tmp] = 2;
            }
        }
        in.close();

        int tmp = 0;
        for (int i = 0; i < dict.size(); i++)
            tmp += (isRoot[i] == 2) ? 1 : 0;

        Topological top = new Topological(G);
        if (!top.hasOrder() || tmp > 1) //why >1 ?
            throw new java.lang.IllegalArgumentException();

        sap = new SAP(G);
    }

    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns() {
        return dict.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return dict.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        Bag<Integer> v = dict.get(nounA);
        Bag<Integer> w = dict.get(nounB);
        if (v == null || w == null)
            throw new java.lang.IllegalArgumentException();
        return sap.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        Bag<Integer> v = dict.get(nounA);
        Bag<Integer> w = dict.get(nounB);
        if (v == null || w == null)
            throw new java.lang.IllegalArgumentException();

        int anc = sap.ancestor(v, w);
        if (anc == -1)
            return null;
        else {
            String str = "";
            for (String s : map.get(anc))
                str += s + " ";
            return str.substring(0, str.length()-1);
        }
    }


    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms100K.txt");
        //for (String str : wn.nouns())
        //    StdOut.println(str);

        String str1 = "narrow-leaved_water_plantain";
        String str2 = "buffet";

        StdOut.println(wn.dict.get(str1) + " " + wn.dict.get(str2));
        StdOut.println(wn.sap("glucagon", "head_register"));

        StdOut.println(wn.distance("glucagon", "head_register"));
    }
}
