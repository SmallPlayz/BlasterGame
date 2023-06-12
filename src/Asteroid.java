import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Asteroid extends JLabel implements Runnable{

    private final int width = 50;
    private final int height = 119;
    private boolean isDestroyed = false;

    Asteroid(int x) throws IOException {
        setBounds(x ,0, width, height);

        try {
            URL asteroidUrl = new URL("https://i.ibb.co/swtv5Gp/Asteroid.png");
            BufferedImage asteroidImage = ImageIO.read(asteroidUrl);
            BufferedImage a = resize(asteroidImage, width, height);
            ImageIcon asteroidImageIcon = new ImageIcon(a);
            setIcon(asteroidImageIcon);
        } catch (IOException e) {
            setOpaque(true);
        }

        //Thread thread = new Thread(this);
        //thread.start();
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    @Override
    public void run() {
        BlasterGame.Threads++;
        while (getY() < 650 && !isDestroyed) {
            try {
                setLocation(getX(), getY()+1);
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        BlasterGame.Threads--;
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }
}