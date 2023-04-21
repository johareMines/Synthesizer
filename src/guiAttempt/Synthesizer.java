package guiAttempt;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class Synthesizer {
	private boolean shouldGenerate;
	private int wavePos;
	
	private final JFrame frame = new JFrame("Synthesizer");
	private final AudioThread audioThread = new AudioThread(() -> {
		
		if(!shouldGenerate) {
			return null;
		}
		short[] s = new short[AudioThread.BUFFER_SIZE];
		for (int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
			s[i] = (short) (Short.MAX_VALUE * Math.sin((2 * Math.PI * 440) / AudioInfo.SAMPLE_RATE * wavePos++));
		}
		return s;
	});
	
	
	
	
	
	Synthesizer() {
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (!audioThread.isRunning()) {
					shouldGenerate = true;
					audioThread.triggerPlayback();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				shouldGenerate = false;
			}
		});
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				audioThread.close();
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(650, 400);
		frame.setResizable(false);
		frame.setLayout(null); // No layout manager managing layout
		frame.setLocationRelativeTo(null); // Appear in middle of screen, not top left
		frame.setVisible(true);
	}
	
	public static class AudioInfo {
		public static final int SAMPLE_RATE = 44100;
	}

}
