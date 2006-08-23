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
 *
 */
public final class Console {

    private static final int INITIAL_CAPACITY = 48;
    private static final Dimension DEFAULT_POSITION = new Dimension(0, 0);
    private static final Dimension DEFAULT_SIZE = new Dimension(10, 10);
    
    private Dimension position;
    private Dimension size;
    private List list = Collections.synchronizedList(new ArrayList(INITIAL_CAPACITY));
    private Text text;
    
    public Console(Text glText) {
        this(glText, DEFAULT_POSITION, DEFAULT_SIZE);
    } // convenince constructor
    
    public Console(Text text, Dimension position, Dimension size) {
        this.text = text;
        this.position = position;
        this.size = size;
    } // constructor
    
    public void print(String s) {
        System.err.print(s);
    } // method
    
    public void println(String s) {
        System.err.println(s);
    } // method
    
    /**
     * <P>Clears all text in the buffer.
     *
     */
    public void clear() {
        list.clear();
    } // method
    
    /**
     * <P>Clears all text in the buffer thats not visible.
     *
     */
    public void clearHidden() {
        // TODO
    } // method
    
    /**
     * @return Returns the position.
     */
    public Dimension getPosition() {
        return position;
    }
    
    /**
     * @param position The position to set.
     */
    public void setPosition(Dimension position) {
        this.position = position;
    }
    
    /**
     * @return Returns the size.
     */
    public Dimension getSize() {
        return size;
    }
    
    /**
     * @param size The size to set.
     */
    public void setSize(Dimension size) {
        this.size = size;
    }
    
} // class
