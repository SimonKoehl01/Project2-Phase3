package titan.solver;

import titan.*;
import titan.math.*;


//This class is a data structure for the rate of change of an ODE
public class Rate implements RateInterface {
  Vector3d[] changes;

  public Rate(int size) {
    changes = new Vector3d[size];
  }

  public Rate(Vector3d[] data) {
    changes = data;
  }

  public Rate scale(double scalar) {
    Rate result = new Rate(changes.length);
    for(int i = 0; i < changes.length; i++) {

      result.changes[i] = (Vector3d) this.changes[i].mul(scalar);
    }
    return result;
  }

  public Rate add(Rate other) {
    Rate result = new Rate(this.changes.length);
    for(int i = 0; i < changes.length; i++) {
      result.changes[i] = (Vector3d) this.changes[i].add(other.changes[i]);
    }
    return result;
  }
}
