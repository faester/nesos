package dk.nesos.view.text;

import java.nio.*;

/**
 * TODO: Consider changing to interface...
 *
 * @author NDHB
 *
 */
public abstract class BitmapFont {
    
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
    
} // class
