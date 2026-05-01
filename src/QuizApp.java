import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizApp extends JFrame {

    private List<Question> questions;
    private int index = 0;
    private int score = 0;

    private boolean flashcardMode;
    private int remembered = 0;

    private JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
    private JRadioButton[] options = new JRadioButton[4];
    private ButtonGroup group = new ButtonGroup();
    private JButton nextButton = new JButton("Next");

    private JProgressBar progressBar = new JProgressBar();

    public QuizApp(List<Question> questions, boolean flashcardMode) {
        this.questions = questions;
        this.flashcardMode = flashcardMode;

        setTitle("Quiz App");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Progress bar
        progressBar.setMinimum(0);
        progressBar.setMaximum(questions.size());
        add(progressBar, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());

        questionLabel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        centerPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            group.add(options[i]);
            optionsPanel.add(options[i]);
        }

        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Next button
        add(nextButton, BorderLayout.SOUTH);
        nextButton.addActionListener(e -> next());

        showQuestion();
        setVisible(true);
    }

    private void showQuestion() {
        if (index >= questions.size()) {
            if (flashcardMode) {
                showFlashcardResult();
            } else {
                showFinalResult();
            }
            return;
        }

        Question q = questions.get(index);

        // Question text + instruction
        questionLabel.setText(
                "<html><body style='width:600px; font-size:14px'>" +
                        "<b>" + q.getQuestion() + "</b>" +
                        (flashcardMode ? "<br><br><span style='font-style:italic;color:gray'>Think of the answer, then click Next</span>" : "") +
                        "</body></html>"
        );

        for (int i = 0; i < 4; i++) {
            options[i].setText(q.getOptions()[i]);
            options[i].setVisible(!flashcardMode);
        }

        nextButton.setText(flashcardMode ? "I remembered / Next" : "Next");

        group.clearSelection();
        updateProgress();
    }

    private void next() {
        Question q = questions.get(index);

        if (flashcardMode) {

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Did you remember this question?",
                    "Flashcard",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                remembered++;
            }

        } else {

            boolean answered = false;

            for (JRadioButton btn : options) {
                if (btn.isSelected()) {
                    answered = true;
                    if (btn.getText().equals(q.getAnswer())) {
                        score++;
                    }
                }
            }

            if (!answered) {
                JOptionPane.showMessageDialog(this, "Please select an answer");
                return;
            }
        }

        index++;
        showQuestion();
    }

    private void updateProgress() {
        progressBar.setValue(index);
    }

    private void showFlashcardResult() {
        double percent = (double) remembered / questions.size() * 100;

        if (percent < 50) {
            JOptionPane.showMessageDialog(this,
                    "NOT ready (" + (int) percent + "%). Study more.");
            dispose();
            openMainMenu();

        } else if (percent >= 80) {
            JOptionPane.showMessageDialog(this,
                    "READY (" + (int) percent + "%). Starting test...");

            // switch to test mode
            flashcardMode = false;
            index = 0;
            score = 0;
            remembered = 0;

            progressBar.setValue(0);

            for (JRadioButton btn : options) {
                btn.setVisible(true);
            }

            showQuestion();

        } else {
            JOptionPane.showMessageDialog(this,
                    "Almost ready (" + (int) percent + "%). Keep practicing.");
            dispose();
            openMainMenu();
        }
    }

    private void showFinalResult() {
        JOptionPane.showMessageDialog(this,
                "Final Score: " + score + "/" + questions.size());
        dispose();
        openMainMenu();
    }

    private void openMainMenu() {
        SwingUtilities.invokeLater(() -> {
            main(null);
        });
    }
    // ===================== MAIN =====================

    public static void main(String[] args) {
        try {
            String mode = ModeSelector.chooseMode();

            if (mode == null) System.exit(0);

            DataLoader<Question> loader;
            List<Question> questions;

            if ("ai".equalsIgnoreCase(mode)) {

                String key = System.getenv("NVIDIA_API_KEY");

                if (key == null || key.isBlank()) {
                    key = JOptionPane.showInputDialog("Enter API key");
                }

                loader = LoaderFactory.create("ai", key);
                questions = loader.load("Java basics");

                new QuizApp(questions, false); // AI = test only

            } else {

                loader = LoaderFactory.create("file", null);
                questions = loader.load("questions.json");

                boolean studyMode = chooseStudyMode();

                new QuizApp(questions, studyMode); // true = flashcards, false = test
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // ===================== STUDY / TEST SELECTOR =====================

    private static boolean chooseStudyMode() {
        int result = JOptionPane.showConfirmDialog(
                null,
                "Do you want STUDY mode?\nYes = Study (flashcards)\nNo = Test",
                "Choose Mode",
                JOptionPane.YES_NO_OPTION
        );

        return result == JOptionPane.YES_OPTION;
    }
}