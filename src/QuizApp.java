import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizApp extends JFrame {

    private List<Question> questions;
    private int index = 0;
    private int score = 0;

    private JLabel questionLabel = new JLabel();
    private JRadioButton[] options = new JRadioButton[4];
    private ButtonGroup group = new ButtonGroup();

    public QuizApp(List<Question> questions) {
        this.questions = questions;

        setTitle("Quiz App");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(questionLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            group.add(options[i]);
            panel.add(options[i]);
        }

        add(panel, BorderLayout.CENTER);

        JButton next = new JButton("Next");
        add(next, BorderLayout.SOUTH);

        next.addActionListener(e -> next());

        showQuestion();
        setVisible(true);
    }

    private void showQuestion() {
        if (index >= questions.size()) {
            JOptionPane.showMessageDialog(this,
                    "Score: " + score + "/" + questions.size());
            System.exit(0);
        }

        Question q = questions.get(index);
        questionLabel.setText(q.getQuestion());

        for (int i = 0; i < 4; i++) {
            options[i].setText(q.getOptions()[i]);
        }

        group.clearSelection();
    }

    private void next() {
        Question q = questions.get(index);

        for (JRadioButton btn : options) {
            if (btn.isSelected() && btn.getText().equals(q.getAnswer())) {
                score++;
            }
        }

        index++;
        showQuestion();
    }

    public static void main(String[] args) {
        try {
            String mode = JOptionPane.showInputDialog("Enter 'file' or 'ai'");

            DataLoader<Question> loader;

            if ("ai".equalsIgnoreCase(mode)) {
                String key = JOptionPane.showInputDialog("API key");
                loader = LoaderFactory.create("ai", key);
                new QuizApp(loader.load("Java basics"));
            } else {
                loader = LoaderFactory.create("file", null);
                new QuizApp(loader.load("questions.json"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}