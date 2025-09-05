package com.viduwa.minigames.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static com.viduwa.minigames.Main.showMainMenu;

public class SnakeGame extends BorderPane {

    public static int gameID = 1;

    // Game variables
    private int speed = 5;
    private int foodcolor = 0;
    private int width = 30;
    private int height = 20;
    private int foodX = 0;
    private int foodY = 0;
    private int cornersize = 30;
    private List<Corner> snake = new ArrayList<>();
    private Dir direction = Dir.left;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private Random rand = new Random();
    private AnimationTimer gameTimer;
    private Canvas canvas;
    private GraphicsContext gc;
    private Consumer<Integer> scoreListener;
    private int score = 0;
    private int currentHighScore = 0;

    // UI components
    private Label scoreLabel;
    private Label userHighScore;
    private Label gameOverLabel;
    private Button restartButton;
    private Button pauseButton;
    private Button backToMenuButton;

    private Runnable scoreUpdateCallback;


    public enum Dir {
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public SnakeGame() {
        setupUI();
        initializeGame();

    }

    private void setupUI() {
        setStyle("-fx-background-color: #1a1a2e;");

        //show highscore
        try (GameDAO dao = new GameDAO()) {
            currentHighScore = dao.getUserHighScoreForGame(Session.getUser().getId(), gameID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize canvas
        canvas = new Canvas(width * cornersize, height * cornersize);
        gc = canvas.getGraphicsContext2D();

        // UI Labels
        scoreLabel = new Label("Score: 0");
        gameOverLabel = new Label("GAME OVER");
        userHighScore = new Label("High Score: " + currentHighScore);
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
        gameArea.getChildren().add(canvas);

        setCenter(gameArea);
        setRight(rightPanel);

        // Instructions
        Label instructionsLabel = new Label("Controls: WASD or Arrow Keys to move, Space to pause");
        instructionsLabel.setFont(Font.font("Arial", 12));
        instructionsLabel.setTextFill(Color.LIGHTGRAY);
        instructionsLabel.setStyle("-fx-padding: 10;");
        setBottom(instructionsLabel);

        // Make focusable for key events
        setFocusTraversable(true);
        setOnKeyPressed(this::handleKeyPress);
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
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 150"
            );

            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: linear-gradient(#1b1b2f, #16213e);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-color: #ff6f91;" +
                            "-fx-border-width: 2;" +
                            "-fx-padding: 8 16;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 150"
            ));

            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: linear-gradient(#16213e, #0f3460);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-color: #e94560;" +
                            "-fx-border-width: 2;" +
                            "-fx-padding: 8 16;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 150"
            ));
        }
    }

    private void styleLabels() {
        Font labelFont = Font.font("Upheaval TT -BRK-", 30);
        Color textColor = Color.WHITE;

        scoreLabel.setFont(labelFont);
        scoreLabel.setTextFill(textColor);

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

        rightPanel.getChildren().addAll(
                userHighScore,
                scoreLabel,
                new Region(), // Spacer
                pauseButton,
                restartButton,
                backToMenuButton,
                new Region(), // Spacer
                gameOverLabel
        );

        return rightPanel;
    }

    private void setupButtonHandlers() {
        restartButton.setOnAction(e -> restartGame());
        pauseButton.setOnAction(e -> togglePause());
        backToMenuButton.setOnAction(e -> {
            System.out.println("Back to Menu");
            showMainMenu(Session.getUser().getUsername());
        });
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Resume" : "Pause");
    }

    private void initializeGame() {
        // Initialize game state
        newFood();

        // Add start snake parts
        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));

        // Reset game variables
        direction = Dir.left;
        gameOver = false;
        isPaused = false;
        speed = 5;
        score = 0;

        updateUI();

        // Start game loop
        startGameLoop();
    }

    private void handleKeyPress(KeyEvent key) {
        if (gameOver) {
            if (key.getCode() == KeyCode.SPACE || key.getCode() == KeyCode.ENTER) {
                restartGame();
            }
            return;
        }

        if (key.getCode() == KeyCode.SPACE) {
            togglePause();
            return;
        }

        if (isPaused) return;

        // Prevent opposite direction moves
        switch (key.getCode()) {
            case W:
            case UP:
                if (direction != Dir.down) {
                    direction = Dir.up;
                }
                break;
            case A:
            case LEFT:
                if (direction != Dir.right) {
                    direction = Dir.left;
                }
                break;
            case S:
            case DOWN:
                if (direction != Dir.up) {
                    direction = Dir.down;
                }
                break;
            case D:
            case RIGHT:
                if (direction != Dir.left) {
                    direction = Dir.right;
                }
                break;
        }
        key.consume();
    }

    private void startGameLoop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new AnimationTimer() {
            long lastTick = 0;

            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    if (!isPaused && !gameOver) {
                        tick();
                    }
                    render();
                    return;
                }

                if (now - lastTick > 1000000000 / speed) {
                    lastTick = now;
                    if (!isPaused && !gameOver) {
                        tick();
                    }
                    render();
                }
            }
        };
        gameTimer.start();
    }

    private void drawCenteredText(GraphicsContext gc, String text, double centerX, double centerY) {
        javafx.scene.text.Text helper = new javafx.scene.text.Text(text);
        helper.setFont(gc.getFont());
        double textWidth = helper.getLayoutBounds().getWidth();
        double textHeight = helper.getLayoutBounds().getHeight();

        // Draw so that the text is centered on (centerX, centerY)
        gc.fillText(text, centerX - textWidth / 2, centerY + textHeight / 4);
    }

    private void checkAndUpdateHighScore() {
        if (score > currentHighScore) {
            System.out.println("Updating new high score: " + score);
            try (GameDAO dao = new GameDAO()) {
                dao.addScore(Session.getUser().getId(), 1, score);
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

    private void tick() {
        if (gameOver) {
            Platform.runLater(() -> {
                gameOverLabel.setVisible(true);
                pauseButton.setText("Pause");
            });

            return;
        }

        // Move snake body
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        // Move snake head
        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y >= height) {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x >= width) {
                    gameOver = true;
                }
                break;
        }

        // Check food collision
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            newFood();
            score = score + 20;
            updateUI();

            // Check and update high score
            checkAndUpdateHighScore();

            if (scoreListener != null) {
                scoreListener.accept(score);
            }
        }

        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }
    }

    private void render() {
        Platform.runLater(() -> {
            // Clear background
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width * cornersize, height * cornersize);

            if (isPaused && !gameOver) {
                // Draw pause overlay
                gc.setFill(Color.color(0, 0, 0, 0.7));
                gc.fillRect(0, 0, width * cornersize, height * cornersize);

                double centerX = width * cornersize / 2;
                double centerY = height * cornersize / 2;

                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Upheaval TT -BRK-", 40));
                drawCenteredText(gc, "PAUSED", centerX, centerY);

                gc.setFont(new Font("Upheaval TT -BRK-", 16));
                drawCenteredText(gc, "Press SPACE to continue", centerX, centerY + 40);
                return;
            }

            if (gameOver) {
                // Draw game over overlay
                gc.setFill(Color.color(0, 0, 0, 0.7));
                gc.fillRect(0, 0, width * cornersize, height * cornersize);

                double centerX = width * cornersize / 2;
                double centerY = height * cornersize / 2;

                gc.setFill(Color.RED);
                gc.setFont(new Font("Upheaval TT -BRK-", 60));
                drawCenteredText(gc, "GAME OVER", centerX, centerY);

                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Upheaval TT -BRK-", 16));
                drawCenteredText(gc, "Press SPACE to restart", centerX, centerY + 40);
                return;
            }

            // Draw food with random color
            Color foodColor = Color.WHITE;
            switch (foodcolor) {
                case 0:
                    foodColor = Color.PURPLE;
                    break;
                case 1:
                    foodColor = Color.LIGHTBLUE;
                    break;
                case 2:
                    foodColor = Color.YELLOW;
                    break;
                case 3:
                    foodColor = Color.PINK;
                    break;
                case 4:
                    foodColor = Color.ORANGE;
                    break;
            }
            gc.setFill(foodColor);
            gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

            // Draw snake
            for (Corner c : snake) {
                // Outer green rectangle
                gc.setFill(Color.LIGHTGREEN);
                gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
                // Inner darker green rectangle
                gc.setFill(Color.GREEN);
                gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
            }

            // Draw grid (optional, similar to Tetris)
            gc.setStroke(Color.color(0.2, 0.2, 0.2));
            gc.setLineWidth(0.5);
            for (int x = 0; x <= width; x++) {
                gc.strokeLine(x * cornersize, 0, x * cornersize, height * cornersize);
            }
            for (int y = 0; y <= height; y++) {
                gc.strokeLine(0, y * cornersize, width * cornersize, y * cornersize);
            }
        });
    }

    private void updateUI() {
        Platform.runLater(() -> {
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }

            if(score > currentHighScore){
                userHighScore.setText("High Score: " + score);
            }
        });
    }

    private void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            // Make sure food doesn't spawn on snake
            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            foodcolor = rand.nextInt(5);
            break;
        }
    }

    private void restartGame() {
        // Stop current game timer
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // Reset all game variables
        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));

        direction = Dir.left;
        gameOver = false;
        isPaused = false;
        speed = 5;
        score = 0;

        // Update UI
        gameOverLabel.setVisible(false);
        pauseButton.setText("Pause");
        updateUI();

        newFood();

        // Update score display
        if (scoreListener != null) {
            scoreListener.accept(score);
        }

        // Restart game loop
        startGameLoop();
    }

    public void setScoreListener(Consumer<Integer> scoreListener) {
        this.scoreListener = scoreListener;
    }

    // Clean up when the game is removed
    public void cleanup() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    // Override focus behavior to ensure the game can receive key events
    @Override
    public void requestFocus() {
        super.requestFocus();
        if (canvas != null) {
            canvas.requestFocus();
        }
    }

    public void setScoreUpdateCallback(Runnable scoreUpdateCallback) {
        this.scoreUpdateCallback = scoreUpdateCallback;
    }


}