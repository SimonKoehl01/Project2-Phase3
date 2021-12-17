package titan.solver;
import titan.*;
import titan.math.*;

public class State implements StateInterface{

  public Vector3d[] positions;
  public Vector3d[] velocities;
  public int size;
  public State(int size) {
    this.size = size;
    this.positions = new Vector3d[size];
    this.velocities = new Vector3d[size];
  }
  /**
   * Update a state to a new state computed by: this + step * rate
   *
   * @param step   The time-step of the update
   * @param rate   The average rate-of-change over the time-step. Has dimensions of [state]/[time].
   * @return The new state after the update. Required to have the same class as 'this'.
   */
  public StateInterface addMul(double step, RateInterface rate) {
    Rate rateObject = (Rate) rate;
    State result = new State(this.size);
    for(int i = 0; i < this.size; i++) {
      result.velocities[i] = (Vector3d) this.velocities[i].addMul(step, rateObject.changes[i]);
      result.positions[i] = (Vector3d) this.positions[i].addMul(step, this.velocities[i]);
    }
    return result;
  }

  public State clone() {
    State result = new State(this.size);
    for(int i = 0; i < this.size; i++) {
      result.positions[i] = this.positions[i];
      result.velocities[i] = this.velocities[i];
    }
    return result;
  }
  public State addToPositions(Rate change) {
    State result = this.clone();
    for(int i = 0; i < this.size; i++) {
      result.positions[i] = (Vector3d) this.positions[i].add(change.changes[i]);
    }
    return result;
  }
  public State update(Rate posChange, Rate velChange) {
    State result = this.clone();
    for(int i = 0; i < this.size; i++) {
      result.velocities[i] = (Vector3d) this.velocities[i].add(velChange.changes[i]);
      result.positions[i] = (Vector3d) this.positions[i].add(posChange.changes[i]);
    }
    return result;
  }

  public State scale(double scalar) {
    State result = this.clone();
    for(int i = 0; i < this.size; i++) {
      result.positions[i] = (Vector3d) this.positions[i].mul(scalar);
    }
    return result;
  }

  public State addAccelerations(double scalar, Rate change) {
    State result = this.clone();
    for(int i = 0; i < this.size; i++) {
      result.velocities[i] = (Vector3d) this.velocities[i].addMul(scalar, change.changes[i]);
    }
    return result;
  }

  public String toString() {
    String result = "[";
    for(int i = 0; i < this.size; i++) {
      result += "(position: " + this.positions[i].toString() + " velocity: " + this.velocities[i].toString() + ")\n";
    }
    return result + "]";
  }
}
