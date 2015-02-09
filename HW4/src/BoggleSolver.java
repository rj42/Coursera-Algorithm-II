import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by lb_k on 4/5/14.
 */

public class BoggleSolver
{
    private MyTrieSET tr = new MyTrieSET();
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String str : dictionary) {
            tr.add(str);
        }
    }

    //M - rows, N - cols
    private static class Word {
        //private byte[] mask;
        private Word prev = null;
        private int mask1 = 0, mask2 = 0, mask3 = 0;

        private static int M, N; //size
        private int i, j; //last position in game
        private MyTrieSET.Node node = new MyTrieSET.Node();
        private byte count = 0; //moves done

        public Word(int M, int N, int i, int j) {
            //mask = new byte[M*N];

            this.i = i;
            this.j = j;
            this.M = M;
            this.N = N;
        }

        public Word(Word w) {
            this.i = w.i;
            this.j = w.j;
            this.M = w.M;
            this.N = w.N;
            this.count = w.count;

            //mask = new byte[M*N];
            //for (int i = 0; i < M*N; i++)
            //    mask[i] = w.mask[i];
            this.prev = w;
            mask1 = w.mask1;
            mask2 = w.mask2;
            mask3 = w.mask3;

            this.node = w.node;
        }

        public String toString(char[][] brd) {
            char[] ch = new char[count];

            /*for (int i = 0; i < M; i++) {
                for (int j = 0; j < N; j++) {
                    if (mask[i*N+j] != 0) {
                        char c = board.getLetter(i, j);
                        if (c == 'Q') {
                            ch[mask[i*N+j]-2] = 'Q';
                            ch[mask[i*N+j]-1] = 'U';
                        }
                        else
                            ch[mask[i*N+j]-1] = c;
                    }
                }
            }*/
            Word tmp = this; //previous
            int t = count;
            while (tmp != null) {
                int i = tmp.i, j = tmp.j;
                char c = brd[i][j];
                if (c == 'Q') {
                    ch[--t] = 'U';
                    ch[--t] = 'Q';
                }
                else
                    ch[--t] = c;
                tmp = tmp.prev;
            }

            return new String(ch, 0, count);
        }

        public void visit(int i, int j, boolean isQ) {
            count++;
            if (isQ)
                count++;
            this.i = i;
            this.j = j;

            //mask[i*N+j] = count;
            if (i*N+j <= 31)
                mask1 |= 1 << (i*N+j);
            else if (i*N+j <= 63)
                mask2 |= 1 << (i*N+j-32);
            else
                mask3 |= 1 << (i*N+j-64);
        }

        public boolean isVisited(int i, int j) {
            //return mask[i*N+j] != 0;

            if (i*N + j <= 31)
                return (mask1 & (1 << (i*N+j))) != 0;
            else if (i*N + j <= 63)
                return (mask2 & (1 << (i*N+j-32))) != 0;
            else
                return (mask3 & (1 << (i*N+j-64))) != 0;
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Queue<String> set = new Queue<String>();
        tr.mark++;

        int M = board.rows();
        int N = board.cols();

        int[] stepI = { -1, 1,  0, 0, -1, -1,  1, 1};
        int[] stepJ = {  0, 0, -1, 1, -1,  1, -1, 1};

        char[][] brd = new char[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                brd[i][j] = board.getLetter(i, j);
            }
        }

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                //Stack<Word> q = new Stack<Word>();
                Queue<Word>q = new Queue<Word>();

                char ch = brd[i][j];
                Word word = new Word(M, N, i, j);
                word.visit(i, j, ch == 'Q');
                word.node = tr.getRoot().next[ch-'A'];
                if (ch == 'Q' && word.node != null) //Qu case
                    word.node = word.node.next['U'-'A'];
                if (word.node == null)
                    continue;
                q.enqueue(word);
                //q.push(word);

                while (!q.isEmpty()) {
                    Word w = q.dequeue();
                    //Word w = q.pop();
                    //looking for neighbours
                    int posI = w.i, posJ = w.j;

                    for (int r = 0; r < 8; r++) { //next neigbhours
                        int nextI = posI + stepI[r];
                        int nextJ = posJ + stepJ[r];

                        if (nextI < 0 || nextI >= M || nextJ < 0 || nextJ >= N)
                            continue;

                        if (w.isVisited(nextI, nextJ))
                            continue;

                        ch = brd[nextI][nextJ];
                        MyTrieSET.Node node = w.node.next[ch-'A'];

                        if (ch == 'Q' && node != null) //Qu case
                            node = node.next['U'-'A'];

                        if (node != null) {
                            Word newW = new Word(w);
                            newW.visit(nextI, nextJ, ch == 'Q');
                            newW.node = node;
                            q.enqueue(newW);
                            //q.push(newW);

                            if (node.isString && newW.count >= 3 && node.numOfMark != tr.mark) {
                                String str = newW.toString(brd);
                                set.enqueue(str);
                                node.numOfMark = tr.mark;
                            }
                        }
                    }
                }
            }
        }

        return set;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!tr.contains(word))
            return 0;
        int len = word.length();
        if (len <= 2) return 0;
        if (len >= 3 && len <= 4) return 1;
        if (len == 5) return 2;
        if (len == 6) return 3;
        if (len == 7) return 5;
        if (len >= 8) return 11;

        return 0;
    }

    public static void main(String[] args)
    {
        //2.5 --> 0.094 (own structure) --> 0.083 (no str) --> 0.16 on hash
        In in = new In("dictionary-yawl.txt");
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 1000; i++) {
            BoggleBoard board = new BoggleBoard();
            int score = 0;
            solver.getAllValidWords(board);
            /*for (String word : solver.getAllValidWords(board))
            {
                StdOut.println(word);
                score += solver.scoreOf(word);
            }
            StdOut.println("Score = " + score);*/
        }
        StdOut.println("Elapsed time - " + sw.elapsedTime());
    }
}