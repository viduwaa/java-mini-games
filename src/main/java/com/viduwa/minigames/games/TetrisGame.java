package com.viduwa.minigames.games;

import com.viduwa.minigames.dao.GameDAO;
import com.viduwa.minigames.session.Session;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.viduwa.minigames.Main.showMainMenu;

public class TetrisGame extends BorderPane {

    public static int gameID = 2;

    // Game constants
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int CELL_SIZE = 30;
    private static final int PREVIEW_SIZE = 4;

    // Game state
    private int[][] board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentX, currentY;
    private long lastDropTime;
    private long dropSpeed = 2000; // milliseconds
    private boolean isGameOver;
    private boolean isPaused;
    private int score;
    private int lines;
    private int level;
    private int currentHighScore = 0;

    // UI components
    private Canvas gameCanvas;
    private Canvas previewCanvas;
    private GraphicsContext gameGc;
    private GraphicsContext previewGc;
    private Label scoreLabel;
    private Label userHighScore;
    private Label linesLabel;
    private Label levelLabel;
    private Label gameOverLabel;
    private Button restartButton;
    private Button pauseButton;
    private Button backToMenuButton;

    private Runnable scoreUpdateCallback;

    private AnimationTimer gameTimer;
    private Random random;

    public TetrisGame() {
        random = new Random();
        initializeGame();
        setupUI();
        startNewGame();

        // Make focusable for key events
        setFocusTraversable(true);

        // Add key event handlers
        setOnKeyPressed(this::handleKeyPressed);

        // Request focus when clicked
        setOnMouseClicked(e -> requestFocus());
    }

    private void styleButtons(Button... buttons) {
        for (Button btn : buttons) {
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            btn.setTextFill(Color.WHITE);
            btn.setStyle(
                    "-fx-background-color: linear-gradient(#16213e, #0f3460);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-color: #e94560;" +
                            "-fx-border-width: 2;" +
                            "-fx-padding: 8 16;" +
                            "-fx-cursor: hand;"+
                            "-fx-min-width: 150"
            );

            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: linear-gradient(#1b1b2f, #16213e);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-color: #ff6f91;" +
                            "-fx-border-width: 2;" +
                            "-fx-padding: 8 16;" +
                            "-fx-cursor: hand;"+
                            "-fx-min-width: 150"
            ));

            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: linear-gradient(#16213e, #0f3460);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-color: #e94560;" +
                            "-fx-border-width: 2;" +
                            "-fx-padding: 8 16;" +
                            "-fx-cursor: hand;"+
                            "-fx-min-width: 150"
            ));
        }
    }


    private void setupUI() {
        setStyle("-fx-background-color: #1a1a2e;");

        //show highscore
        try (GameDAO dao = new GameDAO()) {
            currentHighScore = dao.getUserHighScoreForGame(Session.getUser().getId(), gameID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Game canvas
        gameCanvas = new Canvas(BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        gameGc = gameCanvas.getGraphicsContext2D();

        // Preview canvas
        previewCanvas = new Canvas(PREVIEW_SIZE * CELL_SIZE, PREVIEW_SIZE * CELL_SIZE);
        previewGc = previewCanvas.getGraphicsContext2D();

        // UI Labels
        scoreLabel = new Label("Score: 0");
        userHighScore = new Label("High Score: " + currentHighScore);
        linesLabel = new Label("Lines: 0");
        levelLabel = new Label("Level: 1");
        gameOverLabel = new Label("GAME OVER");

        styleLabels();

        // Buttons
        restartButton = new Button("Restart");
        pauseButton = new Button("Pause");
        backToMenuButton = new Button("Back to Menu");
        setupButtonHandlers();
        styleButtons(restartButton, pauseButton, backToMenuButton);


        // Layout
        VBox rightPanel = createRightPanel();
        VBox gameArea = new VBox(10);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.getChildren().add(gameCanvas);

        setCenter(gameArea);
        setRight(rightPanel);

        // Instructions
        Label instructionsLabel = new Label("Controls: WASD or Arrow Keys to move, Space to pause");
        instructionsLabel.setFont(Font.font("Arial", 12));
        instructionsLabel.setTextFill(Color.LIGHTGRAY);
        instructionsLabel.setStyle("-fx-padding: 10;");
        setBottom(instructionsLabel);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (isGameOver || isPaused) {
            if (event.getCode() == KeyCode.SPACE) {
                togglePause();
            } else if (event.getCode() == KeyCode.ENTER && isGameOver) {
                startNewGame();
            }
            return;
        }

        KeyCode code = event.getCode();
        switch (code) {
            case LEFT:
            case A:
                movePiece(-1, 0);
                break;
            case RIGHT:
            case D:
                movePiece(1, 0);
                break;
            case DOWN:
            case S:
                movePiece(0, 1);
                break;
            case UP:
            case W:
                rotatePiece();
                break;
            case SPACE:
                togglePause();
                break;
            case ENTER:
                if (isGameOver) {
                    startNewGame();
                }
                break;
        }
        event.consume();
    }

    private void styleLabels() {
        Font labelFont = new Font("Upheaval TT -BRK-", 30);
        Color textColor = Color.WHITE;

        scoreLabel.setFont(labelFont);
        scoreLabel.setTextFill(textColor);
        linesLabel.setFont(labelFont);
        linesLabel.setTextFill(textColor);
        levelLabel.setFont(labelFont);
        levelLabel.setTextFill(textColor);
        userHighScore.setFont(labelFont);
        userHighScore.setTextFill(textColor);

        gameOverLabel.setFont(Font.font("Upheaval TT -BRK-", FontWeight.BOLD, 18));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setVisible(false);
    }


    private VBox createRightPanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPrefWidth(300);

        Label nextLabel = new Label("Next:");
        nextLabel.setFont(Font.font("Upheaval TT -BRK-", FontWeight.BOLD, 14));
        nextLabel.setTextFill(Color.WHITE);

        rightPanel.getChildren().addAll(
                userHighScore,
                new Region(),
                scoreLabel, linesLabel, levelLabel,
                new Region(), // Spacer
                nextLabel, previewCanvas,
                new Region(), // Spacer
                pauseButton, restartButton,backToMenuButton,
                new Region(), // Spacer
                gameOverLabel
        );

        return rightPanel;
    }

    private void setupButtonHandlers() {
        restartButton.setOnAction(e -> startNewGame());
        pauseButton.setOnAction(e -> togglePause());
        backToMenuButton.setOnAction(e -> {
            System.out.println("Back to Menu");
            showMainMenu(Session.getUser().getUsername());
        });
    }

    private void initializeGame() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        isGameOver = false;
        isPaused = false;
        score = 0;
        lines = 0;
        level = 1;
        dropSpeed = 1000;
    }

    private void startNewGame() {
        initializeGame();
        currentPiece = getRandomTetromino();
        nextPiece = getRandomTetromino();
        spawnPiece();

        if (gameOverLabel != null) {
            gameOverLabel.setVisible(false);
        }
        if (pauseButton != null) {
            pauseButton.setText("Pause");
        }

        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused && !isGameOver) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastDropTime > dropSpeed) {
                        dropPiece();
                        lastDropTime = currentTime;
                    }
                }
                render();
            }
        };

        gameTimer.start();
        lastDropTime = System.currentTimeMillis();
        updateUI();
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (pauseButton != null) {
            pauseButton.setText(isPaused ? "Resume" : "Pause");
        }
    }

    private void spawnPiece() {
        currentX = BOARD_WIDTH / 2 - 1;
        currentY = 0;

        if (!isValidPosition(currentPiece, currentX, currentY)) {
            gameOver();
        }
    }

    private void gameOver() {
        isGameOver = true;
        if (gameOverLabel != null) {
            gameOverLabel.setVisible(true);
        }
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private boolean movePiece(int dx, int dy) {
        int newX = currentX + dx;
        int newY = currentY + dy;

        if (isValidPosition(currentPiece, newX, newY)) {
            currentX = newX;
            currentY = newY;
            return true;
        }
        return false;
    }

    private void rotatePiece() {
        Tetromino rotated = currentPiece.rotate();
        if (isValidPosition(rotated, currentX, currentY)) {
            currentPiece = rotated;
        }
    }

    private void dropPiece() {
        if (!movePiece(0, 1)) {
            placePiece();
            clearLines();
            currentPiece = nextPiece;
            nextPiece = getRandomTetromino();
            spawnPiece();
        }
    }

    private void placePiece() {
        int[][] shape = currentPiece.getShape();
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    int boardX = currentX + x;
                    int boardY = currentY + y;
                    if (boardY >= 0 && boardY < BOARD_HEIGHT && boardX >= 0 && boardX < BOARD_WIDTH) {
                        board[boardY][boardX] = currentPiece.getColor();
                    }
                }
            }
        }
    }

    private void clearLines() {
        List<Integer> linesToClear = new ArrayList<>();

        for (int y = 0; y < BOARD_HEIGHT; y++) {
            boolean fullLine = true;
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] == 0) {
                    fullLine = false;
                    break;
                }
            }
            if (fullLine) {
                linesToClear.add(y);
            }
        }

        if (!linesToClear.isEmpty()) {
            // Remove cleared lines
            for (int line : linesToClear) {
                for (int y = line; y > 0; y--) {
                    System.arraycopy(board[y - 1], 0, board[y], 0, BOARD_WIDTH);
                }
                // Clear top line
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    board[0][x] = 0;
                }
            }

            // Update score and level
            int clearedLines = linesToClear.size();
            lines += clearedLines;
            score += calculateScore(clearedLines);
            level = (lines / 10) + 1;
            dropSpeed = Math.max(50, 1000 - (level - 1) * 100);

            checkAndUpdateHighScore();

            updateUI();
        }
    }

    private int calculateScore(int lines) {
        int[] scoreTable = {0, 10, 20, 30, 40};
        return scoreTable[Math.min(lines, 4)] * level;
    }

    private void checkAndUpdateHighScore() {
        if (score > currentHighScore) {
            System.out.println("Updating new high score: " + score);
            try (GameDAO dao = new GameDAO()) {
                dao.addScore(Session.getUser().getId(), gameID, score);
                currentHighScore = score;
                Platform.runLater(() -> {
                    userHighScore.setText("High Score: " + score);
                });
                System.out.println("Updated new high score: " + score);

                // NEW: Call the callback to update total score
                if (scoreUpdateCallback != null) {
                    scoreUpdateCallback.run();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean isValidPosition(Tetromino piece, int x, int y) {
        int[][] shape = piece.getShape();
        for (int py = 0; py < shape.length; py++) {
            for (int px = 0; px < shape[py].length; px++) {
                if (shape[py][px] != 0) {
                    int newX = x + px;
                    int newY = y + py;

                    if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT) {
                        return false;
                    }

                    if (newY >= 0 && board[newY][newX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Tetromino getRandomTetromino() {
        Tetromino.Type[] types = Tetromino.Type.values();
        return new Tetromino(types[random.nextInt(types.length)]);
    }

    private void render() {
        Platform.runLater(() -> {
            renderGame();
            renderPreview();
        });
    }

    private void renderGame() {
        gameGc.setFill(Color.BLACK);
        gameGc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw board
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] != 0) {
                    drawCell(gameGc, x * CELL_SIZE, y * CELL_SIZE, getColor(board[y][x]));
                }
            }
        }

        // Draw current piece
        if (currentPiece != null && !isGameOver) {
            int[][] shape = currentPiece.getShape();
            Color pieceColor = getColor(currentPiece.getColor());

            for (int y = 0; y < shape.length; y++) {
                for (int x = 0; x < shape[y].length; x++) {
                    if (shape[y][x] != 0) {
                        int drawX = (currentX + x) * CELL_SIZE;
                        int drawY = (currentY + y) * CELL_SIZE;
                        drawCell(gameGc, drawX, drawY, pieceColor);
                    }
                }
            }
        }

        // Draw grid
        gameGc.setStroke(Color.DARKGRAY);
        gameGc.setLineWidth(0.5);
        for (int x = 0; x <= BOARD_WIDTH; x++) {
            gameGc.strokeLine(x * CELL_SIZE, 0, x * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        }
        for (int y = 0; y <= BOARD_HEIGHT; y++) {
            gameGc.strokeLine(0, y * CELL_SIZE, BOARD_WIDTH * CELL_SIZE, y * CELL_SIZE);
        }
    }

    private void renderPreview() {
        previewGc.setFill(Color.BLACK);
        previewGc.fillRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());

        if (nextPiece != null) {
            int[][] shape = nextPiece.getShape();
            Color pieceColor = getColor(nextPiece.getColor());

            // Center the piece in preview
            int offsetX = (PREVIEW_SIZE - shape[0].length) * CELL_SIZE / 2;
            int offsetY = (PREVIEW_SIZE - shape.length) * CELL_SIZE / 2;

            for (int y = 0; y < shape.length; y++) {
                for (int x = 0; x < shape[y].length; x++) {
                    if (shape[y][x] != 0) {
                        int drawX = offsetX + x * CELL_SIZE;
                        int drawY = offsetY + y * CELL_SIZE;
                        drawCell(previewGc, drawX, drawY, pieceColor);
                    }
                }
            }
        }
    }

    private void drawCell(GraphicsContext gc, double x, double y, Color color) {
        gc.setFill(color);
        gc.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

        // Add 3D effect
        gc.setFill(color.brighter());
        gc.fillRect(x + 1, y + 1, CELL_SIZE - 2, 2);
        gc.fillRect(x + 1, y + 1, 2, CELL_SIZE - 2);

        gc.setFill(color.darker());
        gc.fillRect(x + CELL_SIZE - 3, y + 1, 2, CELL_SIZE - 2);
        gc.fillRect(x + 1, y + CELL_SIZE - 3, CELL_SIZE - 2, 2);
    }

    private Color getColor(int colorIndex) {
        switch (colorIndex) {
            case 1: return Color.CYAN;    // I
            case 2: return Color.YELLOW;  // O
            case 3: return Color.PURPLE;  // T
            case 4: return Color.GREEN;   // S
            case 5: return Color.RED;     // Z
            case 6: return Color.BLUE;    // J
            case 7: return Color.ORANGE;  // L
            default: return Color.WHITE;
        }
    }

    private void updateUI() {
        Platform.runLater(() -> {
            if (scoreLabel != null) scoreLabel.setText("Score: " + score);
            if (linesLabel != null) linesLabel.setText("Lines: " + lines);
            if (levelLabel != null) levelLabel.setText("Level: " + level);
        });
    }

    // Tetromino class
    private static class Tetromino {
        enum Type {
            I, O, T, S, Z, J, L
        }

        private Type type;
        private int[][] shape;
        private int rotation;

        private static final int[][][] SHAPES = {
                // I
                {{1,1,1,1}},
                // O
                {{2,2},{2,2}},
                // T
                {{0,3,0},{3,3,3}},
                // S
                {{0,4,4},{4,4,0}},
                // Z
                {{5,5,0},{0,5,5}},
                // J
                {{6,0,0},{6,6,6}},
                // L
                {{0,0,7},{7,7,7}}
        };

        public Tetromino(Type type) {
            this.type = type;
            this.shape = copyArray(SHAPES[type.ordinal()]);
            this.rotation = 0;
        }

        public Tetromino rotate() {
            Tetromino rotated = new Tetromino(this.type);
            rotated.shape = rotateMatrix(this.shape);
            rotated.rotation = (this.rotation + 1) % 4;
            return rotated;
        }

        private int[][] rotateMatrix(int[][] matrix) {
            int rows = matrix.length;
            int cols = matrix[0].length;
            int[][] rotated = new int[cols][rows];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    rotated[j][rows - 1 - i] = matrix[i][j];
                }
            }
            return rotated;
        }

        private int[][] copyArray(int[][] original) {
            int[][] copy = new int[original.length][];
            for (int i = 0; i < original.length; i++) {
                copy[i] = original[i].clone();
            }
            return copy;
        }

        public int[][] getShape() {
            return shape;
        }

        public int getColor() {
            return type.ordinal() + 1;
        }
    }

    public void setScoreUpdateCallback(Runnable scoreUpdateCallback) {
        this.scoreUpdateCallback = scoreUpdateCallback;
    }

}