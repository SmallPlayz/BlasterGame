import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HeadDetector {

    private JFrame frame;
    private JLabel lblFrame;
    private VideoCapture videoCapture;
    private boolean isRunning;
    private CascadeClassifier faceCascade;
    private int prevX = -1;
    private int middleZoneWidth = 50;

    public HeadDetector() {
        frame = new JFrame("Camera Feed");
        lblFrame = new JLabel();
        frame.getContentPane().add(lblFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        faceCascade = new CascadeClassifier();
        faceCascade.load("C:\\Users\\900ra\\IdeaProjects\\BlasterGame\\src\\haarcascade_frontalface_default.xml"); // Load the face detection model
    }

    public void start() {
        videoCapture = new VideoCapture();
        videoCapture.open(0);

        if (videoCapture.isOpened()) {
            isRunning = true;

            int width = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
            int height = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
            frame.setPreferredSize(new Dimension(width, height));
            frame.pack();

            Thread captureThread = new Thread(() -> {
                Mat frameMat = new Mat();
                BufferedImage image;

                while (isRunning) {
                    videoCapture.read(frameMat);

                    Core.flip(frameMat, frameMat, 1);

                    // OpenCV frame -> bufferimage
                    image = matToBufferedImage(frameMat);

                    // Detect faces
                    MatOfRect faces = new MatOfRect();
                    faceCascade.detectMultiScale(frameMat, faces);

                    // Process faces
                    processFaces(faces, width);

                    // Draw boxes
                    Graphics2D g2d = image.createGraphics();
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(2));
                    for (Rect rect : faces.toArray()) {
                        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
                    }
                    g2d.dispose();

                    lblFrame.setIcon(new ImageIcon(image));
                    lblFrame.repaint();
                }

                videoCapture.release();
                frame.dispose();
            });
            captureThread.start();
        } else {
            System.out.println("Failed to open the camera.");
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void processFaces(MatOfRect faces, int frameWidth) {
        if (faces.toArray().length > 0) {
            Rect faceRect = faces.toArray()[0];
            int x = faceRect.x + faceRect.width / 2;

            if (prevX != -1) {
                double proportion = (double) x / frameWidth;
                int moveAmount = (int) Math.round(proportion * 300);

                // Calculate the new position
                int newX = Math.max(0, Math.min(300 - BlasterGame.spaceship.getWidth(), moveAmount)); // magic

                SwingUtilities.invokeLater(() -> {
                    BlasterGame.spaceship.setLocation(newX, BlasterGame.spaceship.getY());
                });
            }

            prevX = x;
        } else {
            prevX = -1;
        }
    }
    private static BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();
        byte[] data = new byte[width * height * channels];
        mat.get(0, 0, data);

        BufferedImage image;
        if (channels == 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            image.getRaster().setDataElements(0, 0, width, height, data);
        } else if (channels == 3) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            image.getRaster().setDataElements(0, 0, width, height, data);
        } else {
            throw new IllegalArgumentException("Unsupported number of channels: " + channels);
        }
        return image;
    }
}