package dk.nesos.view.text;

import java.util.*;

import org.lwjgl.util.*;

/**
 * <P>Skal indeholde en buffer som kan scroll
 *      Skal have "print" og "println"
 *      Skal have en "clear" / "reset" til buffer
 *
 *
 * @author NDHB
 */
public final class Console {

    private static final int DEFAULT_CAPACITY = 48;
    private static final float DEFAULT_LINE_SPACING = 1f;
    private static final Dimension DEFAULT_POSITION = new Dimension(200, 200);
    private static final Dimension DEFAULT_SIZE = new Dimension(10, 10);
    
    private float lineSpacing = DEFAULT_LINE_SPACING;
    private Dimension position = DEFAULT_POSITION;
    private Dimension size = DEFAULT_SIZE;
    private List<String> list = Collections.synchronizedList(new ArrayList<String>(DEFAULT_CAPACITY));
    private Text text;
    
    public Console(Text glText) {
        this(glText, DEFAULT_POSITION, DEFAULT_SIZE);
    } // convenince constructor
    
    public Console(Text text, Dimension position, Dimension size) {
        this.text = text;
        this.position = position;
        this.size = size;
    } // constructor
    
    public void renderGL() {
      int x = position.getWidth();
      int y = position.getHeight();
      text.drawStrings2D(list.toArray(new String[list.size()]), x, y, 0, lineSpacing * text.getFont().cellHeight);
    } // method
    
    public void print(String s) {
        System.err.print(s);
    } // method
    
    public void println(String s) {
      list.add(s); // TODO: line separator
    } // method
    
    /**
     * <P>Clears all text in the buffer.
     */
    public void clear() {
        list.clear();
    } // method
    
    /**
     * <P>Clears all text in the buffer thats not visible.
     */
    public void clearHidden() {
        // TODO
    } // method
    
    /**
     * @return Returns the position.
     */
    public Dimension getPosition() {
        return position;
    } // method
    
    /**
     * @param position The position to set.
     */
    public void setPosition(Dimension position) {
        this.position = position;
    } // method
    
    /**
     * @return Returns the size.
     */
    public Dimension getSize() {
        return size;
    } // method
    
    /**
     * @param size The size to set.
     */
    public void setSize(Dimension size) {
        this.size = size;
    } // method
    
} // class
