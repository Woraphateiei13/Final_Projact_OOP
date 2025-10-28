package game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Display extends JFrame {
    public Display() {
        super("Wi-Fi Wanderrer");
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(300, 200);
        this.getContentPane().setLayout(null);

        Game g = new Game();
        this.add(g);

        this.setVisible(true);
        // this.requestFocusInWindow();
        SwingUtilities.invokeLater(() -> g.requestFocusInWindow());
    }

    public static void main(String[] args) {
        Display display = new Display();
    }
}
