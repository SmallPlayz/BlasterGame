import javax.swing.*;
import java.awt.*;

public class Projectile extends JLabel implements Runnable{
    private int xPosition;
    private final int yPosition = 500;
    Projectile(int xPosition) {
        this.xPosition = xPosition;

        setBounds(xPosition, yPosition, 10, 10);
        setOpaque(true);

        Thread thread = new Thread(this);
        thread.start();

        CollisionDetection collisionDetection = new CollisionDetection();
        Thread collisionThread = new Thread(collisionDetection);
        collisionThread.start();
    }

    @Override
    public void run() {
        BlasterGame.Threads++;
        while(getY() > -10) {
            try {
                setLocation(getX(), getY() - 5);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        BlasterGame.Threads--;
    }
    private class CollisionDetection extends Thread{
        public void run() {
            BlasterGame.Threads++;
            while (getY()>0) {
                for (Component component : BlasterGame.frame.getContentPane().getComponents()) {
                    if (component instanceof Asteroid) {
                        Asteroid asteroid = (Asteroid) component;

                        Rectangle spaceshipBounds = getBounds();
                        Rectangle asteroidBounds = asteroid.getBounds();

                        if (spaceshipBounds.intersects(asteroidBounds)) {
                            setLocation(getX(), -10);
                            asteroid.setDestroyed(true);
                            BlasterGame.frame.getContentPane().remove(asteroid);
                            BlasterGame.frame.getContentPane().revalidate();
                            BlasterGame.frame.getContentPane().repaint();
                            break;
                        }
                    }
                }
            }
            BlasterGame.Threads--;
        }
    }
}
