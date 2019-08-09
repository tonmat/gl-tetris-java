package tonmat.game;

public class Game {
    private static final Shape[] SHAPES;

    static {
        SHAPES = new Shape[]{
                new Shape(4, 0b0000111100000000),
                new Shape(3, 0b111001000),
                new Shape(3, 0b001111000),
                new Shape(3, 0b100111000),
                new Shape(3, 0b110011000),
                new Shape(3, 0b011110000),
                new Shape(2, 0b1111),
        };
    }

    public final In in;
    public final Board board;
    public final Player player;
    public final Player shadow;
    public final Shape hold;
    public final Shape tempShape;
    public int level;
    public int count;
    public float time;
    public int score;
    public float fallCD;
    public float clearCD;
    public float clearTime;
    public int clearY0;
    public int clearY1;
    public Shape next;
    public boolean canHold;
    public boolean gameOver;

    public Game(Config config) {
        in = new In();
        board = new Board(config.board);
        player = new Player();
        shadow = new Player();
        hold = new Shape();
        tempShape = new Shape();
        reset();
    }

    private static Shape randomShape() {
        return SHAPES[(int) (Math.random() * SHAPES.length)];
    }

    public void reset() {
        in.reset();
        level = 1;
        count = 0;
        time = 0;
        score = 0;
        fallCD = 0;
        clearCD = 0;
        clearTime = 0;
        clearY0 = 0;
        clearY1 = 0;
        board.reset();
        player.reset();
        shadow.reset();
        next = randomShape();
        canHold = false;
        hold.reset();
        tempShape.reset();
        gameOver = false;
    }

    private float nextFallCD() {
        return 0.1f + 0.9f * (99 - level) / 98;
    }

    private void calcShadow() {
        shadow.set(player);
        do {
            shadow.y--;
        } while (board.checkPlayer(shadow));
        shadow.y++;
    }

    private void setPlayer(Shape shape) {
        player.set(shape);
        player.x = (board.width - player.size) / 2;
        player.y = (board.height - player.size) / 2;
        do {
            player.y++;
        } while (board.checkPlayer(player, true));
        player.y--;
        calcShadow();
    }

    private void placePlayer() {
        if (player.size > 0) {
            final int rows = board.placePlayer(player);
            count++;
            if (count % 8 == 0)
                if (level < 99)
                    level++;
            score += 1 << rows * 2;
            if (rows > 0) {
                clearTime = this.time;
                clearCD = .6f;
                clearY0 = this.player.y;
                clearY1 = this.player.y + this.player.size - 1;
                player.reset();
                shadow.reset();
                return;
            }
        }
        setPlayer(next);
        next = randomShape();
        canHold = true;
    }

    private void setGameOver() {
        gameOver = true;
        player.reset();
        shadow.reset();
    }

    public void update(float delta) {
        if (gameOver)
            return;
        time += delta;
        if (clearCD == 0) {
            updateInMoveX(delta);
            updateInRotate(delta);
            updateInFall(delta);
            updateInDrop(delta);
            updateInHold(delta);
            updateFall(delta);
        } else {
            updateClear(delta);
        }
        in.reset();
    }

    private void updateInMoveX(float delta) {
        if (in.moveX != 0) {
            player.x += in.moveX;
            if (board.checkPlayer(player)) {
                calcShadow();
                return;
            }
            player.x -= in.moveX;
        }
    }

    private void updateInRotate(float delta) {
        if (in.rotate != 0) {
            player.rotate(in.rotate);
            if (board.checkPlayer(player)) {
                calcShadow();
                return;
            }
            player.rotate(-in.rotate);
        }
    }

    private void updateInFall(float delta) {
        if (in.fall) {
            fallCD = 0;
        }
    }

    private void updateInDrop(float delta) {
        if (in.drop) {
            fallCD = 0;
            player.set(shadow);
        }
    }

    private void updateInHold(float delta) {
        if (in.hold) {
            if (!canHold)
                return;
            canHold = false;
            if (hold.size > 0)
                tempShape.set(hold);
            hold.set(player);
            if (tempShape.size == 0) {
                setPlayer(next);
                next = randomShape();
            } else
                setPlayer(tempShape);
            tempShape.reset();
        }
    }

    private void updateFall(float delta) {
        fallCD -= delta;
        if (fallCD <= 0) {
            fallCD += nextFallCD();
            if (player.size > 0) {
                player.y--;
                if (board.checkPlayer(player))
                    return;
                player.y++;
            }
            placePlayer();
            if (board.checkPlayer(player))
                return;
            placePlayer();
            setGameOver();
        }
    }

    private void updateClear(float delta) {
        this.clearCD -= delta;
        if (clearCD <= 0) {
            clearCD = 0;
            for (int y = clearY1; y >= clearY0; y--)
                if (board.isFullRow(y))
                    board.removeRow(y);
            setPlayer(next);
            next = randomShape();
            canHold = true;
        }
    }

    public static class In {
        public float moveX;
        public int rotate;
        public boolean fall;
        public boolean drop;
        public boolean hold;

        public void reset() {
            moveX = 0;
            rotate = 0;
            fall = false;
            drop = false;
            hold = false;
        }
    }

    public static class Config {
        public Board.Config board = new Board.Config();
    }
}
