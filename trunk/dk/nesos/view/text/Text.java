package dk.nesos.view.text;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.glu.*;
import org.lwjgl.util.vector.*;

import java.nio.*;

/**
 * Text is an object that writes text with the font specified in the constructor.
 * <P>
 * Note: http://www.gamedev.net/community/forums/topic.asp?topic_id=421835
 * <P>
 * Issues:
 * <OL>
 * <LI>Move some methods to TextureManager</LI>
 * <LI>Lighting interesting?</LI>
 * <LI>Blending interesting?</LI>
 * <LI>AlphaTest interesting?</LI>
 * <LI>Scaling down, seems to require anisotropic filtering (or max mipmap level 0) - else blur!<LI>
 * </OL>
 * 
 * @author NDHB
 */
public final class Text {

  private static final int MAX_STRING_LENGTH = 256; // maximum number of characters in a string
  private static float MAX_TEXTURE_MAX_ANISOTROPY_EXT;

  private boolean monoSpaced; // whether the distance between characters is constant
  private boolean depthTesting; // whether to perform depth testing
  private boolean lighting; // whether to enable light when rendering
  private int listBaseName; // name of first valid display list
  private int textureName; // name of the texture generated
  private FloatBuffer tmpFloatBuffer = BufferUtils.createFloatBuffer(16); // for temporary data
  private IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer(MAX_STRING_LENGTH); // for temporary data
  private Vector4f tmpVector4f = new Vector4f(); // for temporary texture coordiantes
  private BitmapFont font;

  public Text(BitmapFont font) {
    this(font, false);
  } // constructor

  public Text(BitmapFont font, boolean monoSpaced) {
    this.font = font;
    this.monoSpaced = monoSpaced;
    textureName = createFontTexture();
    listBaseName = createDisplayLists();
  } // constructor
  
  public BitmapFont getFont() {
    return font;
  } // method
  
  /**
   * <P>Releases previously allocated resources (ie. display lists and texture object).
   */
  public void cleanup() {
    int requiredLists = font.lastCharacter - font.baseCharacter;
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
  public void drawString2D(String string, int x, int y) {
    String[] strings = { string };
    drawStrings2D(strings, x, y, 0, 0); // performance penalty for this hack?
  } // method

  /**
   * TODO: description
   * 
   * @param strings
   * @param x
   * @param y
   * @param xDelta
   * @param yDelta
   */
  public void drawStrings2D(String[] strings, float x, float y, float  xDelta, float yDelta) {
    beginTextState();
    beginOrthographicProjection(Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight()); // store current projection matrix and transform to orthographic
    GL11.glMatrixMode(GL11.GL_MODELVIEW); // switch to modelview stack
    GL11.glPushMatrix(); // store current matrix
    GL11.glLoadIdentity(); // need identity matrix for proper translation in orthographic perspective
    // GL11.glScalef(0.5f, 0.5f, 1f); // scaling
    GL11.glTranslatef(x, y, 0); // translate to origin    
    for (int s = 0; s < strings.length; s++) {
      GL11.glPushMatrix(); // store current matrix
      drawString(strings[s]); // perform the actual rendering
      GL11.glPopMatrix(); // restore modelview matrix
      GL11.glTranslatef(xDelta, -yDelta, 0); // translate offsets for next line
    } // for
    GL11.glPopMatrix(); // restore modelview matrix
    endOrthographicProjection(); // restore previous projection matrix
    endTextState();
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
  public void drawString3D(String string, float x, float y, float z) {
    beginTextState();
    GL11.glMatrixMode(GL11.GL_MODELVIEW); // switch to modelview stack
    GL11.glPushMatrix(); // store current matrix
    GL11.glTranslatef(x, y, z); // translate in 3 dimensions
    drawString(string); // perform the actual rendering
    GL11.glPopMatrix(); // restore modelview matrix
    endTextState();
  } // method

  private void drawString(String string) {
    int stringLength = string.length();
    if (stringLength > MAX_STRING_LENGTH) {
      throw new UnsupportedOperationException("String too long (" + stringLength + " characters in string, but only " + MAX_STRING_LENGTH + " available). Please shorten string OR increase GLText.MAX_STRING_LENGTH)!");
    } // if string too long
    tmpIntBuffer.limit(stringLength);
    for (int c = 0, characterIndex = listBaseName - font.baseCharacter; c < stringLength; c++) {
      tmpIntBuffer.put(c, characterIndex + string.charAt(c)); // create buffer with names to execute
    } // for all characters
    GL11.glCallLists(tmpIntBuffer); // execute all these display lists
  } // method
  
  /**
   * <P>Draws the font as one textured quad on the x-axis of the current coordinate system. This makes examining the font easier.
   *
   * @param width of the quad being drawn
   * @param height of the quad being drawn
   */
  public void drawTexture(int width, int height) {
    beginTextState();
    GL11.glBegin(GL11.GL_QUADS);
    GL11.glNormal3f(0, 0, 1);
    GL11.glTexCoord2f(0, 1); GL11.glVertex3f(0, 0, 0);
    GL11.glTexCoord2f(1, 1); GL11.glVertex3f(width, 0, 0);
    GL11.glTexCoord2f(1, 0); GL11.glVertex3f(width, height, 0);
    GL11.glTexCoord2f(0, 0); GL11.glVertex3f(0, height, 0);
    GL11.glEnd();
    endTextState();
  } // method

  /**
   * <P>Provided for user to manipulate texture parameters.
   *
   * @return the name of the texture being used.
   */
  public int getTextureName() {
    return textureName;
  } // method  

  /**
   * <P>Whether or not depth test is currently enabled.
   * 
   * @return boolean 
   */
  public boolean hasDepthTesting() {
    return depthTesting;
  } // method

  /**
   * <P>Whether or not lighting is currently enabled.
   * 
   * @return boolean
   */
  public boolean hasLighting() {
    return lighting;
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
  public void setAnisotropicFiltering(float value) {
    if (!GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
      throw new UnsupportedOperationException("Anisotropic Texture Filtering is not available (OpenGL doesn't report the extension EXT_texture_filter_anisotropic)!");
    } // if
    if (MAX_TEXTURE_MAX_ANISOTROPY_EXT == 0) {
      GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, tmpFloatBuffer);
      MAX_TEXTURE_MAX_ANISOTROPY_EXT = tmpFloatBuffer.get(0);
    } // if supported maximum anisotropic is unknown, query it 
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);
    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, value);
  } // method

  /**
   * <P>Enables or disables the depth test when rendering. When enabled the text can be occluded by other objects in 3D space. When disabled the text always appears on top of other objects.
   * 
   * @param depthTesting
   */
  public void setDepthTesting(boolean depthTesting) {
    this.depthTesting = depthTesting;
  } // method

  /**
   * <P>Enables or disables lighting calculations on the text.
   * 
   * @param lighting
   */
  public void setLighting(boolean lighting) {
    this.lighting = lighting;
  } // method

  /**
   * <P>Creates a display list for each character in the font.
   * 
   * <P>The spacing between each character is determined by the width for each character in the font.
   *
   * @return the base list name of the sequence of display lists created.
   */
  private int createDisplayLists() {
    int requiredLists = font.lastCharacter - font.baseCharacter;
    int baseList = GL11.glGenLists(requiredLists);
    if (baseList == 0) {
      throw new OpenGLException("OpenGL was unable to reserve the required " + requiredLists + " display lists!");
    } // if OpenGL failure
    for (int list = baseList, index = font.baseCharacter, lastList = baseList + requiredLists; list < lastList; list++, index++) {
      Vector4f textureCoordinates = getTextureCoordinates(index); // retrieve texture coordinates
      // System.err.println("(list=" + list + ", lastindex=" + lastList + ") | index=" + index + " | " +" (x, y, z, w) = (" + textureCoordinates.x + ", " + textureCoordinates.y + ", " + textureCoordinates.z + ", " + textureCoordinates.w); // DEBUG
      GL11.glNewList(list, GL11.GL_COMPILE);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(textureCoordinates.x, textureCoordinates.w); GL11.glVertex2f(0, 0);
      GL11.glTexCoord2f(textureCoordinates.z, textureCoordinates.w); GL11.glVertex2f(font.cellWidth, 0);
      GL11.glTexCoord2f(textureCoordinates.z, textureCoordinates.y); GL11.glVertex2f(font.cellWidth, font.cellHeight);
      GL11.glTexCoord2f(textureCoordinates.x, textureCoordinates.y); GL11.glVertex2f(0, font.cellHeight);
      GL11.glEnd();
      if (monoSpaced) {
        GL11.glTranslatef(font.cellWidth, 0, 0); // advance to the right (characters left to right)
      } else {
        GL11.glTranslatef(font.characterWidths[index], 0, 0); // advance to the right (characters left to right)
      } // if monospaced
      GL11.glEndList();
    } // for all lists
    return baseList;
  } // method

  private int createFontTexture() {
    GL11.glGenTextures(tmpIntBuffer);
    int name = tmpIntBuffer.get(0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_PRIORITY, 1.0f);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
    int error = GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, font.glInternalFormat, font.width, font.height, font.glFormat, GL11.GL_UNSIGNED_BYTE, font.imageData);
    if (error != 0) {
      System.err.println("Warning: GLU.gluBuild2DMipmaps error " + error + " (resorting to texture without mipmap)!");
      GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, font.glInternalFormat, font.width, font.height, 0, font.glFormat, GL11.GL_UNSIGNED_BYTE, font.imageData);
    } // if
    return name;
  } // method

  /**
   * <P>Should be called when done rendering all characters in the text. The method restores previously saved OpenGL states.
   */
  private void endTextState() {
    GL11.glPopAttrib(); // restore attributes
  } // method

  /**
   * <P>Calculates the texture coordinates for the specified character in the font.
   *
   * @param character
   * @return a Vector4f with the texture coordinates
   */
  private Vector4f getTextureCoordinates(int character) {
    int row = (character - font.baseCharacter) / font.charactersPerRow;
    int col = (character - font.baseCharacter) - row * font.charactersPerRow;
    tmpVector4f.x = col * font.columnFactor; // texture coordinate s1
    tmpVector4f.y = row * font.rowFactor; // texture coordinate t1
    tmpVector4f.z = tmpVector4f.x + font.columnFactor; // texture coordinate s2
    tmpVector4f.w = tmpVector4f.y + font.rowFactor; // texture coordinate t2
    // System.err.println(" (x, y, z, w) = (" + tmpVector4f.x + ", " + tmpVector4f.y + ", " + tmpVector4f.z + ", " + tmpVector4f.w + ")"); // DEBUG
    return tmpVector4f;
  } // method

  /**
   * <P>
   * Should be called prior to rendering the characters in the text.
   * <P>
   * The method saves current OpenGL states and enables those required for rendering. 
   */
  private void beginTextState() {
    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS); // store attributes (FIXME: overkill to store all)
    if (depthTesting) {
      GL11.glEnable(GL11.GL_DEPTH_TEST); // enable depth test (text can be occluded)
      GL11.glDepthMask(true); // enable depth masking
    } else {
      GL11.glDisable(GL11.GL_DEPTH_TEST); // disable depth test (text always on top)
      GL11.glDepthMask(false); // disable depth masking
    } // if
    if (lighting) {
      GL11.glEnable(GL11.GL_LIGHTING); // enable lighting (text is lit)
    } else {
      GL11.glDisable(GL11.GL_LIGHTING); // disable lighting (text never lit)
    } // if        
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // blending functions
    GL11.glEnable(GL11.GL_BLEND); // enable blending
    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // those pixels with alpha greater than 0.1
    GL11.glEnable(GL11.GL_ALPHA_TEST); // ... only write these
    GL11.glEnable(GL11.GL_TEXTURE_2D); // enable texturing
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);
  } // method

  /**
   * Prepares an orthographic projection matrix. Saves the current projection matrix on the stack.
   * <P>
   * <B>Remember to call <code>endOrtographicProjection</code> when done in orthographic mode</B>
   *
   * @param width of the projection screen
   * @param height of the projection screen
   */
  private void beginOrthographicProjection(int width, int height) {
    GL11.glMatrixMode(GL11.GL_PROJECTION); // switch to projection stack
    GL11.glPushMatrix(); // store projection matrix
    GL11.glLoadIdentity(); // reset matrix
    GLU.gluOrtho2D(0, width, 0, height); // set a 2D orthographic projection
  } // method
  
  /**
   * Restores the previous projection from the stack.
   */
  private void endOrthographicProjection() {
    GL11.glMatrixMode(GL11.GL_PROJECTION); // switch to projection stack
    GL11.glPopMatrix(); // restore perspective matrix
  } // method

} // class
