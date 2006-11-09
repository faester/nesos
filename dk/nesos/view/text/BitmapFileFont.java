package dk.nesos.view.text;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

/**
 * <P>Reads the .bff file format. This file format is used by
 * <P>Codehead's Bitmap Font Generator (http://www.codehead.co.uk/cbfg)
 * <P>(the fileformat specification is described in the help file)
 *
 * @author NDHB
 *
 */
public final class BitmapFileFont extends BitmapFont {

  private static final int BFF_VERSION2_FORMAT = 0xF2BF;

  private static final int BFF_VERSION_OFFSET = 0;
  private static final int FONT_WIDTH_OFFSET = 2;
  private static final int FONT_HEIGHT_OFFSET = 6;
  private static final int CELL_WIDTH_OFFSET = 10;
  private static final int CELL_HEIGHT_OFFSET = 14;
  private static final int BITS_PER_PIXEL_OFFSET = 18;
  private static final int BASE_CHARACTER_OFFSET = 19;
  private static final int CHARACTER_WIDTH_OFFSET = 20;
  private static final int NUM_CHARACTER_WIDTH = 256;
  private static final int IMAGE_DATA_OFFSET = 276; // same as CHARACTER_WIDTH_OFFSET + NUM_CHARACTER_WIDTH

  public BitmapFileFont(String filename) {
    File file = new File(filename);
    ByteBuffer buffer = BufferUtils.createByteBuffer((int)file.length());
    FileInputStream fis;
    try {
      fis = new FileInputStream(file);
      FileChannel fc = fis.getChannel();
      fc.read(buffer); // read from disk into buffer
      parse(buffer); // parse data
      fis.close(); // closing the stream also closes the channel
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File \"" + filename + "\" not found!");
    } catch (IOException e) {
      throw new RuntimeException("IOException on \"" + filename + "\"!");
    } // try
  } // constructor

  private void parse(ByteBuffer buffer) {
    int version = buffer.getChar(BFF_VERSION_OFFSET);
    if (version == BFF_VERSION2_FORMAT) {
      width = buffer.getInt(FONT_WIDTH_OFFSET);
      height = buffer.getInt(FONT_HEIGHT_OFFSET); 
      cellWidth = buffer.getInt(CELL_WIDTH_OFFSET);
      cellHeight = buffer.getInt(CELL_HEIGHT_OFFSET);
      charactersPerRow = width / cellWidth;
      bitsPerPixel = buffer.get(BITS_PER_PIXEL_OFFSET);
      baseCharacter = buffer.get(BASE_CHARACTER_OFFSET) & 0xFF; // unsigned byte to int conversion
      lastCharacter = 256; // always 256 in BFF version 2
      characterWidths = new byte[NUM_CHARACTER_WIDTH]; // initialize array of character widths
      rowFactor = (float)cellHeight / (float)height;
      columnFactor = (float)cellWidth / (float)width;
      buffer.position(CHARACTER_WIDTH_OFFSET);
      buffer.get(characterWidths, 0, NUM_CHARACTER_WIDTH); // read into array of character widths
      switch (bitsPerPixel) { // choose an appropriate OpenGL format
        case 8  : glFormat = GL11.GL_ALPHA; glInternalFormat = ARBTextureCompression.  GL_COMPRESSED_ALPHA_ARB; break;
        case 24 : glFormat = GL11.GL_RGB; glInternalFormat = ARBTextureCompression.GL_COMPRESSED_RGB_ARB; break;
        case 32 : glFormat = GL11.GL_RGBA; glInternalFormat = ARBTextureCompression.GL_COMPRESSED_RGBA_ARB; break;
        // case 32  : glFormat = GL11.GL_RGBA; glInternalFormat = EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT; break;
      } // switch
      buffer.position(IMAGE_DATA_OFFSET); // position where image data begins
      imageData = buffer.slice(); // the rest is image data
    } else {
      throw new UnsupportedOperationException("Unsupported format version: 0x" + Integer.toHexString(version).toUpperCase());
    } // if supported version
  } // method
  
  @Override
  public String toString() {
      String LINE_SEPARATOR = System.getProperty("line.separator");
      StringBuilder s = new StringBuilder();
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
