package guiAttempt;

import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WaveControlPanel extends JPanel {
	
	private int frequency;
	private Synthesizer synth;
	private int panelNum;
	
	private static final int MIN_FREQ = 0, MAX_FREQ = 15000;
	
	JLabel frequencyDisplay;
	public WaveControlPanel(int frequency, Synthesizer synth, int panelNum) {
		this.synth = synth;
		this.panelNum = panelNum;
		this.setLayout(new GridLayout(1, 0));
		
		JLabel frequencyInfoText = new JLabel("Frequency:");
		this.add(frequencyInfoText);
		
		frequencyDisplay = new JLabel();
		frequencyDisplay.setText(String.valueOf(frequency));
		this.add(frequencyDisplay);
		
		
		
		JSlider frequencyToPlay = new JSlider(JSlider.HORIZONTAL, MIN_FREQ, MAX_FREQ, frequency);
		frequencyToPlay.addChangeListener(new changeListener());
		frequencyToPlay.setMajorTickSpacing(5000);
		frequencyToPlay.setMinorTickSpacing(1000);
		frequencyToPlay.setPaintTicks(true);
		frequencyToPlay.setPaintLabels(true);
		
		this.add(frequencyToPlay);
		
	}
	
	private class changeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			setFrequency(source.getValue());
			
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
