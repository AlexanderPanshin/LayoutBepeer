package gui;

import audio.SoundPlayer;
import keyboard.KeyboardLayoutDetector;
import keyboard.LayoutMonitor;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Главное окно приложения
 */
public class MainWindow {
    private JFrame frame;
    private JTextField soundPathField;
    private JButton browseButton;
    private JButton testButton;
    private JButton hideButton;
    private JButton exitButton;
    private JLabel statusLabel;
    private JLabel currentLayoutLabel;
    
    private SoundPlayer soundPlayer;
    private KeyboardLayoutDetector layoutDetector;
    private LayoutMonitor layoutMonitor;
    private TrayManager trayManager;
    
    public MainWindow() {

        soundPlayer = new SoundPlayer(Constants.DEFAULT_SOUND);
        layoutDetector = new KeyboardLayoutDetector();
        
        createWindow();
        createLayoutMonitor();
        trayManager = new TrayManager(frame, soundPlayer, layoutDetector);
        
        layoutMonitor.startMonitoring();
    }
    
    private void createWindow() {
        frame = new JFrame(Constants.APP_NAME);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);
        
        JPanel mainPanel = createMainPanel();
        JPanel buttonPanel = createButtonPanel();
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        setupHotkeys();
        
        frame.setVisible(true);
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Текущая раскладка:"), gbc);
        
        gbc.gridx = 1;
        currentLayoutLabel = new JLabel(layoutDetector.getCurrentLayoutInfo());
        currentLayoutLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(currentLayoutLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Статус:"), gbc);
        
        gbc.gridx = 1;
        statusLabel = new JLabel("Мониторинг активен");
        statusLabel.setForeground(Color.GREEN.darker());
        panel.add(statusLabel, gbc);
        
        // Звуковой файл
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Звуковой файл:"), gbc);
        
        JPanel soundPanel = new JPanel(new BorderLayout(5, 0));
        soundPathField = new JTextField(Constants.DEFAULT_SOUND, 20);
        soundPanel.add(soundPathField, BorderLayout.CENTER);
        
        browseButton = new JButton("...");
        browseButton.setPreferredSize(new Dimension(40, 25));
        browseButton.addActionListener(e -> browseForSoundFile());
        soundPanel.add(browseButton, BorderLayout.EAST);
        
        gbc.gridx = 1;
        panel.add(soundPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        testButton = new JButton("Проверить звук");
        testButton.addActionListener(e -> soundPlayer.playSound());
        panel.add(testButton, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        hideButton = new JButton("Свернуть в трей");
        hideButton.addActionListener(e -> minimizeToTray());
        
        exitButton = new JButton("Выход");
        exitButton.addActionListener(e -> exitApplication());
        
        panel.add(hideButton);
        panel.add(exitButton);
        
        return panel;
    }
    
    private void createLayoutMonitor() {
        layoutMonitor = new LayoutMonitor(layoutDetector, soundPlayer, statusLabel, currentLayoutLabel);
    }
    
    private void browseForSoundFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите звуковой файл");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".wav");
            }
            @Override
            public String getDescription() {
                return "WAV файлы (*.wav)";
            }
        });
        
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            soundPathField.setText(path);
            soundPlayer.setSoundFilePath(path);
        }
    }
    
    private void minimizeToTray() {
        frame.setVisible(false);
        trayManager.showTrayNotification("Программа свернута в трей", "Двойной клик для открытия.");
    }
    
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(frame,
            "Вы действительно хотите выйти?",
            "Подтверждение выхода",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            layoutMonitor.stopMonitoring();
            trayManager.removeFromTray();
            System.exit(0);
        }
    }
    
    private void setupHotkeys() {
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "showWindow");
        frame.getRootPane().getActionMap().put("showWindow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWindow();
            }
        });
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                minimizeToTray();
            }
        });
    }
    
    public void showWindow() {
        frame.setVisible(true);
        frame.toFront();
        frame.setState(JFrame.NORMAL);
    }
    
    public JFrame getFrame() {
        return frame;
    }
}