import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private JLabel titleLabel;
    private JComboBox<String> difficultyComboBox;
    private JComboBox<String> inputComboBox;
    private JButton startButton;
    private StartButtonClickListener startButtonClickListener;

    public MenuPanel(StartButtonClickListener listener) {
        setLayout(null);
        setBounds(0, 0, 400, 600);
        setOpaque(false);
        this.startButtonClickListener = listener;

        titleLabel = new JLabel("Asteroid Blaster");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.white);
        titleLabel.setBounds(100, 100, 200, 30);
        add(titleLabel);

        String[] difficultyLevels = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficultyLevels);
        difficultyComboBox.setBounds(150, 200, 100, 30);
        add(difficultyComboBox);

        String[] inputOptions = {"Keyboard", "Mouse", "Camera"};
        inputComboBox = new JComboBox<>(inputOptions);
        inputComboBox.setBounds(150, 250, 100, 30);
        add(inputComboBox);

        startButton = new JButton("Start");
        startButton.setBounds(150, 300, 100, 30);
        startButton.addActionListener(e -> {
            if (startButtonClickListener != null) {
                String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                String selectedMovement = (String) inputComboBox.getSelectedItem();
                startButtonClickListener.onStartButtonClicked(selectedDifficulty, selectedMovement);
            }
        });
        add(startButton);
    }

    public interface StartButtonClickListener {
        void onStartButtonClicked(String difficulty, String input);
    }
}