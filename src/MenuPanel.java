import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private JLabel titleLabel;
    private JComboBox<String> difficultyComboBox;
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

        startButton = new JButton("Start");
        startButton.setBounds(150, 300, 100, 30);
        startButton.addActionListener(e -> {
            if (startButtonClickListener != null) {
                String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                startButtonClickListener.onStartButtonClicked(selectedDifficulty);
            }
        });
        add(startButton);
    }

    public interface StartButtonClickListener {
        void onStartButtonClicked(String difficulty);
    }
}
