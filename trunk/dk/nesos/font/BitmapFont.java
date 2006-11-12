package dk.nesos.font;

import java.nio.*;

/**
 * Interface for bitmap fonts.
 * <P>
 * <B>Warning: Very likely to change in the future when unicode support is added.</B>
 *
 * @author ndhb
 */
public interface BitmapFont {

	/**
	 * @return the first ascii character in the font.
	 */
	int getBaseCharacter();

	/**
	 * @return number of bits per pixel (a.k.a. colour depth) of the image.
	 */
	byte getBitsPerPixel();

	/**
	 * @return the height in pixels of characters.
	 */
	int getCellHeight();

	/**
	 * @return the width in pixels of characters.
	 */
	int getCellWidth();

	/**
	 * @return the number of characters per row in the image.
	 */
	int getCharactersPerRow();

	/**
	 * @return horisontal spacing for each characters in the font.
	 */
	byte[] getCharacterWidths();

	/**
	 * @return the column factor (number of columns in image).
	 */
	float getColumnFactor();

	/**
	 * @return the image format for OpenGL.
	 */
	int getGLFormat();

	/**
	 * @return the internal format for OpenGL when storing the image.
	 */
	int getGLInternalFormat();

	/**
	 * @return height of the image in pixels.
	 */
	int getHeight();

	/**
	 * @return the image data from the bff file.
	 */
	ByteBuffer getImageData();

	/**
	 * @return the last ascii character in the font
	 */
	int getLastCharacter();

	/**
	 * @return the row factor (number of rows in image).
	 */
	float getRowFactor();

	/**
	 * @return width of the image in pixels.
	 */
	int getWidth();
	
	/**
	 * @return vertical spacing between lines.
	 */
	float getLineHeight();

} // interface
