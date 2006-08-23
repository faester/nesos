package dk.nesos.view.text;

import java.nio.*;

/**
 * TODO: Consider changing to interface...
 *
 * @author NDHB
 *
 */
abstract class BitmapFont {
    
    int width;
    int height;
    byte bitsPerPixel;
    int glFormat;
    int glInternalFormat;
    int cellWidth;
    int cellHeight;
    int baseCharacter;
    int lastCharacter;
    int charactersPerRow;
    float rowFactor;
    float columnFactor;
    byte[] characterWidths;
    ByteBuffer imageData;
    
    @Override
    public String toString() {
        String LINE_SEPARATOR = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder(); // TODO: Consider making this a field and clear it every time instead of creating new objects
        s.append(super.toString() + LINE_SEPARATOR);
        s.append("width = " + width + LINE_SEPARATOR);
        s.append("height = " + height + LINE_SEPARATOR);
        s.append("bitsPerPixel = " + bitsPerPixel + LINE_SEPARATOR);
        s.append("glFormat = " + glFormat + LINE_SEPARATOR);
        s.append("glInternalFormat = " + glInternalFormat + LINE_SEPARATOR);
        s.append("cellWidth = " + cellWidth + LINE_SEPARATOR);
        s.append("cellHeight = " + cellHeight + LINE_SEPARATOR);
        s.append("baseCharacter = " + baseCharacter + LINE_SEPARATOR);
        s.append("lastCharacter = " + lastCharacter + LINE_SEPARATOR);
        s.append("charactersPerRow = " + charactersPerRow + LINE_SEPARATOR);
        s.append("rowFactor = " + rowFactor + LINE_SEPARATOR);
        s.append("columnFactor = " + columnFactor + LINE_SEPARATOR);
        s.append("characterWidths = { ");
        for (int i = 0; i < characterWidths.length; i++) {
            s.append(characterWidths[i] + (i < characterWidths.length - 1 ? ", " : ""));
        } // for
        s.append(" }");
        return s.toString();
    } // method
    
} // class
