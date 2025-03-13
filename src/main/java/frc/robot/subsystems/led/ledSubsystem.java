package frc.robot.subsystems.led;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ledSubsystem extends SubsystemBase {

    LedStrip ledStrip;    
    int red, green, blue;
    Color primaryColor;
    Color secondaryColor;
    LedMode mode;
    private FireEffect fireEffect;
    private PacmanEffect pacmanEffect;

    public ledSubsystem() {
        ledStrip = new LedStrip();
        mode = LedMode.SOLID;
        primaryColor = Color.kBlack;
        secondaryColor = Color.kBlack;
        fireEffect = new FireEffect(ledStrip.getBufferLength());
        pacmanEffect = new PacmanEffect(ledStrip.getBufferLength());
    }

    // setter for current primary Color
    public void setColor(Color color) {
        primaryColor = color;
    }
    public void setSecondaryColor(Color color) {
        secondaryColor = color;
    }

    // setter for active mode
    public void setMode(LedMode amode) {
        mode = amode;
    }
    
    @Override
    public void periodic() {
        dispatchLedEffect(mode, primaryColor);
        ledStrip.repaint();
        SmartDashboard.putString("led/colorRgb", this.primaryColor.toString());
        SmartDashboard.putString("led/mode", this.mode.toString());
    }

    public void dispatchLedEffect(LedMode mode, Color primaryColor) {
        if (mode == LedMode.SOLID) {
            solid(primaryColor);
        } else if (mode == LedMode.STROBE) {
            strobe(primaryColor, 0.1);
        } else if (mode == LedMode.FLASH) {
            strobe(primaryColor, 1.0);
        } else if (mode == LedMode.WAVE) {
            wave(primaryColor, secondaryColor, 25.0, 1.0);  
        } else if (mode == LedMode.WAVE2) {
            wave(primaryColor, secondaryColor, 10.0, 0.5);  
        } else if (mode == LedMode.FIRE) {
            fireEffect.update(ledStrip);
        } else if (mode == LedMode.PACMAN) {
            pacmanEffect.update(ledStrip);
        }
   
    }
    
    private void solid(Color color) {
        if (color != null) {
            for (var i = 0; i < ledStrip.getBufferLength(); i++) {
                ledStrip.setPixel(i, color);
            }
        }
    }

    // two color strobe
    private void strobe(Color c1, Color c2, double duration) {
        boolean c1On = ((ledStrip.getCurrentTime() % duration) / duration) > 0.5;
        solid(c1On ? c1 : c2);
    }
    
    // one color strobe
    private void strobe(Color color, double duration) {
        strobe(color, Color.kBlack, duration);
    }

    private void wave(Color c1, Color c2, double cycleLength, double duration) {
        double waveExponent = 0.4;
        double x = (1 - ((ledStrip.getCurrentTime() % duration) / duration)) * 2.0 * Math.PI;
        double xDiffPerLed = (2.0 * Math.PI) / cycleLength;
        for (var i = 0; i < ledStrip.getBufferLength(); i++) {
            x += xDiffPerLed;
            double ratio = (Math.pow(Math.sin(x), waveExponent) + 1.0) / 2.0;
            if (Double.isNaN(ratio)) {
                ratio = (-Math.pow(Math.sin(x + Math.PI), waveExponent) + 1.0) / 2.0;
            }
            if (Double.isNaN(ratio)) {
                ratio = 0.5;
            }
            ledStrip.setMixPixel(i, c1, c2, ratio);
        }
    }

    
    

    public static enum LedMode {
        SOLID,
        STROBE,
        WAVE,
        WAVE2,
        FLASH,
        PACMAN,
        FIRE;
    }
    
}
