import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlasterGame extends Thread implements MenuPanel.StartButtonClickListener, MouseListener {
    public static JFrame frame;
    private MenuPanel menuPanel;
    private String difficulty;

    private String input;
    public static JLabel spaceship;
    public static int Threads = 0;
    private boolean gameStarted;
    private int hit;
    private JLabel Health;

    BlasterGame() {
        frame = new JFrame("Asteroid Blaster Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);

        frame.addMouseListener(this);

        frame.getContentPane().setBackground(Color.BLACK);

        ImageIcon icon = new ImageIcon("src/asteroidlogo.png");
        frame.setIconImage(icon.getImage());

        //health
        Health = new JLabel("Lives: 3");
        Font font = new Font("Serif", Font.BOLD, 18);
        Health.setBounds(0,0,100,50);
        Health.setForeground(Color.WHITE);
        Health.setFont(font);
        frame.add(Health);
        Health.setVisible(false);

        ImageIcon x = new ImageIcon("src/spaceshiptransparent.gif");
        spaceship = new JLabel(x);
        spaceship.setBounds(155,475, 75, 75);
        spaceship.setVisible(false);
        frame.add(spaceship);

        menuPanel = new MenuPanel(this);
        frame.add(menuPanel);

        CollisionDetection collisionDetection = new CollisionDetection();
        Thread collisionThread = new Thread(collisionDetection);
        collisionThread.start();

        ProjectileRunner projectileRunner = new ProjectileRunner();
        Thread projectileRunnerThread = new Thread(projectileRunner);
        projectileRunnerThread.start();

        frame.setVisible(true);
    }
    @Override
    public void onStartButtonClicked(String difficulty, String input) {
        this.difficulty = difficulty;
        this.input = input;
        System.out.println("Difficulty set: " + difficulty + " - Input set: " + input);

        gameStarted = true;

        Health.setVisible(true);

        if(input.equals("Keyboard")) {
            frame.requestFocus();
            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if(gameStarted) {
                        int keyCode = e.getKeyCode();

                        if (spaceship.getX() > 0 && (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT))
                            spaceship.setLocation(spaceship.getX() - 10, spaceship.getLocation().y);
                        if (spaceship.getX() < 300 && keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)
                            spaceship.setLocation(spaceship.getX() + 10, spaceship.getLocation().y);
                    }
                }
            });
        } else if (input.equals("Mouse")) {
            frame.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if(gameStarted) {
                        int mouseX = e.getX();
                        int spaceshipX = spaceship.getX();
                        int spaceshipWidth = spaceship.getWidth();
                        int frameWidth = frame.getWidth();

                        if (mouseX < spaceshipX)
                            spaceship.setLocation(Math.max(mouseX, 0), spaceship.getLocation().y);
                        else if (mouseX > spaceshipX + spaceshipWidth)
                            spaceship.setLocation(Math.min(mouseX - spaceshipWidth, frameWidth - spaceshipWidth), spaceship.getLocation().y);
                    }
                }
            });
        }
        else {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            HeadDetector app = new HeadDetector();
            app.start();
        }
        menuPanel.setVisible(false);
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        Threads++;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 60000; // 1 minute in milliseconds
        int delay = 3000;
        if (difficulty.equals("Medium"))
            delay = 1500;
        else if (difficulty.equals("Hard"))
            delay = 750;

        spaceship.setVisible(true);

        ArrayList<Asteroid> activeAsteroids = new ArrayList<>();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() < endTime) {
                if(gameStarted){
                    try {
                        Asteroid asteroid = new Asteroid((int) (Math.random() * 300));
                        frame.add(asteroid);
                        activeAsteroids.add(asteroid);
                        frame.revalidate();
                        frame.repaint();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                executorService.shutdown();
            }
        }, 0, delay, TimeUnit.MILLISECONDS);

        while (System.currentTimeMillis() < endTime) {
            ArrayList<Asteroid> asteroidsToRemove = new ArrayList<>();

            Iterator<Asteroid> iterator = activeAsteroids.iterator();

            ArrayList<Asteroid> activeAsteroids2 = new ArrayList<>();
            for (Component component : BlasterGame.frame.getContentPane().getComponents()) {
                if (component instanceof Asteroid) {
                    Asteroid asteroid1 = (Asteroid) component;
                    activeAsteroids2.add(asteroid1);
                }
            }

            for(Asteroid asteroid: activeAsteroids2) {
                asteroid.setLocation(asteroid.getX(), asteroid.getY() + 1);
                if (asteroid.getY() >= 650) {
                    frame.getContentPane().remove(asteroid);
                }
            }
            frame.revalidate();
            frame.repaint();

            try {
                Thread.sleep(16); // framerate
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        gameEnd();

    }

    private void gameEnd() {

        gameStarted = false;
        for (Component component : BlasterGame.frame.getContentPane().getComponents())
            if (component instanceof Asteroid)
                frame.getContentPane().remove(component);

        // Render the frame, repaint, etc.
        frame.revalidate();
        frame.repaint();

        spaceshipEndScreen();
    }

    private void gameEndLoss() {
        gameStarted = false;
        for (Component component : BlasterGame.frame.getContentPane().getComponents())
            if (component instanceof Asteroid)
                frame.getContentPane().remove(component);

        JLabel YouLostText = new JLabel("You Lost!");
        Font font1 = new Font("Serif", Font.BOLD, 36);
        YouLostText.setBounds(25, 275, 300, 100);
        YouLostText.setForeground(Color.WHITE);
        YouLostText.setFont(font1);
        frame.add(YouLostText);

        // Render the frame, repaint, etc.
        frame.revalidate();
        frame.repaint();
    }

    private void spaceshipEndScreen() {
        double acceleration = 1;
        int y = spaceship.getY();

        for (double i = y; i >= -100; i -= acceleration) {
            try {
                spaceship.setLocation(spaceship.getX(), (int) i);
                Thread.sleep(15);
                acceleration *= 1.1;
                System.out.println(i + "\t" + acceleration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    @Override
    public void mouseClicked(MouseEvent e) {
        if(gameStarted) {
            Projectile projectile = new Projectile(spaceship.getX() + 33);
            frame.add(projectile);
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {

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

    private class CollisionDetection extends Thread {
        public void run() {
            Threads++;
            hit = 0;
            while (hit < 3) {
                for (Component component : frame.getContentPane().getComponents()) {
                    if (component instanceof Asteroid) {
                        Asteroid asteroid = (Asteroid) component;

                        Rectangle spaceshipBounds = spaceship.getBounds();
                        Rectangle asteroidBounds = asteroid.getBounds();

                        if (spaceshipBounds.intersects(asteroidBounds)) {
                            hit++;
                            Health.setText("Lives: " + (3 - hit));
                            asteroid.setVisible(true);
                            frame.getContentPane().remove(asteroid);
                            frame.getContentPane().revalidate();
                            frame.getContentPane().repaint();
                            break;
                        }
                    }
                }
            }
            gameEndLoss();
            Threads--;
        }
    }
    private class ProjectileRunner extends Thread {
        public void run() {
            System.out.println("Thread Started!");
            while (true) {
                ArrayList<Projectile> activeProjectiles2 = new ArrayList<>();
                for (Component component : BlasterGame.frame.getContentPane().getComponents()) {
                    if (component instanceof Projectile) {
                        Projectile projectile1 = (Projectile) component;
                        activeProjectiles2.add(projectile1);
                    }
                }

                for (Projectile projectile : activeProjectiles2) {
                    projectile.setLocation(projectile.getX(), projectile.getY() - 5);
                }

                // Render the frame, repaint, etc.
                frame.revalidate();
                frame.repaint();

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}