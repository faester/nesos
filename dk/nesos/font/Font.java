package dk.nesos.font;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.glu.*;
import org.lwjgl.util.vector.*;

import dk.nesos.camera.*;

import java.nio.*;

/**
 * Font is an object that creates text with the font specified in the constructor.
 *  
 * @author ndhb
 */
public final class Font {

	private static final int MAX_STRING_LENGTH = 256; // maximum number of characters in a string

	private int characterIndex; // start of character indici
	private int listBaseName; // name of first valid display list
	private int textureName; // name of the texture generated
	private IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer(MAX_STRING_LENGTH); // for temporary display list names
	private Vector4f tmpVector4f = new Vector4f(); // for temporary texture coordinates
	private BitmapFont font; // the font used

	public Font(BitmapFont font) {
		this.font = font;
		textureName = createFontTexture();
		listBaseName = createDisplayLists();
		characterIndex = listBaseName - font.getBaseCharacter();
	} // constructor

	public int getCharacterIndex() {
		return characterIndex;
	} // method

	public BitmapFont getFont() {
		return font;
	} // method

	/**
	 * Releases previously allocated resources (ie. display lists and texture object).
	 */
	public void cleanup() {
		int requiredLists = font.getLastCharacter() - font.getBaseCharacter();
		// System.err.println("GLText.cleanup: glDeleteLists(" + listBaseName + ", " + requiredLists + ");"); // DEBUG
		GL11.glDeleteLists(listBaseName, requiredLists); // delete display lists
		// System.err.println("GLText.cleanup: glDeleteTextures(" + textureName+ ");"); // DEBUG
		tmpIntBuffer.put(0, textureName); // prepare delete texture
		GL11.glDeleteTextures(tmpIntBuffer); // delete texture
	} // method

	/**
	 * Draws the specified string at the given coordinates in orthographic projection (2D). 
	 * <P>
	 * Transformations of the coordinate system are not preserved through this method. That is, the
	 * identity matrix is loaded, before drawing the text.
	 * <P>
	 * <B>Note: The coordinates are given in OpenGL fashion, ie. (0,0) = lower-left corner of the screen</B>
	 * 
	 * @param string
	 * @param x coordinate
	 * @param y coordinate
	 */
	public void drawText2D(String string, int x, int y) {
		Camera.beginOrthographicProjection();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glTranslatef(x, y, 0); // translate to position
		drawText(string); // perform the actual rendering
		GL11.glPopAttrib();
		Camera.endOrthographicProjection();
	} // method

	/**
	 * Draws the specified string in the 3D coordinate system.
	 * <P>
	 * Transformations of the coordinatesystem are preserved through this method. This makes it possible
	 * to scale, rotate and translate the text.
	 *
	 * @param string
	 * @param x
	 * @param y
	 * @param z
	 */
	public void drawText3D(String string) {    
		drawText(string); // perform the actual rendering
	} // method

	private void drawText(String string) {
		int stringLength = string.length();
		if (stringLength > MAX_STRING_LENGTH) {
			throw new UnsupportedOperationException("String too long (" + stringLength + " characters in string, but only " + MAX_STRING_LENGTH + " available). Please shorten string OR increase GLText.MAX_STRING_LENGTH)!");
		} // if string too long
		tmpIntBuffer.limit(stringLength);
		for (int c = 0; c < stringLength; c++) {
			tmpIntBuffer.put(c, characterIndex + string.charAt(c)); // create buffer with names to execute
		} // for all characters
		beginText(); // change states
		GL11.glCallLists(tmpIntBuffer); // execute all these display lists
		endText(); // restore states
	} // method

	/**
	 * Draws the font as one textured quad on the x-axis of the current coordinate system. This makes examining the font easier.
	 *
	 * @param width of the quad being drawn
	 * @param height of the quad being drawn
	 */
	public void drawTexture(int width, int height) {
		beginText();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glNormal3f(0, 0, 1);
		GL11.glTexCoord2f(0, 1); GL11.glVertex3f(0, 0, 0);
		GL11.glTexCoord2f(1, 1); GL11.glVertex3f(width, 0, 0);
		GL11.glTexCoord2f(1, 0); GL11.glVertex3f(width, height, 0);
		GL11.glTexCoord2f(0, 0); GL11.glVertex3f(0, height, 0);
		GL11.glEnd();
		endText();
	} // method

	/**
	 * Provided for user to manipulate texture parameters.
	 *
	 * @return the name of the texture being used.
	 */
	public int getTextureName() {
		return textureName;
	} // method  

	/**
	 * Creates a display list for each character in the font.
	 * <P>
	 * The spacing between each character is determined by the width for each character in the font.
	 *
	 * @return the base list name of the sequence of display lists created.
	 */
	private int createDisplayLists() {
		int baseCharacter = font.getBaseCharacter();
		int lastCharacter = font.getLastCharacter();
		int cellWidth = font.getCellWidth();
		int cellHeight = font.getCellHeight();
		byte[] characterWidths = font.getCharacterWidths();
		int requiredLists = lastCharacter - baseCharacter;
		int baseList = GL11.glGenLists(requiredLists);
		if (baseList == 0) {
			throw new OpenGLException("OpenGL was unable to reserve the required " + requiredLists + " display lists!");
		} // if OpenGL failure
		for (int list = baseList, index = baseCharacter, lastList = baseList + requiredLists; list < lastList; list++, index++) {
			Vector4f textureCoordinates = getTextureCoordinates(index); // retrieve texture coordinates
			// System.err.println("(list=" + list + ", lastindex=" + lastList + ") | index=" + index + " | " +" (x, y, z, w) = (" + textureCoordinates.x + ", " + textureCoordinates.y + ", " + textureCoordinates.z + ", " + textureCoordinates.w); // DEBUG
			GL11.glNewList(list, GL11.GL_COMPILE);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(textureCoordinates.x, textureCoordinates.w); GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(textureCoordinates.z, textureCoordinates.w); GL11.glVertex2f(cellWidth, 0);
			GL11.glTexCoord2f(textureCoordinates.z, textureCoordinates.y); GL11.glVertex2f(cellWidth, cellHeight);
			GL11.glTexCoord2f(textureCoordinates.x, textureCoordinates.y); GL11.glVertex2f(0, cellHeight);
			GL11.glEnd();
			GL11.glTranslatef(characterWidths[index], 0, 0); // advance to the right (characters left to right)
			GL11.glEndList();
		} // for all lists
		return baseList;
	} // method

	private int createFontTexture() {
		GL11.glGenTextures(tmpIntBuffer);
		int name = tmpIntBuffer.get(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_PRIORITY, 1.0f); // please don't trash this one
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 2.0f);
		} else {
			// throw new UnsupportedOperationException("Anisotropic Texture Filtering is not available (OpenGL doesn't report the extension EXT_texture_filter_anisotropic)!");      
		} // if anisotripic supported
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		int error = GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, font.getGlInternalFormat(), font.getWidth(), font.getHeight(), font.getGlFormat(), GL11.GL_UNSIGNED_BYTE, font.getImageData());
		if (error != 0) {
			System.err.println("Warning: GLU.gluBuild2DMipmaps error " + error + " (resorting to texture without mipmap)!");
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, font.getGlInternalFormat(), font.getWidth(), font.getHeight(), 0, font.getGlFormat(), GL11.GL_UNSIGNED_BYTE, font.getImageData());
		} // if
		return name;
	} // method

	/**
	 * Calculates the texture coordinates for the specified character in the font.
	 *
	 * @param character
	 * @return a Vector4f with the texture coordinates
	 */
	private Vector4f getTextureCoordinates(int character) {
		int baseCharacter = font.getBaseCharacter();
		int charactersPerRow = font.getCharactersPerRow();
		int row = (character - baseCharacter) / charactersPerRow;
		int col = (character - baseCharacter) - row * charactersPerRow;
		float columnFactor = font.getColumnFactor();
		float rowFactor = font.getRowFactor();
		tmpVector4f.x = col * columnFactor; // texture coordinate s1
		tmpVector4f.y = row * font.getRowFactor(); // texture coordinate t1
		tmpVector4f.z = tmpVector4f.x + columnFactor; // texture coordinate s2
		tmpVector4f.w = tmpVector4f.y + rowFactor; // texture coordinate t2
		// System.err.println(" (x, y, z, w) = (" + tmpVector4f.x + ", " + tmpVector4f.y + ", " + tmpVector4f.z + ", " + tmpVector4f.w + ")"); // DEBUG
		return tmpVector4f;
	} // method

	/**
	 * Should be called prior to rendering the characters in the text.
	 * <P>
	 * The method saves current OpenGL states and enables those required for rendering. 
	 */
	public void beginText() {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_TRANSFORM_BIT); // store changed attributes
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // blending
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // those pixels with alpha greater than 0.1
		GL11.glDisable(GL11.GL_LIGHTING); // turn off lighting while drawing text
		GL11.glEnable(GL11.GL_BLEND); // enable blending
		GL11.glEnable(GL11.GL_ALPHA_TEST); // filter on alpha func
		GL11.glEnable(GL11.GL_TEXTURE_2D); // enable texturing
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);
	} // method

	/**
	 * Should be called when done rendering all characters in the text. The method restores previously saved OpenGL states.
	 */
	public void endText() {
		GL11.glPopAttrib(); // restore attributes
	} // method

} // class
