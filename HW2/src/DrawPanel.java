public class DrawPanel {
    public static void main(String[] args) {
        Picture pic = new Picture(args[0]);
        StdDraw.setCanvasSize(pic.width(), pic.height());
        StdDraw.setXscale(0, pic.width());
        StdDraw.setYscale(0, pic.height());

        StdDraw.show(0);

        for (int x = 0; x < pic.width(); x++) {
            for (int y = 0; y < pic.height(); y++) {
                StdDraw.setPenColor(pic.get(x, y));
                StdDraw.point(x, pic.height() - y);
            }
        }

        SeamCarver sc = new SeamCarver(pic);

        StdDraw.show(0);
        while (true) {

            // mouse click
            /*if (StdDraw.mousePressed()) StdDraw.setPenColor(StdDraw.CYAN);
            else                        StdDraw.setPenColor(StdDraw.BLUE);

            // mouse location
            StdDraw.clear();
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            StdDraw.filledCircle(x, y, .05);
            StdDraw.show(10);*/
            //StdOut.println(StdDraw.nextKeyTyped());;
            for (int i = 0; i < 255; i++)
                if (StdDraw.isKeyPressed(i))
                    StdOut.println(i + " " + (int)'a');
            if (StdDraw.mousePressed() || StdDraw.isKeyPressed(37)) {
                //for (int i = 0; i < 1; i++)
                    sc.removeVerticalSeam(sc.findVerticalSeam());
                    //sc.removeHorizontalSeam(sc.findHorizontalSeam());
                pic = sc.picture();
                StdDraw.setCanvasSize(pic.width(), pic.height());
                StdDraw.setXscale(0, pic.width());
                StdDraw.setYscale(0, pic.height());

                StdDraw.show(0);

                for (int x = 0; x < pic.width(); x++) {
                    for (int y = 0; y < pic.height(); y++) {
                        StdDraw.setPenColor(pic.get(x, y));
                        StdDraw.point(x, pic.height() - y);
                    }
                }
            }
            StdDraw.show(10);
        }
    }
}