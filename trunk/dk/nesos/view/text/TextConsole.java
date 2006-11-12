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
	
	private List<Text> entries;
	private int positionX;
	private int positionY;
	private int lines;
	private Font font;

	public TextConsole(Font font, int positionX, int positionY, int lines) {
		this.font = font;
		this.positionX = positionX;
		this.positionY = positionY;
		this.lines = lines;
		entries = new ArrayList<Text>(lines);
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
		for (int i = 0; i < entries.size(); i++) {
			GL11.glPushMatrix();
			Text text = entries.get(i);
			text.renderGL();
			GL11.glPopMatrix();
			GL11.glTranslatef(0, -lineHeight, 0); // translate offsets for next line
		} // while
		GL11.glPopAttrib();
		Camera.endOrthographicProjection();
	} // method  

	public void cleanupGL() {
		for (int i = 0; i < entries.size(); i++) {
			Text text = entries.get(i);
			text.cleanupGL();
		} // while
	} // method

	public void println(String string) {
		if (entries.size() >= lines) {
			Text removedText = entries.get(0);
			removedText.cleanupGL();
			entries.remove(0);
		} // if first entry due for removal
		Text text = new Text(font, string);
		text.initGL();
		entries.add(text);
	} // method

	public void clear() {
		cleanupGL();
		entries = new ArrayList<Text>(lines);    
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
