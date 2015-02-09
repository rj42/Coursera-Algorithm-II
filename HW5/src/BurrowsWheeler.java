/**
 * Created by lb_k on 4/5/14.
 */

public class BurrowsWheeler {
    private final static int R = 256;
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode() {
        Queue<Byte> q = new Queue<Byte>();
        while (!BinaryStdIn.isEmpty()) {
            q.enqueue(BinaryStdIn.readByte());
        }

        byte[] s = new byte[q.size()];
        int i = 0;

        for (byte b : q)
            s[i++] = b;

        byte[] c = encode(s);

        for (byte b : c)
            BinaryStdOut.write(b);

        BinaryStdOut.close();
    }

    private static byte[] encode(byte[] s) {
        StringBuilder sb = new StringBuilder(); //very strange, we can't use constructor on byte for string
        for (byte b : s)
            sb.append((char)b);
        String str = sb.toString();

        CircularSuffixArray CSA = new CircularSuffixArray(str);
        int len = str.length();
        int first = 0;
        byte[] c = new byte[len+4];

        for (int i = 0; i < len; i++) {
            c[4 + i] = (byte)str.charAt((CSA.index(i)+len-1) % len);
            if (CSA.index(i) == 0)
                first = i;
        }

        for (int i = 0; i < 4; i++)
            c[3-i] = (byte)(first >>> (i * 8));

        return c;
    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode() {
        int first = BinaryStdIn.readInt();
        Queue<Byte> q = new Queue<Byte>();
        while (!BinaryStdIn.isEmpty()) {
            q.enqueue(BinaryStdIn.readByte());
        }

        byte[] s = new byte[q.size()];
        int i = 0;
        for (byte b : q)
            s[i++] = b;

        byte[] output = decode(first, s);
        for (byte b : output)
            BinaryStdOut.write(b);
        BinaryStdOut.flush();
    }

    private static byte[] decode(int first, byte[] t) {
        int len = t.length;
        int[] count = new int[R];
        int[] next = new int[len];
        byte[] c    = new byte[len];

        for (int i = 0; i < len; i++) //counting sort
            count[t[i] & 0xFF]++;

        for (int i = 0; i < R-1; i++)
            count[i+1] += count[i];

        for (int i = len-1; i >= 0; i--) {
            next[--count[t[i] & 0xFF]] = i;
        }

        int i = next[first], n = 0;
        while (n < len) {
            c[n] = t[i];
            i = next[i];
            n++;
        }

        return c;
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args)
    {
        //Stopwatch sw = new Stopwatch();
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
        //StdOut.println("Elapsed time - " + sw.elapsedTime());
    }
}