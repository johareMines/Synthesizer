

import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

public class Boop extends JFrame implements KeyListener {
    private TextArea textArea = new TextArea();

    private class Noise implements Runnable {

        private int noise[];
        public boolean sound = true;
        private final int freq;
        private int ms = 100;
        private int samples = (int) ((ms * SAMPLE_RATE) / 1000);
        private byte[] output = new byte[samples];
        private int i = 0;
        private double period;

        public Noise(int freq) {
            this.freq = freq;
            period = (double) SAMPLE_RATE / freq;
            new Thread(this).start();
        }

        @Override
        public void run() {
            SourceDataLine line;
            try {
                line = AudioSystem.getSourceDataLine(af);
                line.open(af, SAMPLE_RATE);
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
                return;
            }
            line.start();
            while (i < output.length) {
                double angle = 2.0 * Math.PI * i / period * 1.0 + 0.08;
                output[i++] += (byte) (Math.sin(angle) * 127f);
            }
            while (sound) {
                line.write(output, 0, output.length);
            }
            line.close();
        }
    }

    private class Reader implements Runnable {
        private final String sz;
        Reader(String s) {
            sz = s;
            new Thread(this).start();
        }
        public void run() {
            Noise n;
            for (char c: sz.toCharArray()) {
                n = new Noise(c % 20 * 110);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    continue;
                }
                n.sound = false;
            }
        }
    }

    private static final int SAMPLE_RATE = 16 * 1024;
    private final AudioFormat af = 
            new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
    private HashMap<Character, Noise> noises;

    public static void main(String[] args) {
        new Boop();
    }

    public Boop()  {
        noises = new HashMap<>();
        textArea = new TextArea(20, 80);
        textArea.addKeyListener(this);
        add(textArea);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        try {
            keyReleased(ke);
        } finally {
            char ch = ke.getKeyChar();
            Noise t = new Noise(ch % 20 * 110);
            noises.put(ch, t);
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER)
            new Reader(textArea.getText().toString());
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        try {
            noises.get(ke.getKeyChar()).sound = false;
        } finally {
            return;
        }
    }
}