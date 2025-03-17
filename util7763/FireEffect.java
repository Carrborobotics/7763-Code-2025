import java.util.Random;

public class FireEffect {
    private byte[] heat;
    private Random random = new Random();
    private int cooling;
    private int sparking;
    
    public FireEffect(int numLeds) {
        heat = new byte[numLeds];
        cooling = 55;
        sparking = 120;
    }

    public void update(LedStrip ledStrip) {
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
}

