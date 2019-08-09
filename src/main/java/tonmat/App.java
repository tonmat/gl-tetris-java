package tonmat;

import tonmat.game.Game;
import tonmat.game.Player;
import tonmat.game.Shape;
import tonmat.graphics.*;
import tonmat.inputs.Inputs;
import tonmat.mat4f.Mat4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class App {
    private static int BOARD_HEIGHT = 20;
    private static int BOARD_WIDTH = 10;
    private static Mat4f projection = new Mat4f();
    private static Shader shader;
    private static Texture texture;
    private static Game tetris;
    private static Sprites sprites;
    private static Labels labels;
    private static Batch batch;
    private static boolean paused;

    public static String formatTime(double t) {
        var s = (int) t;
        var m = (int) (s / 60f);
        s %= 60;
        return String.format("%01d:%02d", m, s);
    }

    private static void initialize() {
        Inputs.initialize();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glClearColor(0, .025f, 0, 1);
        glActiveTexture(GL_TEXTURE0);
        glViewport(0, 0, 600, 800);

        projection.setOrtho(0, 15, 0, 20);

        shader = new Shader("shaders/shader.vert", "shaders/shader.frag");
        shader.bind();
        shader.setUniformMat4f("u_projection", projection);
        shader.setUniform1i("u_material", 0);

        texture = new Texture("textures/spritesheet.png");
        texture.bind();

        tetris = new Game(new Game.Config());

        sprites = new Sprites();
        labels = new Labels();

        batch = new Batch(2048);
    }

    private static Sprite createBGSprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 1, 1);
        sprite.color.set(1, 1, 1);
        return sprite;
    }

    private static Sprite createBG2Sprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 1, 1);
        sprite.color.set(.9f, .95f, .9f);
        return sprite;
    }

    private static Sprite createBG3Sprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 1, 1);
        sprite.color.set(.2f, .3f, .2f);
        sprite.alpha = .75f;
        return sprite;
    }

    private static Sprite createBG4Sprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 1, 1);
        sprite.color.set(.08f, .10f, .08f);
        sprite.alpha = .96f;
        return sprite;
    }

    private static Sprite createBoardBGPieceSprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 8, 8);
        sprite.color.set(.975f, .96f, .975f);
        return sprite;
    }

    private static Sprite createBoardFGPieceSprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 8, 8);
        sprite.color.set(0, .1f, 0);
        return sprite;
    }

    private static Sprite createClearPieceSprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 8, 8);
        return sprite;
    }

    private static Sprite createPlayerPieceSprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 8, 8);
        return sprite;
    }

    private static Sprite createShadowPieceSprite() {
        final Sprite sprite = new Sprite();
        sprite.textureRegion = texture.region(0, 64, 8, 8);
        return sprite;
    }

    private static Label createTimeLabel() {
        final Label label = new Label();
        label.text = "TIME";
        label.x = 4.5f;
        label.y = BOARD_HEIGHT - 2;
        label.size = .5f;
        label.hAlign = Label.HAlign.RIGHT;
        label.color.set(.5f, .6f, .5f);
        return label;
    }

    private static Label createTimeValueLabel() {
        final Label label = new Label();
        label.x = 4.5f;
        label.y = BOARD_HEIGHT - 3;
        label.hAlign = Label.HAlign.RIGHT;
        label.color.set(.9f, 1, .9f);
        return label;
    }

    private static Label createScoreLabel() {
        final Label label = new Label();
        label.text = "SCORE";
        label.x = 4.5f;
        label.y = BOARD_HEIGHT - 4;
        label.size = .5f;
        label.hAlign = Label.HAlign.RIGHT;
        label.color.set(.5f, .6f, .5f);
        return label;
    }

    private static Label createScoreValueLabel() {
        final Label label = new Label();
        label.x = 4.5f;
        label.y = BOARD_HEIGHT - 5;
        label.hAlign = Label.HAlign.RIGHT;
        label.color.set(.9f, 1, .9f);
        return label;
    }

    private static Label createLevelLabel() {
        final Label label = new Label();
        label.text = "LEVEL";
        label.x = 4.5f;
        label.y = BOARD_HEIGHT - 6;
        label.size = .5f;
        label.hAlign = Label.HAlign.RIGHT;
        label.color.set(.5f, .6f, .5f);
        return label;
    }

    private static Label createLevelValueLabel() {
        final Label label = new Label();
        label.x = 4.5f;
        label.y = BOARD_HEIGHT - 7;
        label.hAlign = Label.HAlign.RIGHT;
        label.color.set(.9f, 1, .9f);
        return label;
    }

    private static Label createNextLabel() {
        final Label label = new Label();
        label.text = "NEXT";
        label.x = 0;
        label.y = 5.25f;
        label.size = .75f;
        label.hAlign = Label.HAlign.LEFT;
        label.color.set(.5f, .6f, .5f);
        return label;
    }

    private static Label createHoldLabel() {
        final Label label = new Label();
        label.text = "HOLD";
        label.x = 0;
        label.y = 11.5f;
        label.size = .75f;
        label.hAlign = Label.HAlign.LEFT;
        label.color.set(.5f, .6f, .5f);
        return label;
    }

    private static Label createGameOverLabel() {
        final Label label = new Label();
        label.text = "GAME\nOVER";
        label.x = 5 + BOARD_WIDTH / 2;
        label.y = 1 + BOARD_HEIGHT / 2;
        label.vSpacing = 1;
        label.size = 3;
        label.hAlign = Label.HAlign.CENTER;
        label.color.set(.5f, 1, .5f);
        return label;
    }

    public static void update(float delta) {
        var reset = false;
        var dx = 0;
        var rotate = 0;
        var fall = false;
        var drop = false;
        var hold = false;

        Inputs.poolEvents(delta);
        if (Inputs.isDown(GLFW_KEY_P, 1, 1)) paused = !paused;
        if (Inputs.isDown(GLFW_KEY_R, 1, 1)) reset = true;
        if (Inputs.isDown(GLFW_KEY_LEFT, 0.16f, 0.04f)) dx--;
        if (Inputs.isDown(GLFW_KEY_RIGHT, 0.16f, 0.04f)) dx++;
        if (Inputs.isDown(GLFW_KEY_DOWN, 0.16f, 0.04f)) fall = true;
        if (Inputs.isDown(GLFW_KEY_UP, 1, 1)) drop = true;
        if (Inputs.isDown(GLFW_KEY_LEFT_CONTROL, 1, 1)) hold = true;
        if (Inputs.isDown(GLFW_KEY_SPACE, 0.16f, 0.04f)) rotate = 1;

        if (tetris.gameOver)
            paused = false;

        if (reset)
            tetris.reset();
        if (!paused) {
            tetris.in.moveX = dx;
            tetris.in.rotate = rotate;
            tetris.in.fall = fall;
            tetris.in.drop = drop;
            tetris.in.hold = hold;
            tetris.update(delta);
        }
    }

    public static void renderBoard() {
        renderBG(sprites.bg, 5, 0, tetris.board.width, tetris.board.height);
        for (var y = 0; y < tetris.board.height; y++) {
            var isFullRow = tetris.board.isFullRow(y);
            for (var x = 0; x < tetris.board.width; x++) {
                Sprite spt;
                if (paused)
                    spt = sprites.pieces.boardFG;
                else if (isFullRow)
                    spt = sprites.pieces.clear;
                else if (tetris.board.hasCell(x, y))
                    spt = sprites.pieces.boardFG;
                else
                    spt = sprites.pieces.boardBG;
                spt.x = x + 5;
                spt.y = y;
                batch.draw(spt);
            }
        }
    }

    public static void renderShape(Shape shape, Sprite spt, float wx, float wy) {
        if (shape instanceof Player) {
            wx += ((Player) shape).x;
            wy += ((Player) shape).y;
        }
        for (var y = 0; y < shape.size; y++)
            for (var x = 0; x < shape.size; x++) {
                if (shape.hasCell(x, y)) {
                    spt.x = wx + x;
                    spt.y = wy + y;
                    batch.draw(spt);
                }
            }
    }

    public static void renderShapeBox(Shape shape, Sprite spt, float x, float y) {
        renderBG(sprites.bg2, x, y, 4, 4);
        if (shape.size > 0) {
            var wx = x + 2 - shape.centerX;
            var wy = y + 2 - shape.centerY;
            renderShape(shape, spt, wx, wy);
        }
    }

    public static void renderBG(Sprite spt, float x, float y, float w, float h) {
        spt.x = x;
        spt.y = y;
        spt.width = w;
        spt.height = h;
        batch.draw(spt);
    }

    public static void renderTime() {
        labels.timeValue.text = formatTime(tetris.time);
        batch.draw(labels.time);
        batch.draw(labels.timeValue);
    }

    public static void renderScore() {
        labels.scoreValue.text = String.valueOf(tetris.score);
        batch.draw(labels.score);
        batch.draw(labels.scoreValue);
    }

    public static void renderLevel() {
        labels.levelValue.text = String.valueOf(tetris.level);
        batch.draw(labels.level);
        batch.draw(labels.levelValue);
    }

    public static void renderNext() {
        batch.draw(labels.next);
    }

    public static void renderHold() {
        batch.draw(labels.hold);
    }

    public static void renderGameOver() {
        batch.draw(labels.gameOver);
    }

    public static void render(double time) {
        var i1 = (float) Math.abs(Math.cos(.7 * time));
        var i2 = (float) Math.abs(Math.sin(1.7 * time));
        var i3 = 4 * (tetris.time - tetris.clearTime);
        float i4;
        sprites.pieces.player.color.set(.4f, .1f * i1 + .5f, .4f);
        sprites.pieces.shadow.color.set(.6f, .8f, .6f);
        sprites.pieces.shadow.alpha = .4f + .1f * i2;
        if (i3 < .5f) {
            i4 = i3 * 2;
            sprites.pieces.clear.color.set(.5f * i4, .1f + .9f * i4, .5f * i4);
            sprites.pieces.clear.alpha = 1;
        } else {
            i4 = Math.max(0, 1.5f - i3);
            sprites.pieces.clear.color.set(.5f, 1f, .5f);
            sprites.pieces.clear.alpha = i4;
        }

        glClear(GL_COLOR_BUFFER_BIT);
        batch.begin();

        renderBoard();
        if (!paused) {
            renderShape(tetris.shadow, sprites.pieces.shadow, 5, 0);
            renderShape(tetris.player, sprites.pieces.player, 5, 0);
            renderShapeBox(tetris.next, sprites.pieces.boardFG, 0, 1);
            renderShapeBox(tetris.hold, sprites.pieces.boardFG, 0, 7);
            if (!tetris.canHold)
                renderBG(sprites.bg3, 0, 7, 4, 4);
        }
        if (tetris.gameOver) {
            renderBG(sprites.bg4, 5, 0, tetris.board.width, tetris.board.height);
            renderBG(sprites.bg4, 0, 1, 4, 4);
            renderBG(sprites.bg4, 0, 7, 4, 4);
            renderGameOver();
        }
        renderTime();
        renderScore();
        renderLevel();
        renderNext();
        renderHold();

        batch.end();
    }

    public static void main(String[] args) {
        Graphics.initialize(new Graphics.Config() {
            {
                width = 600;
                height = 800;
                initializeCallback = App::initialize;
                updateCallback = App::update;
                renderCallback = App::render;
            }
        });
    }

    private static final class Sprites {
        public Sprite bg = createBGSprite();
        public Sprite bg2 = createBG2Sprite();
        public Sprite bg3 = createBG3Sprite();
        public Sprite bg4 = createBG4Sprite();
        public Pieces pieces = new Pieces();

        public static final class Pieces {
            public Sprite boardBG = createBoardBGPieceSprite();
            public Sprite boardFG = createBoardFGPieceSprite();
            public Sprite clear = createClearPieceSprite();
            public Sprite player = createPlayerPieceSprite();
            public Sprite shadow = createShadowPieceSprite();
        }
    }

    private static final class Labels {
        public Label time = createTimeLabel();
        public Label timeValue = createTimeValueLabel();
        public Label score = createScoreLabel();
        public Label scoreValue = createScoreValueLabel();
        public Label level = createLevelLabel();
        public Label levelValue = createLevelValueLabel();
        public Label next = createNextLabel();
        public Label hold = createHoldLabel();
        public Label gameOver = createGameOverLabel();
    }
}
