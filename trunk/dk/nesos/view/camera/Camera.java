package dk.nesos.view.camera;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.glu.*;
import org.lwjgl.util.vector.*;

/**
 * <P>
 * Simulates a simple camera in OpenGL.
 * <P>
 * <B>NOTE: Upwards is defined to be the Vector [0, 1, 0].</B>
 * 
 * @see org.lwjgl.opengl.glu.GLU#gluLookAt(float, float, float, float, float,
 *      float, float, float, float)
 * @author ndhb
 */
public final class Camera {

    private static final int NUMBER_OF_FRUSTUMS = 6;

    /**
     * The UP vector in the world.
     */
    private static final Vector3f UP = new Vector3f(0, 1, 0);

    /**
     * <P>
     * The direction in 3D space of the camera.
     * <P>
     * The camera is always looking in this direction.
     */
    private Vector3f direction;

    /**
     * <P>
     * The 6 frustum planes defined by the view.
     */
    private Frustum[] frustums = new Frustum[NUMBER_OF_FRUSTUMS];

    /**
     * <P>
     * The position in 3D space of the camera.
     * <P>
     * The position of the virtual observer.
     */
    private Vector3f position;

    
    /**
     * <P>
     * Display List name for drawing axis
     */
    private int axisListName;
    
    /**
     * <P>
     * This constructor quickly sets UP a camera at [1, 20, 1] with direction
     * [1, 0, 0].
     * <P>
     * That means the camera is located 20 units UP the Y-axis and looking down
     * the X-axis.
     */
    public Camera() {
        this(new Vector3f(1, 20, 1), new Vector3f(1, 0, 0));
    } // constructor

    /**
     * <P>
     * This constructor sets UP a camera and its direction at the supplied
     * locations in 3D space.
     */
    public Camera(Vector3f location, Vector3f direction) {
        this.position = location;
        this.direction = direction;
        for (int f = 0; f < NUMBER_OF_FRUSTUMS; f++) {
            frustums[f] = new Frustum();
        } // for all frustums
    } // constructor

    /**
     * <P>
     * Retrieves the direction in 3D space of the camera.
     * 
     * @return Vector3f representing the camera direction.
     */
    public Vector3f getDirection() {
        return direction;
    } // method

    /**
     * <P>
     * Retrieves the computed frustums.
     * 
     * @return Returns the frustums
     */
    public Frustum[] getFrustums() {
        return frustums;
    } // method

    // /** TODO ndhb: move code to some class implementing Intersectable
    // interface (BoundingPoint?)
    // * <P>Checks if the specified point is contained in the current frustum.
    // *
    // * @param cx the X-coordinate of the point
    // * @param cy the Y-coordinate of the point
    // * @param cz the Z-coordinate of the point
    // * @return <code>true</code> if the the specified point is in the current
    // frustum, otherwise <code>false</code>.
    // */
    // public boolean pointInFrustum(float cx, float cy, float cz) {
    // for (int p = 0; p < 6; p++) {
    // if( frustum[p][0] * cx + frustum[p][1] * cy + frustum[p][2] * cz +
    // frustum[p][3] <= 0 ) {
    // return false;
    // } // if
    // } // for all planes
    // return true;
    // } // method
    //    

    /**
     * <P>
     * Retrieves the position in 3D space of the camera.
     * 
     * @return Vector3f representing the camera position.
     */
    public Vector3f getPosition() {
        return position;
    } // method

    /**
     * <P>
     * Rotates the direction of the camera downwards.
     * 
     * <P>
     * <B>Note: Specify degrees (instead of radians).</B>
     * 
     * @param degrees
     *            the number of degrees.
     */
    public void lookDown(float degrees) {
        float radians = (float) Math.toRadians(degrees);
        Vector3f axis = Vector3f.cross(direction, UP, null); // the axis to rotate around, is perpendular to direction and up
        rotateAroundAxis(-radians, axis);
    } // method

    /**
     * <P>
     * Rotates the camera direction in the XZ-plane.
     * 
     * <P>
     * <B>Note: Specify degrees (instead of radians).</B>
     * 
     * @param degrees
     *            the number of degrees.
     */
    public void lookLeft(float degrees) {
        float radians = (float)Math.toRadians(degrees);
        rotateY(radians); // just rotate around the Y-axis
    } // method

    /**
     * <P>
     * Rotates the camera direction in the XZ-plane.
     * 
     * <P>
     * <B>Note: Specify degrees (instead of radians).</B>
     * 
     * @param degrees
     *            the number of degrees.
     */
    public void lookRight(float degrees) {
        float radians = (float)Math.toRadians(degrees);
        rotateY(-radians); // just rotate around the Y-axis
    } // method

    /**
     * <P>
     * Rotates the direction of the camera upwards.
     * 
     * <P>
     * <B>Note: Specify degrees (instead of radians).</B>
     * 
     * @param degrees
     *            the number of degrees.
     */
    public void lookUp(float degrees) {
        float radians = (float)Math.toRadians(degrees);
        Vector3f axis = Vector3f.cross(direction, UP, null); // the axis to rotate around is perpendular to direction and up
        rotateAroundAxis(radians, axis);
    } // method

    /**
     * <P>
     * Moves the position of the camera backwards in the current direction.
     * <P>
     * The Y-coordinate remains unchanged.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveBackwards(float delta) {
        position.x = position.x - (direction.x * delta);
        position.z = position.z - (direction.z * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera backwards and to the left of the current
     * direction.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveBackwardsLeft(float delta) {
        position.x = position.x - (direction.x * delta) + (direction.z * delta);
        position.z = position.z - (direction.z * delta) - (direction.x * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera backwards and to the right of the
     * current direction.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveBackwardsRight(float delta) {
        position.x = position.x - (direction.x * delta) - (direction.z * delta);
        position.z = position.z - (direction.z * delta) + (direction.x * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera downwards in the Y-axis.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveDown(float delta) {
        position.y -= delta;
    } // method

    /**
     * <P>
     * Moves the position of the camera forward in the current direction.
     * <P>
     * The Y-coordinate remains unchanged.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveForwards(float delta) {
        position.x = position.x + (direction.x * delta);
        position.z = position.z + (direction.z * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera forwards and to the left of the current
     * direction.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveForwardsLeft(float delta) {
        position.x = position.x + (direction.x * delta) + (direction.z * delta);
        position.z = position.z + (direction.z * delta) - (direction.x * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera forwards and to the right of the current
     * direction.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveForwardsRight(float delta) {
        position.x = position.x + (direction.x * delta) - (direction.z * delta);
        position.z = position.z + (direction.z * delta) + (direction.x * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera to the left of the current direction.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveLeft(float delta) {
        position.x = position.x + (direction.z * delta);
        position.z = position.z - (direction.x * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera to the right of the current direction.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveRight(float delta) {
        position.x = position.x - (direction.z * delta);
        position.z = position.z + (direction.x * delta);
    } // method

    /**
     * <P>
     * Moves the position of the camera upwards in the Y-axis.
     * 
     * @param delta
     *            the number of units to move.
     */
    public void moveUp(float delta) {
        position.y += delta;
    } // method

    /**
     * <P>
     * Performs an update of the view.
     * <P>
     * After modifying the camera position or direction this method should be
     * called to orient the OpenGL Modelview-matrix.
     */
    public void refresh() {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        // maybe do camera zoom here (with VIEWPORT_MATRIX)?
        GLU.gluLookAt(position.x, position.y, position.z, position.x + direction.x, position.y + direction.y, position.z + direction.z, UP.x, UP.y, UP.z);
        computeFrustums(); // recompute frustums now
    } // method

    /**
     * <P>
     * Rotates the direction around an arbitrary axis (represented by a vector).
     * 
     * @link http://www.cprogramming.com/tutorial/3d/rotation.html
     * @param radians the amount to rotate
     * @param axis the vector representing the axis.
     */
    public void rotateAroundAxis(float radians, Vector3f axis) {
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float t = 1 - cos;
        if (axis.length() != 0) {
            axis.normalise(); // unit vector
        } // if vector is not zero vector
        Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.m00 = t * axis.x * axis.x + cos; // row 0, column 0
        rotationMatrix.m01 = t * axis.x * axis.y + sin * axis.z; // row 0, column 1
        rotationMatrix.m02 = t * axis.x * axis.z - sin * axis.y; // row 0, column 2
        rotationMatrix.m10 = t * axis.x * axis.y - sin * axis.z; // row 1, column 0
        rotationMatrix.m11 = t * axis.y * axis.y + cos; // row 1, column 1
        rotationMatrix.m12 = t * axis.y * axis.z + sin * axis.x; // row 1, column 2
        rotationMatrix.m20 = t * axis.x * axis.y + sin * axis.y; // row 2, column 0
        rotationMatrix.m21 = t * axis.y * axis.z - sin * axis.x; // row 2, column 1
        rotationMatrix.m22 = t * axis.z * axis.z + cos; // row 2, column 2
        Matrix3f.transform(rotationMatrix, direction, direction); // changes direction vector
    } // method

    /**
     * Rotating the view around the X-axis can be achieved by multiplying the
     * matrix:<BR>
     * 
     * <PRE>
     *              [ 1       0        0      ] 
     *   R_x(t) =   [ 0       cos(t)  -sin(t) ]
     *              [ 0       sin(t)  cos(t)  ]
     * </PRE>
     * 
     * <BR>
     * with the direction vector.
     * <P>
     * Note. This method unfolds the matrix multiplication to avoid creating
     * unnecessary objects (eg. Matrix3f).
     * 
     * @param radians the amount to rotate
     */
    public void rotateX(float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        direction.y = (direction.y * cos) + (direction.z * -sin);
        direction.z = (direction.y * sin) + (direction.z * cos);
    } // method

    /**
     * Rotating the view around the Y-axis can be achieved by multiplying the
     * matrix:<BR>
     * 
     * <PRE>
     *              [ cos(t)  0       sin(t)  ] 
     *   R_y(t) =   [ 0       1       0       ]
     *              [ -sin(t) 0       cos(t)  ]
     * </PRE>
     * 
     * <BR>
     * with the direction vector.
     * <P>
     * Note. This method unfolds the matrix multiplication to avoid creating
     * unnecessary objects (eg. Matrix3f).
     * 
     * @param radians the amount to rotate
     */
    public void rotateY(float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        direction.x = (direction.x * cos) + (direction.z * sin);
        direction.z = (direction.x * -sin) + (direction.z * cos);
    } // method

    /**
     * Rotating the view around the Z-axis can be achieved by multiplying the
     * matrix:<BR>
     * 
     * <PRE>
     *              [ cos(t)  -sin(t) 0       ] 
     *   R_z(t) =   [ sin(t)  cos(t)  0       ]
     *              [ 0       0       1       ]
     * </PRE>
     * 
     * <BR>
     * with the direction vector.
     * <P>
     * Note. This method unfolds the matrix multiplication to avoid creating
     * unnecessary objects (eg. Matrix3f).
     * 
     * @param radians the amount to rotate
     */
    public void rotateZ(float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        direction.x = (direction.x * cos) + (direction.y * -sin);
        direction.y = (direction.x * sin) + (direction.y * cos);
    } // method

    /**
     * <P>Sets the camera looking in the specified direction.
     * 
     * @param direction The direction to set.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    } // method

    /**
     * <P>Sets the camera at the specified position.
     * 
     * @param position The position to set.
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    } // method

    /**
     * <P>
     * Returns a string representation of the position and direction.
     * 
     * @see java.lang.Object#toString()
     * @return a String with the position and direction values.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(System.getProperty("line.separator"));
        s.append("Position:  ");
        s.append(position.toString());
        s.append(System.getProperty("line.separator"));
        s.append("Direction: ");
        s.append(direction.toString());
        return s.toString();
    } // method

    /**
     * <P>
     * Computes the frustum planes based on the current ModelView and Projection
     * matrices.
     * <P>
     * Copied from Mark Morley's article on OpenGL.org "Frustum Culling in
     * OpenGL"
     */
    private void computeFrustums() {
        FloatBuffer proj = BufferUtils.createFloatBuffer(16);
        FloatBuffer modl = BufferUtils.createFloatBuffer(16);
        FloatBuffer clip = BufferUtils.createFloatBuffer(16);

        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, proj); // get the current projection matrix from OpenGL
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modl); // get the current modelview matrixfrom OpenGL

        // combine the two matrices (multiply projection by modelview)
        clip.put(0, modl.get(0) * proj.get(0) + modl.get(1) * proj.get(4) + modl.get(2) * proj.get(8) + modl.get(3) * proj.get(12));
        clip.put(1, modl.get(0) * proj.get(1) + modl.get(1) * proj.get(5) + modl.get(2) * proj.get(9) + modl.get(3) * proj.get(13));
        clip.put(2, modl.get(0) * proj.get(2) + modl.get(1) * proj.get(6) + modl.get(2) * proj.get(10) + modl.get(3) * proj.get(14));
        clip.put(3, modl.get(0) * proj.get(3) + modl.get(1) * proj.get(7) + modl.get(2) * proj.get(11) + modl.get(3) * proj.get(15));

        clip.put(4, modl.get(4) * proj.get(0) + modl.get(5) * proj.get(4) + modl.get(6) * proj.get(8) + modl.get(7) * proj.get(12));
        clip.put(5, modl.get(4) * proj.get(1) + modl.get(5) * proj.get(5) + modl.get(6) * proj.get(9) + modl.get(7) * proj.get(13));
        clip.put(6, modl.get(4) * proj.get(2) + modl.get(5) * proj.get(6) + modl.get(6) * proj.get(10) + modl.get(7) * proj.get(14));
        clip.put(7, modl.get(4) * proj.get(3) + modl.get(5) * proj.get(7) + modl.get(6) * proj.get(11) + modl.get(7) * proj.get(15));

        clip.put(8, modl.get(8) * proj.get(0) + modl.get(9) * proj.get(4) + modl.get(10) * proj.get(8) + modl.get(11) * proj.get(12));
        clip.put(9, modl.get(8) * proj.get(1) + modl.get(9) * proj.get(5) + modl.get(10) * proj.get(9) + modl.get(11) * proj.get(13));
        clip.put(10, modl.get(8) * proj.get(2) + modl.get(9) * proj.get(6) + modl.get(10) * proj.get(10) + modl.get(11) * proj.get(14));
        clip.put(11, modl.get(8) * proj.get(3) + modl.get(9) * proj.get(7) + modl.get(10) * proj.get(11) + modl.get(11) * proj.get(15));

        clip.put(12, modl.get(12) * proj.get(0) + modl.get(13) * proj.get(4) + modl.get(14) * proj.get(8) + modl.get(15) * proj.get(12));
        clip.put(13, modl.get(12) * proj.get(1) + modl.get(13) * proj.get(5) + modl.get(14) * proj.get(9) + modl.get(15) * proj.get(13));
        clip.put(14, modl.get(12) * proj.get(2) + modl.get(13) * proj.get(6) + modl.get(14) * proj.get(10) + modl.get(15) * proj.get(14));
        clip.put(15, modl.get(12) * proj.get(3) + modl.get(13) * proj.get(7) + modl.get(14) * proj.get(11) + modl.get(15) * proj.get(15));

        // extract the numbers for the RIGHT plane
        frustums[0].setA(clip.get(3) - clip.get(0));
        frustums[0].setB(clip.get(7) - clip.get(4));
        frustums[0].setC(clip.get(11) - clip.get(8));
        frustums[0].setD(clip.get(15) - clip.get(12));
        frustums[0].normalise();

        // extract the numbers for the LEFT plane
        frustums[1].setA(clip.get(3) + clip.get(0));
        frustums[1].setB(clip.get(7) + clip.get(4));
        frustums[1].setC(clip.get(11) + clip.get(8));
        frustums[1].setD(clip.get(15) + clip.get(12));
        frustums[1].normalise();

        // extract the numbers for the BOTTOM plane
        frustums[2].setA(clip.get(3) + clip.get(1));
        frustums[2].setB(clip.get(7) + clip.get(5));
        frustums[2].setC(clip.get(11) + clip.get(9));
        frustums[2].setD(clip.get(15) + clip.get(13));
        frustums[2].normalise();

        // extract the numbers for the TOP plane
        frustums[3].setA(clip.get(3) - clip.get(1));
        frustums[3].setB(clip.get(7) - clip.get(5));
        frustums[3].setC(clip.get(11) - clip.get(9));
        frustums[3].setD(clip.get(15) - clip.get(13));
        frustums[3].normalise();

        // extract the numbers for the FAR plane
        frustums[4].setA(clip.get(3) - clip.get(2));
        frustums[4].setB(clip.get(7) - clip.get(6));
        frustums[4].setC(clip.get(11) - clip.get(10));
        frustums[4].setD(clip.get(15) - clip.get(14));
        frustums[4].normalise();

        // extract the numbers for the NEAR plane
        frustums[5].setA(clip.get(3) + clip.get(2));
        frustums[5].setB(clip.get(7) + clip.get(6));
        frustums[5].setC(clip.get(11) + clip.get(10));
        frustums[5].setD(clip.get(15) + clip.get(14));
        frustums[5].normalise();
    } // method

    /**
     * <P>Draws lines along the axis of the current coordinate system.
     *
     */
    public void drawAxis() {
        if (axisListName == 0) { // create display list
            axisListName = GL11.glGenLists(1);
            GL11.glNewList(axisListName, GL11.GL_COMPILE);
                GL11.glEnable(GL11.GL_LINE_STIPPLE);
                GL11.glBegin(GL11.GL_LINES);
                    GL11.glColor3f(1, 0, 0); // color red
                    GL11.glVertex3f(0, 0, 0); //  draw line along x
                    GL11.glVertex3f(100, 0, 0);
                    GL11.glColor3f(0, 1, 0); // color green
                    GL11.glVertex3f(0, 0, 0); // draw line along y
                    GL11.glVertex3f(0, 100, 0);
                    GL11.glColor3f(0, 0, 1); // color blue
                    GL11.glVertex3f(0, 0, 0); // draw line along z
                    GL11.glVertex3f(0, 0, 100);
                GL11.glEnd();
                GL11.glDisable(GL11.GL_LINE_STIPPLE);
            GL11.glEndList();
        } else { // call display list
            GL11.glCallList(axisListName);
        } // if else
    } // method
    
} // class
