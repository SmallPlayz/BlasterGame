import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BlasterGame {

    BlasterGame() throws IOException {
        JFrame frame = new JFrame("Asteroid Blaster Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);

        frame.getContentPane().setBackground(Color.BLACK);

        frame.add(new Asteroid());
        ImageIcon x = new ImageIcon("src/ee.gif");
        JLabel spaceship = new JLabel(x);
        spaceship.setBounds(50,300, 75, 75);
        spaceship.setOpaque(true);
        frame.add(spaceship);

        frame.setVisible(true);
    }
}