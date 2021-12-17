package titan.windsimulation;
import titan.math.*;
public class StochasticWindModel {
  //y = 0.9999975x + 0.3
  private static final double min = 0.2;

  private static final double max = 1.2;
  private static double direction;
  public static double getWindSpeed(double height) {
    if(height > 600000) {
      return 0;
    }

    return 0.0010025 * height + 0.3;
  }

  public static double getWind(double height) {
    double rand = Math.random();

    if(direction == 0) {
      direction = 1;
    }
    if(rand < 0.1) {
      direction *= -1;
    }
    double windSpeed = getWindSpeed(height);
    return getWindSpeed(height) * rand  * direction;
  }
}
