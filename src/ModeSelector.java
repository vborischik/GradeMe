import javax.swing.*;
import java.awt.*;

public class ModeSelector {

    public static String chooseMode() {
        JDialog dialog = new JDialog((Frame) null, "Choose Mode", true);
        dialog.setSize(300, 120);
        dialog.setLayout(new FlowLayout());
        dialog.setLocationRelativeTo(null);

        final String[] result = new String[1];

        JButton fileBtn = new JButton("File");
        JButton aiBtn = new JButton("AI");

        fileBtn.addActionListener(e -> {
            result[0] = "file";
            dialog.dispose();
        });

        aiBtn.addActionListener(e -> {
            result[0] = "ai";
            dialog.dispose();
        });

        dialog.add(fileBtn);
        dialog.add(aiBtn);
        dialog.setVisible(true);

        return result[0];
    }
}