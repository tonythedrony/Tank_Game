import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("ALL")
public class GamePanel extends JPanel implements ActionListener, MouseListener {
    static final int FRAME_HEIGHT = 720;
    static final int FRAME_WIDTH = 1280;
    private int focus = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private final String RIGHT = "right";
    private final String LEFT = "left";
    private final String UP = "up";
    private final String DOWN = "down";
    private final String RIGHTP = "rightp";
    private final String LEFTP = "leftp";
    private final String UPP = "upp";
    private final String DOWNP = "downp";
    private final String RIGHTR = "rightr";
    private final String LEFTR = "leftr";
    private final String UPR = "upr";
    private final String DOWNR = "downr";
    private final int MOVEMENT_SPEED = 5;
    private boolean moveUp, moveDown, rotateRight, rotateLeft;
    private Point mouseLoc;
    private int mouseLocX, mouseLocY;
    private double mouseDist, mouseDistX, mouseDistY;
    private double mouseDegree;
    private Timer timer;
    private Tank tank;
    private Point2D fromPoint, toPoint;
    private BufferedImage blueTankBase, blueTankTurret;
    private moveAction leftpress, rightpress, uppress, downpress, leftrelease, rightrelease, uprelease, downrelease;
    private ArrayList<Bullet> bullets = new ArrayList<>(8);

    /**
     * Instantiates the gamePanel
     */
    public GamePanel() {
        tank = new Tank(500,300, 4);
        this.addMouseListener(this);
        toPoint = new Point2D.Double(0,0);
        fromPoint = new Point2D.Double(0,0);

        // https://stackoverflow.com/questions/1978445/get-image-from-relative-path
        try {
            blueTankBase = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/bluetankbase.png")));
            blueTankTurret = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/bluetankturret.png")));
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

        // https://stackoverflow.com/questions/46985936/using-keybinding-and-action-map-in-java-for-shortcut-keys-for-buttons
        leftpress = new moveAction(LEFT, 1);
        rightpress = new moveAction(RIGHT, 1);
        uppress = new moveAction(UP, 1);
        downpress = new moveAction(DOWN, 1);

        leftrelease = new moveAction(LEFT, 0);
        rightrelease = new moveAction(RIGHT, 0);
        uprelease = new moveAction(UP, 0);
        downrelease = new moveAction(DOWN, 0);

        this.getInputMap(focus).put(KeyStroke.getKeyStroke("pressed A"), LEFTP);
        this.getActionMap().put(LEFTP, leftpress);
        this.getInputMap(focus).put(KeyStroke.getKeyStroke("pressed D"), RIGHTP);
        this.getActionMap().put(RIGHTP, rightpress);
        this.getInputMap(focus).put(KeyStroke.getKeyStroke("pressed W"), UPP);
        this.getActionMap().put(UPP, uppress);
        this.getInputMap(focus).put(KeyStroke.getKeyStroke("pressed S"), DOWNP);
        this.getActionMap().put(DOWNP, downpress);

        this.getInputMap(focus).put(KeyStroke.getKeyStroke("released A"), LEFTR);
        this.getActionMap().put(LEFTR, leftrelease);
        this.getInputMap(focus).put(KeyStroke.getKeyStroke("released D"), RIGHTR);
        this.getActionMap().put(RIGHTR, rightrelease);
        this.getInputMap(focus).put(KeyStroke.getKeyStroke("released W"), UPR);
        this.getActionMap().put(UPR, uprelease);
        this.getInputMap(focus).put(KeyStroke.getKeyStroke("released S"), DOWNR);
        this.getActionMap().put(DOWNR, downrelease);

        startGame();
    }

    /**
     * @param e the event to be processed
     */
    // https://stackoverflow.com/questions/2668718/java-mouselistener
    @Override
    public void mousePressed(MouseEvent e) {
        fromPoint = getBusinessEndOfBarrel();
        double outOfViewRadius = Math.max(tank.getTankWidth(), tank.getTankHeight()) * 2d;
        toPoint = getPointOnCircle(tank.getTurretAngle() + tank.getBaseAngle(), -90, outOfViewRadius, tank.getCenterBase().getX(), tank.getCenterBase().getY());

        Bullet bullet = new Bullet(fromPoint, toPoint, 750);
        bullets.add(bullet);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private class moveAction extends AbstractAction {
        String direction;
        int state;

        /**
         * Creates a moveAction object with a direction and state
         * @param direction - the direction of the action
         * @param state - the state of the action
         */
        moveAction(String direction, int state) {
            this.direction = direction;
            this.state = state;
        }

        /**
         * @param e the event to be processed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (direction.equals(RIGHT) && state == 1) {
                rotateRight = true;
            }
            if (direction.equals(LEFT) && state == 1) {
                rotateLeft = true;
            }
            if (direction.equals(UP) && state == 1) {
                moveUp = true;
            }
            if (direction.equals(DOWN) && state == 1) {
                moveDown = true;
            }

            if (direction.equals(RIGHT) && state == 0) {
                rotateRight = false;
            }
            if (direction.equals(LEFT) && state == 0) {
                rotateLeft = false;
            }
            if (direction.equals(UP) && state == 0) {
                moveUp = false;
            }
            if (direction.equals(DOWN) && state == 0) {
                moveDown = false;
            }
        }
    }

    /**
     * The update method being called by the timer that is constantly refreshing all actions happening on the screen
     */
    public void update() {
        tank.setCenterTurret(new Point2D.Double(tank.xPos() + 67, tank.yPos() + 125));
        tank.setEndTurret(new Point2D.Double(tank.xPos() + ((Math.sin(Math.toRadians(tank.getTurretAngle())))) + 63, tank.yPos() + ((Math.cos(Math.toRadians(tank.getTurretAngle())))) - 25));
        tank.setCenterBase(new Point2D.Double(tank.xPos() + (tank.getTankWidth() / 2), tank.yPos() + (tank.getTankHeight() / 2)));

        // Gets the mouse location relative to the JFrame
        // https://stackoverflow.com/questions/1439022/get-mouse-position
        mouseLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLoc, this);

        // Gets the X and Y position of the mouse
        mouseLocX = (int) mouseLoc.getX();
        mouseLocY = (int) mouseLoc.getY();

        // Gets the distance between the mouse and the tank
        mouseDistX = mouseLocX - tank.getCenterTurret().getX();
        mouseDistY = mouseLocY - tank.getCenterTurret().getY();

        // Gets the angle of the mouse relative to the center of the turret
        mouseDegree = angleInRelation(mouseLoc, tank.getCenterTurret());
        mouseDegree = mouseDegree - tank.getBaseAngle();
        tank.setTurretAngle(mouseDegree);

        // https://gamedev.stackexchange.com/questions/36046/how-do-i-make-an-entity-move-in-a-direction
        if (moveUp) {
            tank.setLocation(tank.xPos() + (MOVEMENT_SPEED * Math.sin(Math.toRadians(tank.getBaseAngle()))), tank.yPos() - MOVEMENT_SPEED * Math.cos(Math.toRadians(tank.getBaseAngle())));
        }
        if (moveDown) {
            tank.setLocation(tank.xPos() - (MOVEMENT_SPEED * Math.sin(Math.toRadians(tank.getBaseAngle()))), tank.yPos() + MOVEMENT_SPEED * Math.cos(Math.toRadians(tank.getBaseAngle())));
        }
        if (rotateLeft && tank.xPos() >= 0) {
            tank.setBaseAngle((tank.getBaseAngle() - 5) % 360);
        }
        if (rotateRight && tank.xPos() + tank.getTankWidth() <= FRAME_WIDTH) {
            tank.setBaseAngle((tank.getBaseAngle() + 5) % 360);
        }

        // https://stackoverflow.com/questions/72411743/how-to-move-paint-graphics-along-slope?answertab=scoredesc#tab-top
        ArrayList<Bullet> outOfScopeProjectiles = new ArrayList<>(8);
        Rectangle visibleBounds = new Rectangle(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        for (Bullet bullet : bullets) {
            bullet.tick();
            Point2D current = bullet.getLocation();
            if (!visibleBounds.contains(current)) {
                outOfScopeProjectiles.add(bullet);
            }
        }
        bullets.removeAll(outOfScopeProjectiles);
        repaint();
    }

    /**
     * Paints everything on the screen, being called by update()
     * @param g the <code>Graphics</code> object to protect
     */
    // https://stackoverflow.com/questions/29585353/paint-with-swing-java
    // https://stackoverflow.com/questions/8639567/java-rotating-images
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D master = (Graphics2D) g;
        Graphics2D g2D = (Graphics2D) master.create();

        // Clears the screen back to white
        g2D.setColor(Color.white);
        g2D.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);

        // Paints the base of the tank
        Graphics2D tankBase = (Graphics2D) g2D.create();
        tankBase.rotate(Math.toRadians(tank.getBaseAngle()), tank.getCenterBase().getX(), tank.getCenterBase().getY());
        tankBase.drawImage(blueTankBase, (int) tank.xPos(), (int) tank.yPos(), tank.getTankWidth(), tank.getTankHeight(), null);

        // Paints the turret of the tank
        Graphics2D tankTurret = (Graphics2D) tankBase.create();
        tankTurret.rotate(Math.toRadians(tank.getTurretAngle()), tank.xPos() + 67, tank.yPos() + 125);
        tankTurret.drawImage(blueTankTurret, (int) tank.xPos() + 33, (int) tank.yPos() - 10, tank.getTurretWidth(), tank.getTurretHeight(), null);

        // Paints the bullets
        for (Bullet bullet : bullets) {
            Point2D currentBulletLoc = bullet.getLocation();
            master.fill(new Ellipse2D.Double(currentBulletLoc.getX() - 2, currentBulletLoc.getY() - 2, 8, 8));
        }
        g2D.dispose();
    }

    /**
     * Timer calls this to keep calling update
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    /**
     * Finds the angle between point1 in relation to point2
     * @param point1 - the first point
     * @param point2 - the second point
     * @return - the angle between the two points
     */
    public double angleInRelation(Point point1, Point2D point2) {
        double angle = Math.toDegrees(Math.atan2((point1.getY() - point2.getY()), point1.getX() - point2.getX()));
        angle += 90;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Gets a point on a circle, given a degree, offset and radius, gets called by other getPointOnCircle
     * @param degrees - the degree number to find a point at
     * @param offset - the offset to the degree
     * @param radius - the radius of the circle
     * @return a point on the circle with the specified parameters
     */
    // https://stackoverflow.com/questions/72411743/how-to-move-paint-graphics-along-slope?answertab=scoredesc#tab-top
    public static Point2D getPointOnCircle(double degrees, double offset, double radius) {
        double rads = Math.toRadians(degrees + offset); // 0 becomes the top

        // Calculates the outer point of the line
        double xPosy = Math.cos(rads) * radius;
        double yPosy = Math.sin(rads) * radius;

        return new Point2D.Double(xPosy, yPosy);
    }

    /**
     Gets a point on a circle, given a degree, offset and radius, gets called by other getPointOnCircle
     * @param degrees - the degree number to find a point at
     * @param offset - the offset to the degree
     * @param radius - the radius of the circle
     * @param centerX - the X point of the circle
     * @param centerY - the Y point of the circle
     * @return the point on the circle with the specified parameters
     */
    // https://stackoverflow.com/questions/72411743/how-to-move-paint-graphics-along-slope?answertab=scoredesc#tab-top
    public static Point2D getPointOnCircle(double degress, double offset, double radius, double centerX, double centerY) {
        Point2D poc = getPointOnCircle(degress, offset, radius);
        return new Point2D.Double(poc.getX() + centerX, poc.getY() + centerY);
    }

    /**
     * Calculates the point at the end of turret
     * @return the point at the end of the turret
     */
    // https://stackoverflow.com/questions/72411743/how-to-move-paint-graphics-along-slope?answertab=scoredesc#tab-top
    public Point2D getBusinessEndOfBarrel() {
        // Calculates the point at the end of the turret
        double centerX = tank.getCenterBase().getX();
        double centerY = tank.getCenterBase().getY();

        return getPointOnCircle(tank.getTurretAngle() + tank.getBaseAngle(), -90, Math.max(tank.getTankWidth(), tank.getTankHeight()) / 2, centerX, centerY);
    }

    /**
     * Starts the timer that calls update(), starting the game
     */
    // https://stackoverflow.com/questions/22366890/java-timer-action-listener
    public void startGame() {
        timer = new Timer(1000 / 60, this);
        timer.start();
    }
}