
import java.util.Random;
import javax.swing.*;

//import frc.robot.subsystems.led.LedStrip;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LedStripDemo {

    private LedStrip ledStrip; 
    private Random random = new Random();
    private Timer timer;
    private double time = 0;
    private LedMode mode;
    private Color primaryColor;
    private Color secondaryColor;

    // for the fire-effect
    private byte[] heat;

    // Pacman game variables
    private int pacWidth = 3; // pixel width of each object, must be larger than 1
    private double pacmanPosition;
    private double[] ghostPositions;
    private int pelletPosition = 5; // Pellet near the left
    private boolean pelletEaten = false;
    private boolean pacmanEating = false;
    private double pacmanNormalSpeed = 0.5; // Pixels per second
    private double pacmanEatingSpeed = 1.0;
    private double ghostSpeed = 0.4;
    private int pacmanDirection = -1; // 1 = right, -1 = left
    //private int ghostFleeingTimer = 0;
    //private static final int GHOST_FLEE_DURATION = 300; // 300*20ms = 6 seconds
    private Color[] ghostColors = {Color.RED, Color.PINK, Color.CYAN, Color.ORANGE};
    private Color pacmanColor = Color.YELLOW;
    private Color ghostFleeColor = Color.BLUE;
    private Color ghostFleeColor2 = Color.WHITE;

    

    public LedStripDemo(LedMode mode, Color primaryColor, Color secondaryColor) {
        /*-----------------------------------------------------------*/
        // these are set from arguments now.
        this.mode = mode;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        /*-----------------------------------------------------------*/

        ledStrip = new LedStrip();
        
        // fire
        heat = new byte[ledStrip.getBufferLength()];
        // pacman
        ghostPositions = new double[4];
        resetPacmanPositions();

        // 20ms to match Timer in real roboRio
        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //dispatchLedEffect(mode, saveColor);
                dispatchLedEffect();
                ledStrip.repaint();
                time += 0.02; // Increment time by 20ms
                ledStrip.setTime(time);

            }
        });
        timer.start();
        
    }

    // display demo gui (MAIN)
    public static void main(String[] args) {
        // Default values if no arguments are provided
        LedMode mode = LedMode.PACMAN;
        Color primaryColor = Color.RED;
        Color secondaryColor = Color.GREEN;

        // Parse arguments if they exist
        if (args.length >= 1) {
            try {
                mode = LedMode.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid LedMode: " + args[0] + ". Using default: PACMAN");
            }
        }
        if (args.length >= 2) {
            try {
                primaryColor = argparseColor(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid primary color: " + args[1] + ". Using default: RED");
            }
        }
            if (args.length >= 3) {
            try {
                secondaryColor = argparseColor(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid secondary color: " + args[2] + ". Using default: GREEN");
            }
        }

        // Create and run the LedStripDemo with the parsed arguments
        final LedMode finalMode = mode;
        final Color finalPrimaryColor = primaryColor;
        final Color finalSecondaryColor = secondaryColor;
        SwingUtilities.invokeLater(() -> new LedStripDemo(finalMode, finalPrimaryColor, finalSecondaryColor));
    }

       // Helper method to parse a color string (e.g., "255,0,0" or "RED")
       private static Color argparseColor(String colorString) {
        if (colorString.matches("\\d+,\\d+,\\d+")) {
            String[] rgb = colorString.split(",");
            int rz = Integer.parseInt(rgb[0]);
            int gz = Integer.parseInt(rgb[1]);
            int bz = Integer.parseInt(rgb[2]);
            return new Color(rz, gz, bz);
        } else {
            try {
                return (Color) Color.class.getField(colorString.toUpperCase()).get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new NumberFormatException("Invalid color: " + colorString);
            }
        }
    }

    private void waveEffectRedBlue() {
        for (int i = 0; i < ledStrip.getBufferLength(); i++) {
            double wave = Math.sin((i / (double) ledStrip.getBufferLength() * Math.PI * 2) + time);
            int red = (int) (127.5 * (1 + Math.sin(wave + time)));
            int blue = (int) (127.5 * (1 + Math.cos(wave + time)));
            ledStrip.setPixel(i, red, 0, blue);
        }
    }

    private void resetPacmanPositions() {
        pelletEaten = false;
        pacmanEating = false;
        pacmanDirection = -1;
        pacmanPosition = ledStrip.getBufferLength() - 1;
        ghostPositions[0] = ledStrip.getBufferLength() + 10;
        ghostPositions[1] = ledStrip.getBufferLength() + 18;
        ghostPositions[2] = ledStrip.getBufferLength() + 26;
        ghostPositions[3] = ledStrip.getBufferLength() + 34;
    }

    private void updatePacmanGame() {
        double pacmanSpeed;
        // Update Pacman position
        if (pelletEaten) {
            pacmanDirection = 1; // ate the pellet, now go right
            pacmanSpeed = pacmanEatingSpeed;
            pacmanEating = true;

        } else {
            pacmanDirection = -1 ; // go left
            pacmanSpeed = pacmanNormalSpeed;
            pacmanEating = false;
        }

        pacmanPosition += pacmanSpeed * pacmanDirection;

        // Limit pacman to the strip length.
        if (pacmanPosition < 0) {
            pacmanPosition = 0; // this should never happen
        } else if (pacmanPosition >= ledStrip.getBufferLength()) {
            resetPacmanPositions();
        }

        // Update Ghost positions
        for (int i = 0; i < ghostPositions.length; i++) {
            // if a ghost touches pacman then put that ghost off the screen
            if (Math.abs(pacmanPosition - ghostPositions[i]) < 2) {
                ghostPositions[i] = ledStrip.getBufferLength() * 2; 
            } else { // move ghost normally
                ghostPositions[i] = ghostPositions[i] + pacmanDirection * ghostSpeed;
            }
        }

        // Check if Pacman has reached the pellet
        if (!pelletEaten && Math.abs(pacmanPosition - pelletPosition) <= 2) {
            pelletEaten = true;
        }
    }

    private void updatePacmanDisplay() {

        int flashtime = (int) (ledStrip.getCurrentTime() / 0.2);

        // Clear the strip
        for (int i = 0; i < ledStrip.getBufferLength(); i++) {
            ledStrip.setPixel(i, Color.BLACK);
        }

        // Draw pellet
        if (!pelletEaten) {
            if ((int) (ledStrip.getCurrentTime() * 2) % 2 == 0) { // Flash every 0.5 seconds
                ledStrip.setPixelWidth(pelletPosition, Color.WHITE, pacWidth);
            }
        }

        // Draw Pacman
        //System.out.println("Pacman " + pacmanDirection + ": " + pacmanPosition);

        // Ensure pacmanPosition is within bounds or else dont draw him.
        if (pacmanPosition >= 0 && pacmanPosition < ledStrip.getBufferLength()) {
            int mouthinset = (flashtime % 2)*2;
            int pacpos = (int) pacmanPosition;
            if (pacmanDirection > 0) { // right
                ledStrip.setPixelWidth(pacpos, pacmanColor, pacWidth-mouthinset);
            } else {
                ledStrip.setPixelWidth(pacpos + mouthinset, pacmanColor, pacWidth-mouthinset);
            }
        }

        // Draw Ghosts
        for (int i = 0 ; i < ghostPositions.length ; i++) {
            Color ghostColor;
            if (pacmanEating) {
                // flash between blue and white
                ghostColor = flashtime % 2 == 0 ? ghostFleeColor : ghostFleeColor2;
            } else {
                // not eating, normal colors
                ghostColor = ghostColors[i];
            }
            // Ensure ghostPositions[i] is within bounds before drawing
            if (ghostPositions[i] >= 0 && ghostPositions[i] < ledStrip.getBufferLength()) {
                ledStrip.setPixelWidth((int) ghostPositions[i], ghostColor, pacWidth);
            }
        }

    }


    private void fireEffect() {
        int cooling = 55;
        int sparking = 120;
        int cooldown;

        // Step 1. Cool down every cell a little
        for (int i = 0; i < ledStrip.getBufferLength(); i++) {
            cooldown = random.nextInt(((cooling * 10) / ledStrip.getBufferLength()) + 2);

            if ((heat[i] & 0xFF) <= cooldown) { // Compare unsigned values
                heat[i] = 0;
            } else {
                heat[i] = (byte) ((heat[i] & 0xFF) - cooldown); // Subtract unsigned, store signed
            }
        }

        // Step 2. Heat from each cell drifts 'up' and diffuses a little
        for (int k = ledStrip.getBufferLength() - 1; k >= 2; k--) {
            int h1 = heat[k - 1] & 0xFF;
            int h2 = heat[k - 2] & 0xFF;
            heat[k] = (byte) ((h1 + h2 + h2) / 3);
        }

        // Step 3. Randomly ignite new 'sparks' near the bottom
        if (random.nextInt(256) < sparking) {
            int y = random.nextInt(7);
            heat[y] = (byte) (random.nextInt(160, 256)); // Directly assign unsigned value
        }

        // Step 4. Convert heat to LED colors
        for (int i = 0; i < ledStrip.getBufferLength(); i++) {
            
            byte temperature = heat[i];
                // Scale 'heat' down from 0-255 to 0-191 (using unsigned value)
            int tempUnsigned = temperature & 0xFF;
            int t192 = (int) Math.round((tempUnsigned / 255.0) * 191);

            // calculate ramp up from
            int heatramp = t192 & 0x3F; // 0..63
            heatramp <<= 2; // scale up to 0..252

            // figure out which third of the spectrum we're in:
            if (t192 > 0x80) { // hottest
                ledStrip.setPixel(i, 255, 255, heatramp); // yellow --> white
            } else if (t192 > 0x40) { // middle
                ledStrip.setPixel(i, 255, heatramp, 0); // red --> yellow
            } else { // coolest
                ledStrip.setPixel(i, heatramp, 0, 0); // black --> red
            }
        }
    }

    public void dispatchLedEffect() {
        dispatchLedEffect(this.mode, this.primaryColor, this.secondaryColor);
    }

    public void dispatchLedEffect(LedMode mode, Color primaryColor, Color secondaryColor) {
        if (mode == LedMode.SOLID) {
            solid(primaryColor);
        } else if (mode == LedMode.STROBE) {
            strobe(primaryColor, secondaryColor, 0.1);
        } else if (mode == LedMode.FLASH) {
            strobe(primaryColor, secondaryColor, 1.0);
        } else if (mode == LedMode.WAVE) {
            waveEffect(primaryColor, secondaryColor, 25.0, 1.0);
        } else if (mode == LedMode.WAVERB) {
            waveEffectRedBlue();
        } else if (mode == LedMode.FIRE) {
            fireEffect();
        } else if (mode == LedMode.PACMAN) {
            updatePacmanGame();
            updatePacmanDisplay();
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
        strobe(color, Color.BLACK, duration);
    }

    private void waveEffect(Color c1, Color c2, double cycleLength, double duration) {
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
          ledStrip.setPixelMix(i, c1, c2, ratio);
        }
    }
    

    public static enum LedMode {
        SOLID,
        STROBE,
        WAVE,
        WAVERB,
        FIRE,
        PACMAN,
        FLASH;
    }
    
}
