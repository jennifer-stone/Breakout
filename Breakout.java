import objectdraw.*;
import java.util.ArrayList;
import java.awt.*;
import java.util.Random;

public class Breakout extends ActiveObject {
    private static final double BALL_SIZE = BreakoutProgram.BALL_RADIUS;
    private static final int DELAY_TIME = BreakoutProgram.DELAY;

    private static final double INIT_Y_SPEED = BreakoutProgram.VELOCITY_Y;
    private static final double INIT_X_SPEED_MAX = BreakoutProgram.VELOCITY_X_MAX;
    private static final double INIT_X_SPEED_MIN = BreakoutProgram.VELOCITY_X_MIN;

    public static final double PADDLE_WIDTH = BreakoutProgram.PADDLE_WIDTH;
    public static final double PADDLE_HEIGHT = BreakoutProgram.PADDLE_HEIGHT;
    public static final double PADDLE_Y_OFFSET = BreakoutProgram.PADDLE_Y_OFFSET;

    private static final int NBRICK_COLUMNS = BreakoutProgram.NBRICK_COLUMNS;
    private static final int NBRICK_ROWS = BreakoutProgram.NBRICK_ROWS;
    private static final int TOTAL_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;

    private static final double BRICK_WIDTH = BreakoutProgram.BRICK_WIDTH;
    private static final double BRICK_HEIGHT = BreakoutProgram.BRICK_HEIGHT;

    private static final double BRICK_SEP = BreakoutProgram.BRICK_SEP;
    private static final double BRICK_Y_OFFSET = BreakoutProgram.BRICK_Y_OFFSET;

    public static final double FRAME_T = 7;

    private static final String FONT = BreakoutProgram.SCREEN_FONT;

    private FilledOval theBall;
    private final DrawingCanvas canvas;

    FilledRect leftSide;
    FilledRect rightSide;
    FilledRect top;
    FilledRect bottom;
    FilledRect paddle;

    ArrayList <FilledRect> brickArray;

    double xVel;
    double yVel;

    Text playerScoreText;
    Text endScreenText;

    int playerScore;
    int numberOfLives;

    boolean gameOver = false;
    boolean win = false;

    public Breakout(Location initialLocation, DrawingCanvas canvas) {

        this.canvas = canvas;

        // Oval Ball
        makeBall(initialLocation);

        // Frame
        leftSide = new FilledRect(0, 0,
                FRAME_T, canvas.getHeight(), Color.WHITE, canvas);

        rightSide = new FilledRect(canvas.getWidth() - FRAME_T,
                0, FRAME_T, canvas.getHeight(), Color.WHITE, canvas);

        top = new FilledRect(0, 0, canvas.getWidth(), FRAME_T, Color.WHITE, canvas);

        bottom = new FilledRect(0, canvas.getHeight() - FRAME_T, canvas.getWidth(), FRAME_T, Color.WHITE, canvas);

        //Paddle
        paddle = new FilledRect((canvas.getWidth() - (PADDLE_WIDTH))/2 ,canvas.getHeight()-PADDLE_Y_OFFSET,
                PADDLE_WIDTH, PADDLE_HEIGHT, Color.WHITE, canvas);

        playerScoreText = new Text(true, 25, 25, Color.WHITE, canvas);
        playerScoreText.setFont(FONT);

        //Score
        playerScore = 0;
        numberOfLives = BreakoutProgram.NTURNS;

        //Bricks Color
        brickArray = new ArrayList<>();
        Color color;

        for (int j = 0; j < NBRICK_COLUMNS; j++) {
            if (j % 10 < 2) {
                color = Color.RED;
            } else if (j % 10 < 4) {
                color = Color.ORANGE;
            } else if (j % 10 < 6) {
                color = Color.YELLOW;
            } else if (j % 10 < 8) {
                color = Color.GREEN;
            } else {
                color = Color.CYAN;
            }

            for (int i = 0; i < NBRICK_ROWS; i++) {
                brickArray.add(new FilledRect(FRAME_T + ((i) * (BRICK_WIDTH +
                        BRICK_SEP)), (BRICK_Y_OFFSET) + (j) * (BRICK_HEIGHT +
                        BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT, color, canvas));
            }
        }

        //Background Color
        canvas.setBackground(Color.BLACK);
        start();
    }

     public void run(){
        while(!gameOver && !win) {
            updateBall();
            updateBricks();
            playerScoreText.setText("Score: " + playerScore + ", Turns: " + numberOfLives);
            pause(DELAY_TIME);
        }

        canvas.repaint();
    }

    private void makeBall(Location initialLocation){
        theBall = new FilledOval(initialLocation,
                BALL_SIZE,
                BALL_SIZE, Color.WHITE,
                canvas);
        Random random = new Random();

        xVel = random.nextDouble() * (INIT_X_SPEED_MAX-INIT_X_SPEED_MIN)+ INIT_X_SPEED_MIN;
        if(random.nextDouble() < .5 ){
            xVel *= -1;
        }
        yVel = INIT_Y_SPEED;
    }

    private void updateBall(){
        theBall.move(xVel, yVel);
        if (theBall.overlaps(leftSide)) {
            xVel = Math.abs(xVel);
        }

        if (theBall.overlaps(rightSide)) {
            xVel = -Math.abs(xVel);
        }

        if (theBall.overlaps(top)) {
            yVel = Math.abs(yVel);
        }

        if (theBall.overlaps(paddle)) {
            yVel = -Math.abs(yVel);
        }

        if (theBall.getY() > canvas.getHeight()) {

            theBall.moveTo(canvas.getWidth()/2.0,canvas.getHeight()/2.0);

            numberOfLives--;

            if (numberOfLives <= 0) {
                lose();
            }
        }
    }

    private void updateBricks() {
        if (playerScore == TOTAL_BRICKS) {
            win();
        }

        for (int i = 0; i < brickArray.size() ; i++) {
            if (theBall.overlaps(brickArray.get(i))) {
                brickArray.get(i).removeFromCanvas();
                brickArray.remove(i);
                playerScore ++;

                if (yVel > 0){
                    yVel = -Math.abs(yVel);
                }

                else if (yVel < 0){
                    yVel = Math.abs(yVel);
                }
            }
        }
    }

    private void win() {
        endScreenText = new Text(true,0,0,Color.WHITE, canvas);
        endScreenText.setFont(FONT);
        endScreenText.setText("YOU WIN!!");
        endScreenText.moveTo(calculateTextCenter(endScreenText.getWidth(),endScreenText.getHeight()));
        theBall.removeFromCanvas();
        paddle.removeFromCanvas();
        playerScoreText.removeFromCanvas();
        win = true;
    }

    private void lose() {
        endScreenText = new Text(true,0,0,Color.WHITE,canvas);
        endScreenText.setFont(FONT);
        endScreenText.setText("YOU LOSE!!");
        endScreenText.moveTo(calculateTextCenter(endScreenText.getWidth(),endScreenText.getHeight()));

        for (FilledRect filledRect : brickArray) {
            filledRect.removeFromCanvas();
        }

        theBall.removeFromCanvas();
        paddle.removeFromCanvas();
        gameOver = true;
        playerScoreText.removeFromCanvas();
    }

    private Location calculateTextCenter(double textWidth, double textHeight) {
         return new Location(canvas.getWidth()/2.0 - textWidth/2.0,
                 canvas.getHeight()/2.0-textHeight/2);
    }
}