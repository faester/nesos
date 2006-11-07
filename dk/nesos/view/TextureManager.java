package dk.nesos.view;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

/**
 * [TODO: work in progress - how this should be implemented has not been decided]
 *
 * Ideas:
 * <OL>
 * <LI>Implement as singleton.</LI>
 * <LI>If loading textures might want to use some caching scheme.</LI>
 * <LI>Might want to cache the use of GL texture names regardless of above.</LI>
 * </OL>
 * 
 * @author ndhb
 */
public final class TextureManager {

  private static IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer(1); // temporary data
  private static FloatBuffer tmpFloatBuffer = BufferUtils.createFloatBuffer(16); // temporary data
  
  public static boolean isTextureResident(int name, int textureType) {
    GL11.glBindTexture(textureType, name);  
    GL11.glGetTexParameter(textureType, GL11.GL_TEXTURE_RESIDENT, tmpIntBuffer);
    return (tmpIntBuffer.get(0) == 0 ? false : true);
  } // method
    
  public static float getTexturePriority(int name) {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
    GL11.glGetTexParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_PRIORITY, tmpFloatBuffer);
    return tmpFloatBuffer.get(0);
  } // method

  public static boolean isTextureCompressed(int name) {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
    GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, ARBTextureCompression.GL_TEXTURE_COMPRESSED_ARB, tmpFloatBuffer); // FIXME: Allow EXTTextureCompressionS3TC a.o. as well?
    boolean compressed = tmpFloatBuffer.get(0) != 0.0f;
    System.err.println("Compressed = " + compressed); // DEBUG
    if (compressed) {
      GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, ARBTextureCompression.GL_TEXTURE_IMAGE_SIZE_ARB, tmpFloatBuffer);
      System.err.println("Compressed Size: " + tmpFloatBuffer.get(0)); // DEBUG
    } else {
      GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT, tmpFloatBuffer); // DEBUG
      System.err.println("Internal Format: " + (int) tmpFloatBuffer.get(0));
    } // if
    return compressed;
  } // method
  
} // class
