import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class BlasterGame extends Thread implements MenuPanel.StartButtonClickListener{
    private JFrame frame;
    private MenuPanel menuPanel;
    private String difficulty = "easy";
    private JLabel spaceship;

    BlasterGame() {
        frame = new JFrame("Asteroid Blaster Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);

        frame.add(new Projectile(300));

        frame.getContentPane().setBackground(Color.BLACK);

        ImageIcon icon = new ImageIcon("src/asteroidlogo.png");
        frame.setIconImage(icon.getImage());

        ImageIcon x = new ImageIcon("src/spaceshiptransparent.gif");
        spaceship = new JLabel(x);
        spaceship.setBounds(155,475, 75, 75);
        spaceship.setVisible(false);
        frame.add(spaceship);

        menuPanel = new MenuPanel(this);
        frame.add(menuPanel);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                System.out.println(spaceship.getX());
                if(spaceship.getX() > 0 && (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT))
                        spaceship.setLocation(spaceship.getX() - 10, spaceship.getLocation().y);
                if(spaceship.getX() < 300 && keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)
                    spaceship.setLocation(spaceship.getX() + 10, spaceship.getLocation().y);
            }
        });

        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int spaceshipX = spaceship.getX();
                int spaceshipWidth = spaceship.getWidth();
                int frameWidth = frame.getWidth();

                if (mouseX < spaceshipX)
                    spaceship.setLocation(Math.max(mouseX, 0), spaceship.getLocation().y);
                else if (mouseX > spaceshipX + spaceshipWidth)
                    spaceship.setLocation(Math.min(mouseX - spaceshipWidth, frameWidth - spaceshipWidth), spaceship.getLocation().y);
            }
        });

        CollisionDetection collisionDetection = new CollisionDetection();
        Thread collisionThread = new Thread(collisionDetection);
        collisionThread.start();

        frame.setVisible(true);
    }
    @Override
    public void onStartButtonClicked(String difficulty) {
        System.out.println("Start button clicked with difficulty: " + difficulty);
        this.difficulty = difficulty;
        menuPanel.setVisible(false);
        Thread thread = new Thread(this);
        thread.start();
    }
    public void run() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 60000; // 1 minute in milliseconds
        int delay = 3000;
        if(difficulty.equals("Medium"))
            delay = 1500;
        else if (difficulty.equals("Hard"))
            delay = 750;

        spaceship.setVisible(true);

        while(System.currentTimeMillis() < endTime){
            try {
                frame.add(new Asteroid((int)(Math.random()*300)));
                Thread.sleep(delay);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class CollisionDetection extends Thread{
        public void run() {
            int hit = 0;
            while (hit < 3) {
                for (Component component : frame.getContentPane().getComponents()) {
                    if (component instanceof Asteroid) {
                        Asteroid asteroid = (Asteroid) component;

                        Rectangle spaceshipBounds = spaceship.getBounds();
                        Rectangle asteroidBounds = asteroid.getBounds();

                        if (spaceshipBounds.intersects(asteroidBounds)) {
                            hit++;
                            asteroid.setVisible(true);
                            frame.getContentPane().remove(asteroid);
                            frame.getContentPane().revalidate();
                            frame.getContentPane().repaint();
                            break;
                        }
                    }
                }
            }
        }
    }
}