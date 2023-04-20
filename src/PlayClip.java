import java.io.ByteArrayInputStream;

import javax.sound.sampled.*;

public class PlayClip {
    public static void main(String[] args) {
        try {
            // Open the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(PlayClip.class.getResourceAsStream("res/c-note.wav"));

            // Get the audio format
            AudioFormat format = audioInputStream.getFormat();

            // Set up the data line for playback
            SourceDataLine dataLine = AudioSystem.getSourceDataLine(format);
            dataLine.open(format);
            dataLine.start();

            // Read the audio data into a byte array
            int playbackLength = (int) (audioInputStream.getFrameLength() * format.getFrameSize());
            if (playbackLength % 2 == 1) {
            	playbackLength --;
            }
            byte[] audioData = new byte[playbackLength];
            audioInputStream.read(audioData);

            // Create a new input stream from the byte array
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);

            // Set the start position in the stream to skip to
            long startPositionInBytes = (long)(44100 * 0.1); // e.g. skip the first half-second of the audio
            
            // Skip to the start position in the stream
            long bytesSkipped = byteArrayInputStream.skip(startPositionInBytes);
            if (bytesSkipped != startPositionInBytes) {
                throw new IllegalStateException("Unable to skip to specified start position in audio stream");
            }
            

            // Read and play back the audio data in a loop
            int bytesRead = 0;
            int iterations = 0;
            while (true) {
                byteArrayInputStream.reset();
                
                int totalBytesRead = 0;
                while ((bytesRead = byteArrayInputStream.read(audioData)) != -1 && totalBytesRead < audioData.length) {
                    int bytesToWrite = Math.min(bytesRead, audioData.length - totalBytesRead);
                    dataLine.write(audioData, 0, bytesToWrite);
                    totalBytesRead += bytesToWrite;
                }
                iterations ++;
                if (iterations >= 30) {
                	break;
                }
            }

            // Clean up resources
//            audioInputStream.close();
//            dataLine.drain();
//            dataLine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}