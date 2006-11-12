package dk.nesos.font;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import dk.nesos.camera.*;

/**
 * A text string that doesn't change and is displayed many times (a text label).
 * 
 * @author ndhb
 */
public final class Text {

  private static final int MAX_STRING_LENGTH = 64; // maximum number of characters in a string

  private IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer(MAX_STRING_LENGTH); // for temporary display list names
  private Font font;
  private String string;
  private int name;

  public Text(Font font, String string) {
    this.font = font;
    this.string = string;
    this.name = GL11.glGenLists(1);
  } // method

  public void initGL() {
    int stringLength = string.length();
    if (stringLength > MAX_STRING_LENGTH) {
      throw new UnsupportedOperationException("String too long (" + stringLength + " characters in string, but only " + MAX_STRING_LENGTH + " available). Please shorten string OR increase Text.MAX_STRING_LENGTH)!");
    } // if string too long
    tmpIntBuffer.limit(stringLength);
    int characterIndex = font.getCharacterIndex();
    for (int c = 0; c < stringLength; c++) {
      tmpIntBuffer.put(c, characterIndex + string.charAt(c)); // create buffer with names to execute
    } // for all characters
    GL11.glNewList(name, GL11.GL_COMPILE);
    font.beginText();
    GL11.glCallLists(tmpIntBuffer); // execute all these display lists
    font.endText();
    GL11.glEndList();
  } // method

  public void draw2D(int x, int y) {
    Camera.beginOrthographicProjection();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDepthMask(false);
    GL11.glTranslatef(x, y, 0); // translate to position
    renderGL();
    GL11.glPopAttrib();
    Camera.endOrthographicProjection();
  } // method

  public void draw3D() {
    renderGL();
  } // method

  public void renderGL() {
    GL11.glCallList(name); // perform the actual rendering
  } // method

  public void cleanupGL() {
    GL11.glDeleteLists(name, 1);
  } // method

  public String getString() {
    return string;
  } // method

  public int getName() {
    return name;
  } // method

  public Font getFont() {
    return font;
  } // method

  @Override
  public String toString() {
    return string;
  } // mehod
  
} // class