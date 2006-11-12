package dk.nesos.view.camera;

/**
 * A Frustum is a plane defining the view volume.
 *
 * @author ndhb
 */
public final class Frustum {

	private float a;
	private float b;
	private float c;
	private float d;

	public Frustum() {
		// empty - default values works
	} // constructor

	/**
	 * Constructs the Frustum defined by the equation ax + by + cz + d = 0.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public Frustum(float a, float b, float c, float d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	} // constructor

	/**
	 * @return Returns the a.
	 */
	public float getA() {
		return a;
	} // method

	/**
	 * @param a The a to set.
	 */
	public void setA(float a) {
		this.a = a;
	} // method

	/**
	 * @return Returns the b.
	 */
	public float getB() {
		return b;
	} // method

	/**
	 * @param b The b to set.
	 */
	public void setB(float b) {
		this.b = b;
	} // method

	/**
	 * @return Returns the c.
	 */
	public float getC() {
		return c;
	} // method

	/**
	 * @param c The c to set.
	 */
	public void setC(float c) {
		this.c = c;
	} // method

	/**
	 * @return Returns the d.
	 */
	public float getD() {
		return d;
	} // method

	/**
	 * @param d The d to set.
	 */
	public void setD(float d) {
		this.d = d;
	} // method

	/**
	 * Normalizing is an optimization for determining distance to the plane.
	 */
	public void normalise() {
		float t = (float)Math.sqrt(a * a + b * b + c * c);
		a /= t;
		b /= t;
		c /= t;
		d /= t;
	} // method

} // class
