package dk.nesos.util;

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

	private static IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer(16); // temporary data
	private static FloatBuffer tmpFloatBuffer = BufferUtils.createFloatBuffer(16); // temporary data
//	private static float MAX_TEXTURE_MAX_ANISOTROPY_EXT; // implementation specific constant cached

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

	public static boolean isTextureCompressed2(int name) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
		GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, ARBTextureCompression.GL_TEXTURE_COMPRESSED_ARB, tmpFloatBuffer); // FIXME: Allow EXTTextureCompressionS3TC a.o. as well?
		boolean compressed = tmpFloatBuffer.get(0) != 0.0f;
		System.err.println("Compressed = " + compressed); // DEBUG
		if (compressed) {
			GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, ARBTextureCompression.GL_TEXTURE_IMAGE_SIZE_ARB, tmpFloatBuffer);
			System.err.println("Compressed Size: " + tmpFloatBuffer.get(0)); // DEBUG
		} //else {
		GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT, tmpFloatBuffer); // DEBUG
		System.err.println("Internal Format: " + (int) tmpFloatBuffer.get(0));
		// } // if
		return compressed;
	} // method

	public static void listTextureCompressionFormats() {
		GL11.glGetInteger(ARBTextureCompression.GL_COMPRESSED_TEXTURE_FORMATS_ARB, tmpIntBuffer);
		for (int i = 0; i < 16; i++) {
			int format = tmpIntBuffer.get(i);
			switch (format) {
			case 0x83f0: System.out.println("GL_COMPRESSED_RGB_S3TC_DXT1_EXT (" + 0x83f0 + ")"); break;
			case 0x83f1: System.out.println("GL_COMPRESSED_RGBA_S3TC_DXT1_EXT (" + 0x83f1 + ")"); break;
			case 0x83f2: System.out.println("GL_COMPRESSED_RGBA_S3TC_DXT3_EXT (" + 0x83f2 + ")"); break;
			case 0x83f3: System.out.println("GL_COMPRESSED_RGBA_S3TC_DXT5_EXT (" + 0x83f3 + ")"); break;
			} // switch
		} // for
	} // method

	/**
	 * <P>Enables or disables anisotropic filtering on the font texture.
	 * 
	 * <P>Valid values are between 1.0 and the implementation specific constant GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT (both inclusive).
	 * 
	 * <P>A value of 1.0 effectively disables anisotropic filtering.
	 *
	 * @param value
	 */
	public static void setAnisotropicFiltering(int name, float value) {
		if (!GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			throw new UnsupportedOperationException("Anisotropic Texture Filtering is not available (OpenGL doesn't report the extension EXT_texture_filter_anisotropic)!");
		} // if
//		if (MAX_TEXTURE_MAX_ANISOTROPY_EXT == 0) {
//		GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, tmpFloatBuffer);
//		MAX_TEXTURE_MAX_ANISOTROPY_EXT = tmpFloatBuffer.get(0);
//		} // if supported maximum anisotropic is unknown, query it 
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, value);
	} // method

} // class
