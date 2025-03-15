import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaveStripGUI extends JFrame {

    private static final int LED_COUNT = 100;
    private static final int LED_SIZE = 10;
    private static final int LED_SPACING = 1;
    private static final int WINDOW_WIDTH = LED_COUNT * (LED_SIZE + LED_SPACING) + LED_SPACING;
    private static final int WINDOW_HEIGHT = LED_SIZE + 2 * LED_SPACING + 50;
    private JPanel ledPanel;
    private Color[] ledColors;
    private Timer timer;
    private double time = 0;

    public WaveStripGUI() {
        setTitle("LED Strip Display - Wave");
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

        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLEDs();
                ledPanel.repaint();
                time += 0.02; // Increment time by 20ms
            }
        });
        timer.start();

        setVisible(true);
    }

    private void updateLEDs() {
        for (int i = 0; i < LED_COUNT; i++) {
            double wave = Math.sin((i / (double) LED_COUNT * Math.PI * 2) + time);
            int red = (int) (127.5 * (1 + Math.sin(wave + time)));
            int blue = (int) (127.5 * (1 + Math.cos(wave + time)));
            ledColors[i] = new Color(red, 0, blue);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WaveStripGUI::new);
    }
}
