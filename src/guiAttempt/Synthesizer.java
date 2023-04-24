package guiAttempt;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class Synthesizer {
	private static Synthesizer theInstance;
	
	final long sharedContext = SharedAudioContext.createSharedContext();
	
	
	private static final int NUM_CONTROLS = 1;
	private static WaveControlPanel[] waveControls;
	private int[] threadFrequencies;
	private AudioThread[] audioThreads;
	
	private boolean shouldPlayAudio;
	private int wavePos;
	
	
	private final JFrame frame = new JFrame("Synthesizer");
//	private AudioThread audioThread1 = new AudioThread(() -> {
//		return generateAudioData(thread1Frequency);
//	});
	
	
	
	private short[] generateAudioData(int frequency) {
		//If the audio thread is muted, we don't want playback
		if(!shouldPlayAudio) {
			return null;
		}
		
		//Create an array named s that will store audio data
		short[] s = new short[AudioThread.BUFFER_SIZE];
		//wavePos %= AudioInfo.SAMPLE_RATE;
		for (int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
			//Fill the buffer with the initial frequency of the tone to play
			double sinInput = (2 * Math.PI * frequency) / AudioInfo.SAMPLE_RATE * wavePos++;
			sinInput = Math.round(sinInput * 10.0)/10.0;
			//System.out.println("gAD sinInput: " + sinInput);
			short newSi = (short) (Short.MAX_VALUE * Math.sin(sinInput));
			//System.out.println("gAD: " + newSi);
			s[i] = newSi;
			wavePos %= AudioInfo.SAMPLE_RATE;
		}
		return s;
	}
//	private short[][] sineWaveTable = new short[AudioInfo.SAMPLE_RATE / 2][AudioThread.BUFFER_SIZE];
//	
//	private void generateSineWaveTable() {
//	    for (int i = 0; i < sineWaveTable.length; i++) {
//	        double frequency = i;
//	        for (int j = 0; j < AudioThread.BUFFER_SIZE; j++) {
//	            double sinInput = (2 * Math.PI * frequency) / AudioInfo.SAMPLE_RATE * j;
//	            sineWaveTable[i][j] = (short) (Short.MAX_VALUE * Math.sin(sinInput));
//	        }
//	    }
//	}
//	
//	
//	
//	private short[] generateAudioData(int frequency) {
//	    // If the audio thread is muted, we don't want playback
//	    if (!shouldPlayAudio) {
//	        return null;
//	    }
//
//	    // Create an array named s that will store audio data
//	    short[] s = sineWaveTable[frequency];
//
//	    // Increment the wavePos variable to change the phase of the sine wave
//	    wavePos += AudioThread.BUFFER_SIZE;
//
//	    return s;
//	}
	
	
	Synthesizer() {
		
//		if (!audioThread1.isRunning()) {
//			shouldPlayAudio = true;
//			audioThread1.triggerPlayback();
//		}
//		frame.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				thread1Frequency += 5;
//				waveControl1.setFrequency(thread1Frequency);
//				audioThread1.setAudioData(() -> {
//					return generateAudioData(thread1Frequency);
//				});
//			}
//			
//			@Override
//			public void keyReleased(KeyEvent e) {
//				//shouldPlayAudio = false;
//			}
//		});
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				for (int i=0; i<audioThreads.length; i++) {
					audioThreads[i].close();
				}
				
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(650, 400);
		frame.setResizable(false);
		//frame.setLayout(null); // No layout manager managing layout
		frame.setLocationRelativeTo(null); // Appear in middle of screen, not top left
		
		
		waveControls = new WaveControlPanel[NUM_CONTROLS];
		threadFrequencies = new int[NUM_CONTROLS];
		audioThreads = new AudioThread[NUM_CONTROLS];
		
		int baseFrequency = 440;
		
		
		
		
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		for (int i=0; i<waveControls.length; i++) {
			threadFrequencies[i] = baseFrequency * (i+1);
			waveControls[i] = new WaveControlPanel(threadFrequencies[i], this, i);
			frame.add(waveControls[i]);
			
			final int index = i;
			shouldPlayAudio = true;
			audioThreads[index] = new AudioThread(() -> {
				return generateAudioData(threadFrequencies[index]);
			});
			
			if (!audioThreads[0].isRunning()) {
				audioThreads[i].triggerPlayback();
			}
			System.out.println(audioThreads[0].isRunning());
		}
		
		
		frame.setVisible(true);
	}
	
	public static class AudioInfo {
		public static final int SAMPLE_RATE = 44100;
	}

	public static Synthesizer getTheInstance() {
		if (theInstance == null) {
			theInstance = new Synthesizer();
		}
		return theInstance;
	}
	
	


	public WaveControlPanel[] getWaveControls() {
		return waveControls;
	}


	public void setPanelFrequency(int frequency, int panelNum) {
		threadFrequencies[panelNum] = frequency;
	}

}
