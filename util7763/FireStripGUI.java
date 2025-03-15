import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class FireStripGUI extends JFrame {

    private static final int LED_COUNT = 120;
    private static final int LED_SIZE = 10;
    private static final int LED_SPACING = 1;
    private static final int WINDOW_WIDTH = LED_COUNT * (LED_SIZE + LED_SPACING) + LED_SPACING;
    private static final int WINDOW_HEIGHT = LED_SIZE + 2 * LED_SPACING + 50;
    private JPanel ledPanel;
    private Color[] ledColors;
    private Timer timer;
    private double time = 0;

    private Random random = new Random();
    private byte[] heat = new byte[LED_COUNT];
    private int cooling = 55;
    private int sparking = 120;
    private int speedDelay = 20;

    public FireStripGUI() {
        setTitle("LED Strip Display");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ledColors = new Color[LED_COUNT];
        ledPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < LED_COUNT; i++) {
                    g.setColor(ledColors[i]);
                    g.fillRect(i * (LED_SIZE + LED_SPACING) + LED_SPACING, LED_SPACING, LED_SIZE, LED_SIZE);
                }
            }
        };

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(ledPanel, BorderLayout.CENTER);

        timer = new Timer(speedDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEffect();
                ledPanel.repaint();
                time += 0.02; // Increment time by 20ms

            }
        });
        timer.start();

        setVisible(true);
    }

    private void fireEffect() {
        int cooldown;

        // Step 1. Cool down every cell a little
        for (int i = 0; i < LED_COUNT; i++) {
            cooldown = random.nextInt(((cooling * 10) / LED_COUNT) + 2);

            if ((heat[i] & 0xFF) <= cooldown) { // Compare unsigned values
                heat[i] = 0;
            } else {
                heat[i] = (byte) ((heat[i] & 0xFF) - cooldown); // Subtract unsigned, store signed
            }
        }

        // Step 2. Heat from each cell drifts 'up' and diffuses a little
        for (int k = LED_COUNT - 1; k >= 2; k--) {
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
        for (int j = 0; j < LED_COUNT; j++) {
            //System.out.print(" " + heat[j]);

            setPixelHeatColor(j, heat[j]);
            //setPixel(j, (int) heat[j] & 0xFF, 0, 0);
        }
        //System.out.println(""); // Byte value: " + j + " " + heat[j]);
    }

private void setPixelHeatColor(int pixel, byte temperature) {
        // Scale 'heat' down from 0-255 to 0-191 (using unsigned value)
        int tempUnsigned = temperature & 0xFF;
        int t192 = (int) Math.round((tempUnsigned / 255.0) * 191);

        // calculate ramp up from
        int heatramp = t192 & 0x3F; // 0..63
        heatramp <<= 2; // scale up to 0..252

        // figure out which third of the spectrum we're in:
        if (t192 > 0x80) { // hottest
            setPixel(pixel, 255, 255, heatramp);
        } else if (t192 > 0x40) { // middle
            setPixel(pixel, 255, heatramp, 0);
        } else { // coolest
            setPixel(pixel, heatramp, 0, 0);
        }
    }

    private void setPixel(int pixel, int red, int green, int blue) {
        ledColors[pixel] = new Color(red, green, blue);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FireStripGUI::new);
    }
}
