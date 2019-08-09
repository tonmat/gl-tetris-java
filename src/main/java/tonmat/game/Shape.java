package tonmat.game;

public class Shape {
    public int size;
    public int mask;
    public float centerX;
    public float centerY;

    public Shape() {
        this(0, 0);
    }

    public Shape(int size, int mask) {
        this.size = size;
        this.mask = mask;
        calcCenter();
    }

    public void reset() {
        size = 0;
        mask = 0;
        centerX = 0;
        centerY = 0;
    }

    private void calcCenter() {
        int xMin = size - 1;
        int yMin = size - 1;
        int xMax = 0;
        int yMax = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!hasCell(x, y))
                    continue;
                xMin = Math.min(xMin, x);
                yMin = Math.min(yMin, y);
                xMax = Math.max(xMax, x + 1);
                yMax = Math.max(yMax, y + 1);
            }
        }
        centerX = (xMin + xMax) / 2f;
        centerY = (yMin + yMax) / 2f;
    }

    public void set(Shape shape) {
        size = shape.size;
        mask = shape.mask;
        calcCenter();
    }

    public boolean hasCell(int x, int y) {
        return (mask >>> x + y * size & 1) == 1;
    }

    public void rotate(int direction) {
        if (direction < 0)
            rotateCCW();
        else if (direction > 0)
            rotateCW();
    }

    private void rotateCW() {
        int mask = 0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!hasCell(x, y))
                    continue;
                final int ny = size - 1 - x;
                final int nx = y;
                mask |= 1 << (nx + ny * size);
            }
        }
        this.mask = mask;
        calcCenter();
    }

    private void rotateCCW() {
        int mask = 0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!hasCell(x, y))
                    continue;
                final int ny = x;
                final int nx = size - 1 - y;
                mask |= 1 << (nx + ny * size);
            }
        }
        this.mask = mask;
        calcCenter();
    }
}
