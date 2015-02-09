/**
 * Created by lb_k on 4/5/14.
 */

public class MoveToFront {
    private static final int R = 256;
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        byte[] alphabet = new byte[R];
        byte[] count    = new byte[R];

        for (int i = 0; i < R; i++) {
            alphabet[i] = (byte)i;
            count[i] = (byte)i;
        }

        while (!BinaryStdIn.isEmpty()) {
            byte ch = BinaryStdIn.readByte();
            byte c = count[ch & 0xFF];
            BinaryStdOut.write(c);

            for (int j = (int)(c & 0xFF); j >= 0; j--) {
                if (j > 0) {
                    alphabet[j] = alphabet[j-1];
                    count[(int)(alphabet[j-1] & 0xFF)] = (byte)j;
                }
                else {
                    alphabet[0] = ch;
                    count[(int)(ch & 0xFF)] = 0;
                }
            }
        }

        BinaryStdOut.flush();
    }

    private static byte[] encode(byte[] str)
    {
        byte[] alphabet = new byte[R];
        byte[] count    = new byte[R];
        byte[] code     = new byte[str.length];

        for (int i = 0; i < R; i++) {
            alphabet[i] = (byte)i;
            count[i] = (byte)i;
        }

        for (int i = 0; i < str.length; i++) {
            byte ch = str[i];
            byte c = count[ch & 0xFF];
            code[i] = c;
            for (int j = (int)(c & 0xFF); j >= 0; j--) {
                if (j > 0) {
                    alphabet[j] = alphabet[j-1];
                    count[(int)(alphabet[j-1] & 0xFF)] = (byte)j;
                }
                else {
                    alphabet[j] = ch;
                    count[(int)(ch & 0xFF)] = (byte)j;
                }
            }

            /*for (int j = (byte)'A'; j < (byte)'Z'; j++) {
                StdOut.print(alphabet[j] + " ");
            }
            StdOut.println(code[i]);*/
        }

        return code;
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        byte[] alphabet = new byte[R];
        byte[] count    = new byte[R];

        for (int i = 0; i < R; i++) {
            alphabet[i] = (byte)i;
            count[i] = (byte)i;
        }

        while (!BinaryStdIn.isEmpty()) {
            int pos = (BinaryStdIn.readByte() & 0xFF);
            byte ch = alphabet[pos];
            BinaryStdOut.write(ch);

            for (int j = pos; j >= 0; j--) {
                if (j > 0) {
                    alphabet[j] = alphabet[j-1];
                    count[alphabet[j-1] & 0xFF] = (byte)j;
                }
                else {
                    alphabet[0] = ch;
                    count[ch & 0xFF] = 0;
                }
            }
        }

        BinaryStdOut.close();
    };

    private static byte[] decode(byte[] code) {
        byte[] alphabet = new byte[R];
        byte[] count    = new byte[R];
        byte[] str     = new byte[code.length];

        for (int i = 0; i < R; i++) {
            alphabet[i] = (byte)i;
            count[i] = (byte)i;
        }

        for (int i = 0; i < code.length; i++) {
            int pos = code[i] & 0xFF;
            byte ch = alphabet[pos];
            str[i] = ch;

            for (int j = pos; j >= 0; j--) {
                if (j > 0) {
                    alphabet[j] = alphabet[j-1];
                    count[alphabet[j-1] & 0xFF] = (byte)j;
                }
                else {
                    alphabet[j] = ch;
                    count[ch & 0xFF] = (byte)j;
                }
            }
        }

        //StdOut.println(new String(str));
        return str;
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
