package titan.math;
import titan.Vector3dInterface;
public class Vector3d implements Vector3dInterface {
  double x;
  double y;
  double z;

  /**
   *Construstor to initalize x,y and z coordinates
   *
   * @param   xValue    x coordinate
   * @param   yValue    y coordinate
   * @param   zValue    z coordinate
   */

  public Vector3d(double xValue, double yValue, double zValue) {

    x = xValue;
    y = yValue;
    z = zValue;
  }

  public Vector3d() {
    x = 0.0;
    y = 0.0;
    z = 0.0;
  }

  /**
   * method to return X coordinate
   * @return X coordinate of type double
   */

  public double getX() {
    return this.x;
  }

  /**
   * method to set X coordinate
   * @param  x  x-coordinate
   */

  public void setX(double x) {
    this.x = x;
  }

  /**
   * method to return Y coordinate
   * @return Y coordinate of type double
   */

  public double getY() {
    return this.y;
  }

  /**
   * method to set Y coordinate
   * @param  Y  y-coordinate
   */

  public void setY(double y) {
    this.y = y;
  }

  /**
   * method to return Z coordinate
   * @return Z coordinate of type double
   */

  public double getZ() {
    return this.z;
  }

  /**
   * method to set Z coordinate
   * @param  Z  z-coordinate
   */

  public void setZ(double z) {
    this.z = z;
  }

  /**
   * Method to add 2 Vectors
   * @param   other    3D Vector to be added
   * @return  new 3D Vector which is the sum of the original 2 Vectors
   */

  public Vector3dInterface add(Vector3dInterface other) {
    Vector3d v2 = (Vector3d) other;
    double newX = this.x + v2.x;
    double newY = this.y + v2.y;
    double newZ = this.z + v2.z;
    return new Vector3d(newX, newY, newZ);
  }

  /**
   * Method to subtract 2 Vectors
   * @param 3D vector to be subtracted
   * @return new 3D vector which is the difference of the 2 original Vectors
   */

  public Vector3dInterface sub(Vector3dInterface other) {
    Vector3d v2 = (Vector3d) other;
    double newX = this.x - v2.x;
    double newY = this.y - v2.y;
    double newZ = this.z - v2.z;
    return new Vector3d(newX, newY, newZ);
  }

  /**
   * Method to return a scalar multiple of a 3D Vector
   * @param   scalar    the scalar to be multiplied to the Vector
   * @return a new 3D Vector whose coordinates are the scalar multiple of the original coordinates
   */

  public Vector3dInterface mul(double scalar) {
    return new Vector3d(scalar * this.x, scalar * this.y, scalar * this.z);
  }

  /**
   * Method to add scalar multiple of Vector 2 to Vector 1
   * @param   scalar    a scalar value to be multiplied to Vector 2
   * @param   other     3D Vector 2
   * @return  result of v1+(s*v2)
   */

  public Vector3dInterface addMul(double scalar, Vector3dInterface other) {
    Vector3d v2 = (Vector3d) other;
    return add(v2.mul(scalar));
  }

  /**
   * Method to calculate normal of a 3D Vector
   * @return the normal of the 3D Vector
   */

  public double norm() {
    return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
  }

  /**
   * Method to calculate the absolute distance between two Vectors
   * @param   other    3D Vector v2 which is at a certain distance away from v1
   * @return the absolute distance between v1 and v2
   */

  public double dist(Vector3dInterface other) {
    return sub(other).norm();
  }

  /**
  * Method to calculate the dot product between this Vector3d object and the given other Vector3d object
  * @param other Vector3d object with which the dot product should be computed
  * @return The dot product of this Vector3d object and the other given Vector3d object
  */
  public double dotProduct(Vector3dInterface other) {
    return this.getX() * other.getX() + this.getY() * other.getY() + this.getZ() * other.getZ();
  }
  /**
  * @return A string in this format:
  * Vector3d(-1.0, 2, -3.0) should print out (-1.0,2.0,-3.0)
  */
  public String toString() {
    String result = "(" + this.x + "," + this.y + "," + this.z + ")";
    return result;
  }

  public boolean equals(Vector3dInterface v2) {
    if(this.getX() == v2.getX() && this.getY() == v2.getY() && this.getZ() == v2.getZ()) {
      return true;
    }
    return false;
  }

}
