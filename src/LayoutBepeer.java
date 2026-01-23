import gui.MainWindow;
import javax.swing.SwingUtilities;


public class LayoutBepeer {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainWindow();
            } catch (Exception e) {
                System.err.println("Ошибка запуска приложения: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}