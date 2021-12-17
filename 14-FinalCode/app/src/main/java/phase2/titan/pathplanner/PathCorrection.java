package titan.pathplanner;
//Project-specific imports
import titan.math.*;
import titan.*;
public class PathCorrection {

  private static Vector3d[] desiredTrajectory;
  private static final double tolerance = 10.0;
  public static Vector3d getClosestNormalPoint(Vector3d pos, double currentVelocity) {
    //Get closest normal
    Vector3d recordNormal = new Vector3d(0, 0, 0);
    Vector3d recordB = new Vector3d(0, 0, 0);

    double smallest = Double.MAX_VALUE;
    for(int i = 1; i < desiredTrajectory.length; i++) {
      Vector3d normalPoint = getNormalPoint(pos, desiredTrajectory[i-1], desiredTrajectory[i]);
      if(normalPoint != null) {

        double distance = pos.dist(normalPoint);
        if(distance < smallest) {
          smallest = distance;
          recordNormal = normalPoint;
          recordB = (Vector3d) desiredTrajectory[i].sub(desiredTrajectory[i-1]);
        }
      }
    }
    //Get goal
    return recordNormal;
  }

  public static void setDesiredTrajectory(Vector3dInterface[] desiredTrajectoryVar) {
    desiredTrajectory = (Vector3d[]) desiredTrajectoryVar;
  }

  private static Vector3d getNormalPoint(Vector3d point, Vector3d start, Vector3d end) {
    Vector3d a = (Vector3d) point.sub(start);
    Vector3d b = (Vector3d) end.sub(start);
    Vector3d normalizedB = (Vector3d) b.mul(1/b.norm());

    double scalar = a.dotProduct(normalizedB);
    Vector3d projection = (Vector3d) normalizedB.mul(scalar);
    if(scalar < 0 || projection.norm() > b.norm()) {
      return null;
    }
    Vector3d result = (Vector3d) start.add(projection);
    return result;
  }
}
