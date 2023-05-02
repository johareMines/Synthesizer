package guiAttempt;

import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WaveControlPanel extends JPanel {
	
	private int frequency;
	private int volume = 10;
	private Synthesizer synth;
	private int panelNum;
	
	private static final int MIN_FREQ = 0, MAX_FREQ = 15000;
	private static final int MIN_VOL = 0, MAX_VOL = 10;
	
	JLabel frequencyDisplay;
	JLabel volDisplay;
	public WaveControlPanel(int frequency, Synthesizer synth, int panelNum) {
		this.synth = synth;
		this.panelNum = panelNum;
		this.setLayout(new GridLayout(1, 0));
		
		
		
		JPanel freqPanel = new JPanel();
		freqPanel.setLayout(new GridLayout(0,1));
		freqPanel.setBorder(BorderFactory.createEmptyBorder(-50, 0, -10, 0));
		
		JLabel frequencyInfoText = new JLabel("Frequency:");
		frequencyInfoText.setHorizontalAlignment(JLabel.CENTER);
		freqPanel.add(frequencyInfoText);
		
		frequencyDisplay = new JLabel();
		frequencyDisplay.setText(String.valueOf(frequency));
		frequencyDisplay.setHorizontalAlignment(JLabel.CENTER);
		freqPanel.add(frequencyDisplay);
		
		JSlider frequencyToPlay = new JSlider(JSlider.HORIZONTAL, MIN_FREQ, MAX_FREQ, frequency);
		frequencyToPlay.addChangeListener(new freqChangeListener());
		frequencyToPlay.setMajorTickSpacing(5000);
		frequencyToPlay.setMinorTickSpacing(1000);
		frequencyToPlay.setPaintTicks(true);
		frequencyToPlay.setPaintLabels(true);
		
		freqPanel.add(frequencyToPlay);
		
		/////////////
		
		JPanel volPanel = new JPanel();
		volPanel.setLayout(new GridLayout(0,1));
		
		JLabel volInfoText = new JLabel("Volume:");
		volInfoText.setHorizontalAlignment(JLabel.CENTER);
		volPanel.add(volInfoText);
		
		volDisplay = new JLabel();
		volDisplay.setText(String.valueOf(volume));
		volDisplay.setHorizontalAlignment(JLabel.CENTER);
		volPanel.add(volDisplay);
		
		JSlider volToPlay = new JSlider(JSlider.HORIZONTAL, MIN_VOL, MAX_VOL, volume);
		volToPlay.addChangeListener(new volChangeListener());
		volToPlay.setMajorTickSpacing(2);
		volToPlay.setMinorTickSpacing(1);
		volToPlay.setPaintTicks(true);
		volToPlay.setPaintLabels(true);
		
		volPanel.add(volToPlay);
		
		this.add(volPanel);
		this.add(freqPanel);
		
	}
	
	private class freqChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			setFrequency(source.getValue());
			
			//Update proper variable in Synthesizer
			//WaveControlPanel[] panels = synth.getWaveControls();
			
		}
		
	}
	
	private class volChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			//setFrequency(source.getValue());
			
			volume = source.getValue();
			
			volDisplay.setText(String.valueOf(volume));
			
			float volFloat = (float) (((float) volume) / 10.0);
			
			synth.setThreadVol(volFloat, panelNum);
			
			
			//Update proper variable in Synthesizer
			//WaveControlPanel[] panels = synth.getWaveControls();
			
		}
		
	}
	
	
	
	
	public void setFrequency(int frequency) {
		if (frequency >= 1) {
			this.frequency = frequency;
			frequencyDisplay.setText(String.valueOf(this.frequency));
			synth.setPanelFrequency(frequency, panelNum);
		}
	}
	
	
	

}
