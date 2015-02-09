public class Outcast {
    // constructor takes a WordNet object
    private WordNet wordnet;
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[][] d = new int[nouns.length][nouns.length];
        int D = 0;
        int max = 0, k = 0;
        for (int i = 0; i < nouns.length; i++) {
            for (int j = 0; j < nouns.length; j++) {
                if (i <= j)
                    d[i][j] = wordnet.distance(nouns[i], nouns[j]);
                else
                    d[i][j] = d[j][i];
                D += d[i][j];
            }
            if (D > max) {
                k = i;
                max = D;
            }
            D = 0;
        }

        return nouns[k];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
