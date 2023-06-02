import javax.swing.*;

public class Projectile extends JLabel implements Runnable{
    private int xPosition;
    private final int yPosition = 300;
    Projectile(int xPosition) {
        this.xPosition = xPosition;

        setBounds(xPosition, yPosition, 10, 10);
        setOpaque(true);

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

    }
}
