package guiAttempt;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;

import utils.Utils;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.util.function.Supplier;

public class AudioThread extends Thread{
	
	static final int BUFFER_SIZE = 512; // How many samples each buffer contains
	static final int BUFFER_COUNT = 8; // How many buffers are in the queue
	
	private final Supplier<short[]> bufferSupplier;
	
	private final int[] buffers = new int[BUFFER_COUNT];
	private final long device = alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER)); // let API decide what device to open
	private final long context = alcCreateContext(device, new int[1]); // Passed in attributes is just one int 0
	private final int source;
	
	private int bufferIndex; // used in bufferSamples to apply to all
	private boolean closed;
	private boolean running;
	
	AudioThread(Supplier<short[]> bufferSupplier) {
		this.bufferSupplier = bufferSupplier;
		alcMakeContextCurrent(context); // Set context
		AL.createCapabilities(ALC.createCapabilities(device)); // So open AL knows what's possible
		
		source = alGenSources();
		
		// Initialize queue of buffers
		for (int i = 0; i < BUFFER_COUNT; i++) {
			bufferSamples(new short[0]); // Create all buffers filled with nothing, ready to be filled with audio
			
		}
		alSourcePlay(source);
		catchInternalException();
		start(); // calls run
		
	}
	
	boolean isRunning() {
		return running;
	}
	
	@Override
	public synchronized void run() {
		while (!closed) {
			while (!running) {
				Utils.invokeProcedure(this::wait, false); // Don't run when not needed, more efficient than sleeping every loop
			}
			
			int processedBuffers = alGetSourcei(source, AL_BUFFERS_PROCESSED);
			for (int i=0; i<processedBuffers; i++) {
				short[] samples = bufferSupplier.get();
				
				if (samples == null ) {
					running = false;
					break;
				}
				alDeleteBuffers(alSourceUnqueueBuffers(source));
				buffers[bufferIndex] = alGenBuffers();
				bufferSamples(samples);
			}
			
			if (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING) {
				alSourcePlay(source);
			}
			catchInternalException();
				
		}
		
		//Clean memory
		alDeleteSources(source); 
		alDeleteBuffers(buffers);
		alcDestroyContext(context);
		alcCloseDevice(device);
	}
	
	synchronized void triggerPlayback() {
		running = true;
		notify();
	}
	
	void close() {
		closed = true;
		// break out of loop
		triggerPlayback();
	}
	
	private void bufferSamples(short[] samples) {
		int buf = buffers[bufferIndex++]; //increment so it's ready the next time it's needed
		alBufferData(buf, AL_FORMAT_MONO16, samples, Synthesizer.AudioInfo.SAMPLE_RATE);
		
		alSourceQueueBuffers(source, buf);
		bufferIndex %= BUFFER_COUNT; // Modulo operation to keep bufferIndex between 0 and BUFFER_COUNT - 1
		
	}
	
	private void catchInternalException() {
		int error = alcGetError(device); // Device has an error flag that we want to check
		if (error != ALC_NO_ERROR) {
			throw new OpenALException(error);
		}
	}

}
