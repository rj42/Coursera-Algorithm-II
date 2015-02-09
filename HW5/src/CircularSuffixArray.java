import java.util.Arrays;

/**
 * Created by lb_k on 4/5/14.
 */

public class CircularSuffixArray {
    SuffixArrayX sfx;
    //Manber mnb;
    String s;
    public CircularSuffixArray(String s) {
        sfx = new SuffixArrayX(s);
        //mnb = new Manber(s);
        this.s = s;
    }

    public int length() {
        return s.length();
    }

    public int index(int i) {
        return sfx.index(i);
        //return mnb.index[i];
    }

    public static void main(String[] args) {
        int N = 2048000/1024;
        char[] chars = new char[N];
        for (int i = 0; i < N; i++)
            chars[i] = (char)(Math.random()*255);
        Stopwatch sw = new Stopwatch();
        CircularSuffixArray CSA = new CircularSuffixArray(new String(chars));
        StdOut.println("Full Elapsed time - " + sw.elapsedTime());

        //for (int i = 0; i < CSA.length(); i++)
        //   StdOut.println(        CSA.sfx.select(i) + " " + CSA.sfx.index(i));
    }
}
