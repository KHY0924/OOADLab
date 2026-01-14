import views.MainFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Use CrossPlatform (Metal) or Nimbus for reliable custom coloring
        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // We'll stick to default (Metal/CrossPlatform) or force styling via Theme.
        // If we want modern components without native interference:
        // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

        // For now, let's keep it simple and rely on Theme.java's setUI

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
        });
    }
}
