import ui.LoginFrame;
import ui.components.Theme;
import util.GameConfig;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Apply Global AAA Design System Look and Feel
        Theme.applyGlobalLaf();

        System.out.println("=========================================");
        System.out.println(" ⚡ FITSO — Cyber Fitness Gaming Platform ");
        System.out.println("=========================================");
        System.out.println("Game Config Loaded: LEVEL_STEP = " + GameConfig.LEVEL_STEP);

        // Launch Login Experience Frame
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
