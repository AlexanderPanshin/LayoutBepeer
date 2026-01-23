package gui;

import audio.SoundPlayer;
import keyboard.KeyboardLayoutDetector;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


public class TrayManager {
    private TrayIcon trayIcon;
    private SystemTray tray;
    private JFrame mainFrame;
    private SoundPlayer soundPlayer;
    private KeyboardLayoutDetector layoutDetector;
    
    public TrayManager(JFrame frame, SoundPlayer soundPlayer, KeyboardLayoutDetector detector) {
        this.mainFrame = frame;
        this.soundPlayer = soundPlayer;
        this.layoutDetector = detector;
        
        if (SystemTray.isSupported()) {
            createTrayIcon();
        } else {
            System.err.println("Системный трей не поддерживается");
        }
    }
    
    private void createTrayIcon() {
        try {
            tray = SystemTray.getSystemTray();
            
            Image image = createTrayImage();
            trayIcon = new TrayIcon(image, "Keyboard Layout Monitor");
            trayIcon.setImageAutoSize(true);
            
            // Создаем контекстное меню
            PopupMenu popup = new PopupMenu();
            
            MenuItem showItem = new MenuItem("Показать окно");
            showItem.addActionListener(e -> showMainWindow());
            
            MenuItem soundItem = new MenuItem("Проверить звук");
            soundItem.addActionListener(e -> soundPlayer.playSound());
            
            MenuItem layoutItem = new MenuItem("Текущая раскладка");
            layoutItem.addActionListener(e -> showCurrentLayout());
            
            MenuItem exitItem = new MenuItem("Выход");
            exitItem.addActionListener(e -> exitApplication());
            
            popup.add(showItem);
            popup.add(soundItem);
            popup.add(layoutItem);
            popup.addSeparator();
            popup.add(exitItem);
            
            trayIcon.setPopupMenu(popup);
            trayIcon.addActionListener(e -> showMainWindow());
            
            tray.add(trayIcon);
            
            showTrayNotification("Keyboard Layout Monitor", 
                "Программа запущена и свернута в трей");
            
        } catch (Exception e) {
            System.err.println("Ошибка создания иконки трея: " + e.getMessage());
        }
    }
    
    private Image createTrayImage() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillOval(0, 0, size-1, size-1);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "K";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return image;
    }
    
    public void showTrayNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }
    
    private void showMainWindow() {
        mainFrame.setVisible(true);
        mainFrame.toFront();
        mainFrame.setState(JFrame.NORMAL);
    }
    
    private void showCurrentLayout() {
        String layoutInfo = layoutDetector.getCurrentLayoutInfo();
        showTrayNotification("Текущая раскладка", layoutInfo);
    }
    
    private void exitApplication() {
        if (mainFrame != null) {
            WindowEvent closingEvent = new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING);
            mainFrame.dispatchEvent(closingEvent);
        }
    }
    
    public void removeFromTray() {
        if (trayIcon != null && tray != null) {
            tray.remove(trayIcon);
        }
    }
}