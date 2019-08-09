package tonmat.game;

import java.util.Arrays;

public class Board {
    public final int width;
    public final int height;
    public final int[] rows;

    public Board(Config config) {
        width = config.width;
        height = config.height;
        rows = new int[height + 1];
    }

    public void reset() {
        Arrays.fill(rows, 0);
    }

    public void setCell(int x, int y, boolean cell) {
        if (cell)
            rows[y] |= 1 << x;
        else
            rows[y] &= ~(1 << x);
    }

    public boolean isFullRow(int y) {
        return y >= 0 && y < height && rows[y] == (1 << width) - 1;
    }

    public void removeRow(int y) {
        if (y >= 0 && y < height)
            System.arraycopy(rows, y + 1, rows, y, height - y);
    }

    public boolean hasCell(int x, int y) {
        return (rows[y] >>> x & 1) == 1;
    }

    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    public boolean checkPlayer(Player player) {
        return checkPlayer(player, false);
    }

    public boolean checkPlayer(Player player, boolean ignoreRows) {
        for (int y = 0; y < player.size; y++) {
            for (int x = 0; x < player.size; x++) {
                if (!player.hasCell(x, y))
                    continue;
                final int bx = player.x + x;
                final int by = player.y + y;
                if (isOutOfBounds(bx, by))
                    return false;
                if (!ignoreRows)
                    if (hasCell(bx, by))
                        return false;
            }
        }
        return true;
    }

    public int placePlayer(Player player) {
        int rows = 0;
        for (int y = 0; y < player.size; y++) {
            final int by = player.y + y;
            for (int x = 0; x < player.size; x++) {
                if (!player.hasCell(x, y))
                    continue;
                final int bx = player.x + x;
                if (isOutOfBounds(bx, by))
                    continue;
                setCell(bx, by, true);
            }

            if (isFullRow(by))
                rows++;
        }
        return rows;
    }

    public static class Config {
        public int width = 10;
        public int height = 20;
    }
}
