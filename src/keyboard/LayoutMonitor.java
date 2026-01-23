package keyboard;

import audio.SoundPlayer;
import utils.Constants;
import javax.swing.*;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;


public class LayoutMonitor {
    private Timer monitorTimer;
    private boolean monitoring = false;
    private KeyboardLayoutDetector detector;
    private SoundPlayer soundPlayer;
    private JLabel statusLabel;
    private JLabel layoutLabel;
    
    public LayoutMonitor(KeyboardLayoutDetector detector, SoundPlayer soundPlayer, 
                         JLabel statusLabel, JLabel layoutLabel) {
        this.detector = detector;
        this.soundPlayer = soundPlayer;
        this.statusLabel = statusLabel;
        this.layoutLabel = layoutLabel;
    }
    

    public void startMonitoring() {
        if (monitorTimer != null) {
            monitorTimer.cancel();
        }
        
        monitoring = true;
        updateStatus("Мониторинг активен", Color.GREEN.darker());
        

        SwingUtilities.invokeLater(() -> {
            layoutLabel.setText(detector.getCurrentLayoutInfo());
        });
        
        monitorTimer = new Timer(true);
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    layoutLabel.setText(detector.getCurrentLayoutInfo());
                    
                    if (detector.hasLayoutChanged()) {
                        soundPlayer.playSound();
                    }
                });
            }
        }, 0, Constants.CHECK_INTERVAL);
    }
    
    public void stopMonitoring() {
        if (monitorTimer != null) {
            monitorTimer.cancel();
            monitorTimer = null;
        }
        monitoring = false;
        updateStatus("Мониторинг остановлен", Color.RED);
    }
    
    public boolean isMonitoring() {
        return monitoring;
    }
    
    private void updateStatus(String text, Color color) {
        if (statusLabel != null) {
            statusLabel.setText(text);
            statusLabel.setForeground(color);
        }
    }
}