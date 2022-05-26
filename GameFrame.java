import javax.swing.*;

public class GameFrame {
    private final JFrame frame = new JFrame(); //creates the frame
    static final int FRAME_HEIGHT = 720; //should be changed in settings later
    static final int FRAME_WIDTH = 1280; //should be changed in settings later
    public GameFrame() {
        frame.setLayout(null);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("Tank Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        mainMenu();
    }

    /**
     * Sets JFrame to MainMenu panel
     */
    public void mainMenu() {
        MainMenu m = new MainMenu(frame); //creates MainMenu object
    }

    /**
     * Sets JFrame to host menu panel
     */
    public void startGame() {
        GamePanel panel = new GamePanel(); //Creates GamePanel object
        panel.setVisible(true);
        frame.setContentPane(panel);
    }
}


