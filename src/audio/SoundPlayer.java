package audio;

import javax.sound.sampled.*;
import java.io.File;
import java.awt.Toolkit;


public class SoundPlayer {
    private String soundFilePath;
    
    public SoundPlayer(String defaultSoundPath) {
        this.soundFilePath = defaultSoundPath;
    }
    
    public void setSoundFilePath(String path) {
        if (path != null && !path.trim().isEmpty()) {
            this.soundFilePath = path;
        }
    }
    
    public String getSoundFilePath() {
        return soundFilePath;
    }
    

    public void playSound() {
        System.out.println("Воспроизведение звука: " + soundFilePath);
        
        File soundFile = new File(soundFilePath);
        if (!soundFile.exists() || !soundFile.canRead()) {
            System.err.println("Файл не найден или недоступен: " + soundFilePath);
            playFallbackSound();
            return;
        }
        
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(Math.min(6.0f, gainControl.getMaximum())); // +6 dB
            }
            
            clip.start();
            
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Неподдерживаемый формат аудиофайла");
            playFallbackSound();
        } catch (Exception e) {
            System.err.println("Ошибка воспроизведения: " + e.getMessage());
            playFallbackSound();
        }
    }
    

    private void playFallbackSound() {
        Toolkit.getDefaultToolkit().beep();
    }
    

    public boolean isSoundFileAvailable() {
        File file = new File(soundFilePath);
        return file.exists() && file.canRead() && file.length() > 0;
    }
}