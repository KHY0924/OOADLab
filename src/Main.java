import views.MainFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Run database migrations/schema creation if needed (already handled by DAOs
        // mostly, but good practice)
        database.RunSchema.run();
        database.MockDataGenerator.insertMockData();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
        });
    }
}
