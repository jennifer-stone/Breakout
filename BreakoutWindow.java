import objectdraw.FilledRect;
import objectdraw.Location;
import objectdraw.WindowController;

    public class BreakoutWindow extends WindowController {
        private static final double CANVAS_HEIGHT = BreakoutProgram.CANVAS_HEIGHT;
        private static final double CANVAS_WIDTH = BreakoutProgram.CANVAS_WIDTH;
        FilledRect paddle;

        final double paddleWidth = BreakoutProgram.PADDLE_WIDTH;
        final double frameT = Breakout.FRAME_T;

        public void begin() {
            Breakout game = new Breakout(new Location(canvas.getWidth() / 2.0, canvas.getHeight() / 2.0), canvas);
            paddle = game.paddle;
        }

        public void onMouseMove(Location point) {
            if (point.getX() >= (frameT)) {
                paddle.moveTo(Math.min(point.getX(), (canvas.getWidth()-(frameT + paddleWidth))), paddle.getY());
            }
        }

        public static void main(String[] args) {
            new BreakoutWindow().startController((int)CANVAS_WIDTH, (int)CANVAS_HEIGHT);
        }
    }

