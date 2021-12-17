package titan.math;
public class Vector4d {
  public double x;
  public double y;
  public double z;

  public double k;
  public Vector4d(double x, double y, double z, double k) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.k = k;
  }

  public Vector4d sub(Vector4d other) {
    return new Vector4d(this.x-other.x, this.y-other.y, this.z-other.z, this.k-other.k);
  }
  public Vector4d add(Vector4d other) {
    return new Vector4d(this.x+other.x, this.y+other.y, this.z+other.z, this.k+other.k);
  }
  public Vector4d scale(double scalar) {
    return new Vector4d(this.x*scalar, this.y*scalar, this.z*scalar, this.k*scalar);
  }

  public double norm() {
    return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z + this.k*this.k);
  }

  public double dist(Vector4d other) {
    return this.sub(other).norm();
  }
  public String toString() {
    String result = "(" + this.x + "," + this.y + "," + this.z + "," + this.k + ")";
    return result;
  }
}
