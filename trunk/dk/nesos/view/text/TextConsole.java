package dk.nesos.view.text;

import java.util.*;

import org.lwjgl.opengl.*;

import dk.nesos.view.camera.*;

/**
 * TODO: description
 *
 * @author ndhb
 */
public final class TextConsole {

  private static final int DEFAULT_CAPACITY = 48;
  
  private List<Text> list = new ArrayList<Text>(DEFAULT_CAPACITY);
  private int positionX;
  private int positionY;
  private int lines;
  private Font font;

  public TextConsole(Font font, int positionX, int positionY, int lines) {
    this.font = font;
    this.positionX = positionX;
    this.positionY = positionY;
    this.lines = lines;
  } // constructor

  public void initGL() {
    // empty
  } // method
  
  public void renderGL() {
    int lineHeight = font.getFont().getCellHeight();
    Camera.beginOrthographicProjection();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDepthMask(false);
    GL11.glTranslatef(positionX, positionY, 0); // translate to position
    Iterator<Text> iterator = list.iterator();
    while (iterator.hasNext()) {
      Text text = iterator.next();
      GL11.glPushMatrix();
      text.renderGL(); 
      GL11.glPopMatrix();
      GL11.glTranslatef(0, -lineHeight, 0); // translate offsets for next line
    } // while
    GL11.glPopAttrib();
    Camera.endOrthographicProjection();
  } // method  
  
  public void cleanupGL() {
    Iterator<Text> iterator = list.iterator();
    while (iterator.hasNext()) {
      Text text = iterator.next();
      text.cleanupGL();
    } // while
  } // method
  
  public void println(String string) {
    Text text = new Text(font, string);
    text.initGL();
    list.add(text);
  } // method

  /**
   * Clears all entries.
   */
  public void clear() {
    list.clear();
  } // method

  /**
   * Clears only the hidden entries.
   */
  public void clearHidden() {
    // TODO
  } // method

  public int getPositionX() {
    return positionX;
  } // method

  public int getPositionY() {
    return positionY;
  } // method

  public int getLines() {
    return lines;
  } // method
  
  public void setPosition(int positionX, int positionY) {
    this.positionX = positionX;
    this.positionY = positionY;
  } // method
  
  public void setPositionX(int positionX) {
    this.positionX = positionX;
  } // method

  public void setPositionY(int positionY) {
    this.positionY = positionY;
  } // method
  
  public void setLines(int lines) {
    this.lines = lines;
  } // method
  
} // class
