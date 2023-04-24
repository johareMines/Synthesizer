package guiAttempt;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.openal.ALC;
import org.lwjgl.system.MemoryUtil;

public class SharedAudioContext {
    
    private static long sharedContext;
    
    public static long createSharedContext() {
        if (sharedContext == 0) {
        	long device = alcOpenDevice((ByteBuffer) null);
            sharedContext = alcCreateContext(device, (int[])null);
            if (sharedContext == NULL) {
                throw new RuntimeException("Failed to create shared context.");
            }
            alcMakeContextCurrent(sharedContext);
            ALC.createCapabilities(sharedContext);
        }
        return sharedContext;
    }
    
    public static void destroySharedContext() {
        if (sharedContext != 0) {
            alcDestroyContext(sharedContext);
            sharedContext = 0;
        }
    }
}