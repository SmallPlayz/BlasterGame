import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Asteroid extends JLabel {

    private final int Width = 50;
    private final int Height = 119;

    Asteroid() throws IOException {
        setBounds(0 ,0, Width, Height);

        URL asteroidUrl = new URL("https://i.ibb.co/swtv5Gp/Asteroid.png");
        BufferedImage asteroidImage = ImageIO.read(asteroidUrl);
        BufferedImage a = resize(asteroidImage, Width, Height);
        ImageIcon asteroidImageIcon = new ImageIcon(a);

        setIcon(asteroidImageIcon);
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}