package dk.nesos.font;

import java.nio.*;

/**
 * TODO: description
 *
 * @author ndhb
 */
public interface BitmapFont {

	int getBaseCharacter();

	byte getBitsPerPixel();

	int getCellHeight();

	int getCellWidth();

	int getCharactersPerRow();

	byte[] getCharacterWidths();

	float getColumnFactor();

	int getGlFormat();

	int getGlInternalFormat();

	int getHeight();

	ByteBuffer getImageData();

	int getLastCharacter();

	float getRowFactor();

	int getWidth();

} // interface
