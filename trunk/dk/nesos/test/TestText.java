package dk.nesos.test;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.glu.*;
import org.lwjgl.input.*;
import org.lwjgl.util.vector.*;

import java.nio.*;

import dk.nesos.util.*;
import dk.nesos.view.*;
import dk.nesos.view.camera.*;
import dk.nesos.view.text.*;

/**
 * For testing the view.text.* classes
 * <P>
 * Take a look at the input method - there are many filtering and sampling options to try out. 
 * 
 * @author ndhb, mhf
 *
 */
public class TestText {

  private static final String testString = "The quick onyx goblin jumps over the lazy dwarf";
  private static final float FPS_UPDATE_TIME = 250;
  private static boolean FULLSCREEN = false;

  private boolean key_f1 = false;
  private boolean key_f2 = false;
  private boolean key_f3 = false;
  private boolean key_f4 = false;
  private boolean key_f5 = false;
  private boolean key_f6 = false;
  private boolean key_f7 = false;
  private boolean key_f8 = false;
  private boolean key_f9 = false;
  private boolean key_f10 = false;
  private boolean key_f11 = false;
  private boolean key_f12 = false;
  private boolean key_return = false;
  private boolean glShadeModel = Configuration.hasSmooth();
  private boolean glPolygonMode = Configuration.getFill();
  private boolean glLighting = Configuration.hasLighting();
  private boolean done = false;
  private String windowTitle = "TestText";
  private DisplayMode displayMode;

  private int framesRendered;
  private float currentFPS;
  private float maxFPS = 0;
  private float minFPS = Short.MAX_VALUE;
  private long currentTime;
  private long timeToFPS;

  private Camera camera;

  private boolean lightPaused = true;
  private float lightAngle = 0;
  private float lightAngleDelta = 0.5f;
  private float lightDistance = 50;
  private FloatBuffer lightPos = BufferUtils.createFloatBuffer(4).put(0, lightDistance).put(1, 50).put(2, lightDistance).put(3, 1);
  Sphere lightSphere = new Sphere(); // create a "sun"
  
  private Sphere sphere = new Sphere();
  private int sphereList;
  private int mipMapMax;
  private int mipMapMaxLOD;
  private int mipMapMinLOD;
  private float textRotationAngle;
  private float textRotationAngleDelta = 0.5f;
  private float alphaReference = 0;

  private Font fontLucida, fontCourier;
  private TextConsole textConsole;
  private Text text;
  
  public static void main(String args[]) {
    TestText q = new TestText();
    q.run();
  } // main

  public void run() {
    try {
      init();
    } catch (Exception e) {
      Sys.alert(windowTitle, "An error occured and the game will exit.");
      cleanup();
      e.printStackTrace();
      System.exit(1);
    } // try catch

    currentTime = System.currentTimeMillis();
    timeToFPS = currentTime + (long)FPS_UPDATE_TIME;

    while (! done) {
      Display.update();
      if (Display.isCloseRequested()) {
        done = true;// check for close requests
      } else if (Display.isActive() && Display.isVisible()) {
        input();
        render();
      } else {
        // the window is not in the foreground, so we can allow other stuff to run and infrequently update
        try {
          Thread.sleep(200); // take a break CPU
        } catch (InterruptedException e) {
          // intentionally ignore
        } // try catch
        if (Display.isVisible() || Display.isDirty()) {
          render(); // only bother rendering if the window is visible or dirty
        } // if visible
      }  // if else
    } // while
    cleanup();
  } // method

  private void input() {
    if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) { // Exit if window is closed or Escape is pressed
      done = true;
    } // if closing

    // KEY_F1
    if (!key_f1 && Keyboard.isKeyDown(Keyboard.KEY_F1)) {
      key_f1 = true;
      FULLSCREEN = ! FULLSCREEN;
      try {
        Display.setFullscreen(FULLSCREEN);
      } catch (Exception e) {
        e.printStackTrace();
      } // try catch
    } else if (key_f1 && !Keyboard.isKeyDown(Keyboard.KEY_F1)) {
      key_f1 = false;
    } // if else

    // KEY_F2
    if (!key_f2 && Keyboard.isKeyDown(Keyboard.KEY_F2)) {
      key_f2 = true;
      glShadeModel = ! glShadeModel;
      if (glShadeModel) {
        GL11.glShadeModel(GL11.GL_SMOOTH);
      } else {
        GL11.glShadeModel(GL11.GL_FLAT);
      } // if else
        System.err.println("GL_SMOOTH = " + glShadeModel);
    } else if (key_f2 && !Keyboard.isKeyDown(Keyboard.KEY_F2)) {
      key_f2 = false;
    } // if else

    // KEY_F3
    if (!key_f3 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
      key_f3 = true;
      glPolygonMode = ! glPolygonMode;
      if (glPolygonMode) {
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
      } else {
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
      } // if else
        System.err.println("GL_POLYGON_MODE = " + glPolygonMode);
    } else if (key_f3 && !Keyboard.isKeyDown(Keyboard.KEY_F3)) {
      key_f3 = false;
    } // if else

    // KEY_F4
    if (!key_f4 && Keyboard.isKeyDown(Keyboard.KEY_F4)) {
      key_f4 = true;
      glLighting = ! glLighting;
      if (glLighting) {
        GL11.glEnable(GL11.GL_LIGHTING);
      } else {
        GL11.glDisable(GL11.GL_LIGHTING);
      } // if else
        System.err.println("GL_LIGHTING = " + glLighting);
    } else if (key_f4 && !Keyboard.isKeyDown(Keyboard.KEY_F4)) {
      key_f4 = false;
    } // if else

    // KEY_F5
    if (!key_f5 && Keyboard.isKeyDown(Keyboard.KEY_F5)) {
      key_f5 = true;
      // available
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontLucida.getTextureName());
      GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_DECAL);
      Debug.println("GL_TEXTURE_ENV_MODE = GL_DECAL");
    } else if (key_f5 && !Keyboard.isKeyDown(Keyboard.KEY_F5)) {
      key_f5 = false;
    } // if else        

    // KEY_F6
    if (!key_f6 && Keyboard.isKeyDown(Keyboard.KEY_F6)) {
      key_f6 = true;
      // available
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontLucida.getTextureName());
      GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
      Debug.println("GL_TEXTURE_ENV_MODE = GL_REPLACE");
    } else if (key_f6 && !Keyboard.isKeyDown(Keyboard.KEY_F6)) {
      key_f6 = false;
    } // if else

    // KEY_F7
    if (!key_f7 && Keyboard.isKeyDown(Keyboard.KEY_F7)) {
      key_f7 = true;
      // available
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontLucida.getTextureName());
      GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
      Debug.println("GL_TEXTURE_ENV_MODE = GL_MODULATE");
    } else if (key_f7 && !Keyboard.isKeyDown(Keyboard.KEY_F7)) {
      key_f7 = false;
    } // if else

    // KEY_F8
    if (!key_f8 && Keyboard.isKeyDown(Keyboard.KEY_F8)) {
      key_f8 = true;
      // available
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontLucida.getTextureName());
      GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
      Debug.println("GL_TEXTURE_ENV_MODE = GL_BLEND");
    } else if (key_f8 && !Keyboard.isKeyDown(Keyboard.KEY_F8)) {
      key_f8 = false;
    } // if else

    // KEY_F9
    if (!key_f9 && Keyboard.isKeyDown(Keyboard.KEY_F9)) {
      key_f9 = true;
      // available
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontLucida.getTextureName());
      GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_ADD);
      Debug.println("GL_TEXTURE_ENV_MODE = GL_ADD");
    } else if (key_f9 && !Keyboard.isKeyDown(Keyboard.KEY_F9)) {
      key_f9 = false;
    } // if else

    // KEY_F10
    if (!key_f10 && Keyboard.isKeyDown(Keyboard.KEY_F10)) {
      key_f10 = true;
      // available
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontLucida.getTextureName());
      GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
      Debug.println("GL_TEXTURE_ENV_MODE = GL_COMBINE (1.3)");
    } else if (key_f10 && !Keyboard.isKeyDown(Keyboard.KEY_F10)) {
      key_f10 = false;
    } // if else

    // KEY_F11
    if (!key_f11 && Keyboard.isKeyDown(Keyboard.KEY_F11)) {
      key_f11 = true;
      // available
    } else if (key_f11 && !Keyboard.isKeyDown(Keyboard.KEY_F11)) {
      key_f11 = false;
    } // if else

    // KEY_F12
    if (!key_f12 && Keyboard.isKeyDown(Keyboard.KEY_F12)) {
      key_f12 = true;
      // available
    } else if (key_f12 && !Keyboard.isKeyDown(Keyboard.KEY_F12)) {
      key_f12 = false;
    } // if else

    // KEY_HOME
    if (Keyboard.isKeyDown(Keyboard.KEY_HOME)) {
      lightAngleDelta += 0.01f;
    } // if

    // KEY_END
    if (Keyboard.isKeyDown(Keyboard.KEY_END)) {
      lightAngleDelta -= 0.01f;
    } // if

    // KEY_UP
    if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
      camera.lookUp(1f);
    } // if

    // KEY_DOWN
    if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
      camera.lookDown(1f);
    } // if

    // KEY_LEFT
    if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
      camera.lookLeft(1f);
    } // if

    // KEY_RIGHT
    if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
      camera.lookRight(1f);
    } // if else

    // KEY_ADD
    if (Keyboard.isKeyDown(Keyboard.KEY_ADD) || Keyboard.isKeyDown(Keyboard.KEY_Q)) {
      camera.moveUp(1);
    } // if

    // KEY_SUBTRACT
    if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT) || Keyboard.isKeyDown(Keyboard.KEY_E)) {
      camera.moveDown(1);
    } // if

    // KEY_1
    if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
      // available
      // fontLucida.setAnisotropicFiltering(1f);
    } // if

    // KEY_2
    if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
      // available
      // fontLucida.setAnisotropicFiltering(2f);
    } // if

    // KEY_4
    if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
      // available
      // fontLucida.setAnisotropicFiltering(4f);
    } // if

    // KEY_8
    if (Keyboard.isKeyDown(Keyboard.KEY_8)) {
      // available
      // fontLucida.setAnisotropicFiltering(8f);
    } // if

    // KEY_PERIOD
    if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
      // available
      int name = fontLucida.getTextureName();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, (mipMapMax == 0 ? 0 : --mipMapMax));
      Debug.println("MipMap Texture Max Level Decreasing: " + mipMapMax);
    } // if

    // KEY_COMMA
    if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)) {
      // available
      int name = fontLucida.getTextureName();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, ++mipMapMax);
      Debug.println("MipMap Texture Max Level Increase: " + mipMapMax);
    } // if

    // KEY_N
    if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
      // available
      int name = fontLucida.getTextureName();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, (mipMapMaxLOD == 0 ? 0 : --mipMapMaxLOD));
      Debug.println("MipMap Texture MAX LOD Decreasing: " + mipMapMaxLOD);
      try { Thread.sleep(100); } catch (InterruptedException e) { /* doh */ }
    } // if

    // KEY_M
    if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
      // available
      int name = fontLucida.getTextureName();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, ++mipMapMaxLOD);
      Debug.println("MipMap Texture MAX LOD Increase: " + mipMapMaxLOD);
      try { Thread.sleep(100); } catch (InterruptedException e) { /* doh */ }
    } // if

    // KEY_V
    if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
      // available
      int name = fontLucida.getTextureName();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, (mipMapMinLOD == 0 ? 0 : --mipMapMinLOD));
      Debug.println("MipMap Texture MIN LOD Decreasing: " + mipMapMinLOD);
      try { Thread.sleep(100); } catch (InterruptedException e) { /* doh */ }
    } // if

    // KEY_B
    if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
      // available
      int name = fontLucida.getTextureName();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, ++mipMapMinLOD);
      Debug.println("MipMap Texture MIN LOD Increase: " + mipMapMinLOD);
      try { Thread.sleep(100); } catch (InterruptedException e) { /* doh */ }
    } // if

    // KEY_X
    if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
      alphaReference += 0.05f;
      Debug.println("AlphaFunc with " + alphaReference);
      try { Thread.sleep(100); } catch (InterruptedException e) { /* doh */ }
    } // if

    // KEY_Z
    if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
      alphaReference -= 0.05f;
      Debug.println("AlphaFunc with " + alphaReference);
      try { Thread.sleep(100); } catch (InterruptedException e) { /* doh */ }
    } // if

    // KEY_P
    if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
      textConsole.println("Hello console... " + Math.random());
      try { Thread.sleep(100); } catch (InterruptedException e) { /* doh */ }
    } // if
    
    // KEY_C
    if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
      textConsole.clear();
    } // if
    
    // KEY_NUMPAD1
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)) {
      camera.moveBackwardsLeft(1);
    } // if

    // KEY_NUMPAD2
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
      camera.moveBackwards(1);
    } // if

    // KEY_NUMPAD3
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3)) {
      camera.moveBackwardsRight(1);
    } // if

    // KEY_NUMPAD4
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
      camera.moveLeft(1);
    } // if

    // KEY_NUMPAD5
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
      // available
    } // if

    // KEY_NUMPAD6
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
      camera.moveRight(1);
    } // if

    // KEY_NUMPAD7
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7)) {
      camera.moveForwardsLeft(1);
    } // if

    // KEY_NUMPAD8
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
      camera.moveForwards(1);
    } // if

    // KEY_NUMPAD9
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD9)) {
      camera.moveForwardsRight(1);
    } // if

    // KEY_RETURN
    if (!key_return && Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
      key_return = true;
      Debug.println("Return pressed: " + camera);
    } else if (key_return && !Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
      key_return = false;
    } // if else

    // Mouse
    if (Mouse.isButtonDown(1)) { // 0 = MOUSE_BUTTON_LEFT, 1 = MOUSE_BUTTON_RIGHT
      int dx = Mouse.getDX();
      int dy = Mouse.getDY();
      if (dx > 0) {
        camera.lookRight(1);
      } else if (dx < 0) {
        camera.lookLeft(1);
      } // if mouse move horisontally
      if (dy > 0) {
        camera.lookUp(0.5f);
      } else if (dy < 0) {
        camera.lookDown(0.5f);
      } // if mouse move vertically
      Mouse.setGrabbed(true);
    } else if (Mouse.isButtonDown(0)) {
      textConsole.setPosition(Mouse.getX(), Mouse.getY());
    } else {
      Mouse.setGrabbed(false);
    } // if mouse

  } // method
  
  private void render() {
    camera.refresh();

    GL11.glAlphaFunc(GL11.GL_GREATER, alphaReference); // for testing alpha func
    
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear buffers

    if (! lightPaused) {
      GL11.glPushMatrix(); // save viewpoint matrix
      lightAngle -= lightAngleDelta;
      GL11.glRotatef(lightAngle, 0, 1, 0);
      GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPos); // set light position
      GL11.glTranslatef(lightPos.get(0), lightPos.get(1), lightPos.get(2));
      Color.glColor3f(Color.YELLOW);
      lightSphere.draw(2, 16, 16); // draw "sun" (note: expensive drawing!)
      GL11.glPopMatrix(); // restore matrix
    } // if rotating

    // rendering code goes here
    camera.drawAxis();

    // draw spheres
    if (true) {
      if (sphereList == 0) {
        sphereList = GL11.glGenLists(1);
        GL11.glNewList(sphereList, GL11.GL_COMPILE);
        int size = 10;
        int distance = 80;
        for (int i = 0; i < 30; i++) {
          float s = (float)Math.random();
          float t = (float)Math.random();
          float u = (float)Math.random();
          GL11.glPushMatrix();
          GL11.glTranslatef(s * distance, t * distance, u * distance);
          GL11.glColor3f(s, t, u);
          sphere.draw(size, 16, 16);
          GL11.glPopMatrix();
        } // for
        GL11.glEndList();
      } else {
        GL11.glCallList(sphereList);
      } // if                        
    } // if sphere

    if (true) {
      GL11.glColor3f(1, 1, 1);
      fontLucida.drawTexture(100, 100);
    } // if textures
        
    if (true) {
      GL11.glColor3f(0.35f, 0.35f, 0.7f);
      // GL11.glColor3f(0f, 0f, 0f);
      // GL11.glColor3f(1f, 1f, 1f);      
      GL11.glPushMatrix(); // store matrix
      textRotationAngle += textRotationAngleDelta * 0.25f;
      GL11.glRotatef(textRotationAngle, 0, 1, 0);
      // GL11.glScalef(1f, 2f, 1f);
      // GL11.glTranslatef(0, 0, (float)Math.cos(textRotationAngle) * 20f);
      text.draw3D();
      GL11.glPopMatrix(); // restore matrix
    } // if

    if (true) {
      GL11.glColor4f(1f, 1f, 1f, 0.75f);
      textConsole.renderGL();
    } // if
    
    currentTime = System.currentTimeMillis();
    if (currentTime > timeToFPS) {
      currentFPS = framesRendered / FPS_UPDATE_TIME * 1000;
      minFPS = Math.min(currentFPS, minFPS);
      maxFPS = Math.max(currentFPS, maxFPS);
      framesRendered = 0; // reset FPS counter
      timeToFPS = currentTime + (long)FPS_UPDATE_TIME; // reset next fps update time
    } else {
      framesRendered++;
    } // if time to update fps
    GL11.glColor4f(1f, 1f, 1f, 0.25f);
    fontLucida.drawText2D("FPS: " + currentFPS + " (MIN: " + (int)minFPS + ", MAX: " + (int)maxFPS + ")", 0, 0);
  } // method

  private void init() throws Exception {
    // initialize LWJGL
    Display.setFullscreen(FULLSCREEN);
    DisplayMode d[] = Display.getAvailableDisplayModes();
    for (int i = 0; i < d.length; i++) {
      // System.out.println("Width " + d[i].getWidth() + " Height " + d[i].getHeight() + " Bits " + d[i].getBitsPerPixel() + " Freq " + d[i].getFrequency()); // ndhb temp
      if ((d[i].getWidth() == Configuration.getWidth() && d[i].getHeight() == Configuration.getHeight() && d[i].getBitsPerPixel() == Configuration.getBitsPerPixel() && d[i].getFrequency() == 60)) {
     // if ((d[i].getWidth() == Configuration.getWidth() && d[i].getHeight() == Configuration.getHeight() && d[i].getBitsPerPixel() == Configuration.getBitsPerPixel() && d[i].getFrequency() == 60)) {
        displayMode = d[i];
        break;
      } // if reasonable display mode detected
    } // for all display modes
    Display.setDisplayMode(displayMode);
    Display.setTitle(windowTitle);
    Display.setVSyncEnabled(Configuration.hasVsync()); // try framerate sync
    if (Configuration.hasAntiAliasing()) {
      PixelFormat pf = new PixelFormat(8, 16, 0, 2); // antialias http://lwjglold.org/forum/viewtopic.php?t=1398
      Display.create(pf); 
    } else {
      Display.create();
    } // if else
    Display.setLocation(0,0); // ndhb - dont cover textConsole window
    Keyboard.create();
    Mouse.create();

    // set rendering preferences
    GL11.glEnable(GL11.GL_DEPTH_TEST); // enables depth testing
    GL11.glDepthFunc(GL11.GL_LEQUAL); // type of depth testing

    if (Configuration.hasCullFace()) { 
      GL11.glEnable(GL11.GL_CULL_FACE); // cull back faces
    } // if

    if (Configuration.hasLighting()) { 
      GL11.glEnable(GL11.GL_LIGHTING); // apply lighting
    } // if

    if (Configuration.hasFill()) {
      GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
    } else {
      GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
    } // if else

    // GL11.glLineStipple(1, (short)0x0F0F); // select line stipple mode        
    GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // enable perspective calculations    

    // set perspective rendering
    GL11.glMatrixMode(GL11.GL_PROJECTION); // select the projection matrix
    GL11.glLoadIdentity(); // reset matrix
    GLU.gluPerspective(45, (float)displayMode.getWidth() / (float)displayMode.getHeight(), 0.1f, 2000f);
    GL11.glMatrixMode(GL11.GL_MODELVIEW); // select the modelview matrix

    // create light components       
    GL11.glEnable(GL11.GL_LIGHT0);
    FloatBuffer diffuseLight = BufferUtils.createFloatBuffer(4).put(3, 1f).put(2, 0.6f).put(1, 0.6f).put(0, 0.6f);
    FloatBuffer specularLight = BufferUtils.createFloatBuffer(4).put(3, 1f).put(2, 0.9f).put(1, 0.9f).put(0, 0.9f);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuseLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, specularLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPos);

    // define material(s) used
    GL11.glEnable(GL11.GL_COLOR_MATERIAL);
    GL11.glMateriali(GL11.GL_FRONT, GL11.GL_SHININESS, 64);

    // GL11.glEnable(GL11.GL_BLEND); // enable blending
    // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_COLOR);

    // camera = new Camera(new Vector3f(33, 55, 140), new Vector3f(0, 0, -1)); // front
    camera = new Camera(new Vector3f(106.42713f, 27.0f, 38.21351f), new Vector3f(-0.51368123f, 0.0f, -0.84130013f));
    // camera = new Camera(new Vector3f(-200, 50f, 100f), new Vector3f(0.55f, 0.0f, 0.02f)); // anisotrophic

    // BitmapFont lucidaBitmapFont = new BitmapFileFont("asset/font/LucidaSansUnicode512x512x8.bff");
    BitmapFont lucidaBitmapFont = new BitmapFileFont("asset/font/FranklinGothicMedium512x512x32.bff");
    // BitmapFont courierBitmapFont = new BitmapFileFont("asset/font/Courier512x512x32.bff");
    BitmapFont courierBitmapFont = new BitmapFileFont("asset/font/Courier256x256x8.bff");
    
    fontLucida = new Font(lucidaBitmapFont);
    fontCourier = new Font(courierBitmapFont);
    
    textConsole = new TextConsole(fontCourier, 0, Display.getDisplayMode().getHeight() - 16, 10);
    // textConsole = new TextConsole(fontLucida, 0, Display.getDisplayMode().getHeight() - 16, 10);
    textConsole.initGL();
    
    text = new Text(fontCourier, testString);
    text.initGL();
    
    // GL11.glClearColor(0.85f, 0.65f, 0.25f, 0);
    GL11.glClearColor(0, 0, 0, 0);
  } // method

  private void cleanup() {
    if (fontLucida != null)
      fontLucida.cleanupGL();
    if (fontCourier != null)
      fontCourier.cleanupGL();
    if (text != null)
      text.cleanupGL();
    if (textConsole != null);
      textConsole.cleanupGL();
    Mouse.destroy();
    Keyboard.destroy();
    Display.destroy();
    // Debug.println("GL warnings issued: " + GL.getNumWarningsIssued());
  } // method

} // class