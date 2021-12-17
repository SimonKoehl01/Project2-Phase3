package titan.bodies;

import javafx.scene.shape.*;
import javafx.scene.paint.PhongMaterial;

import javafx.scene.image.Image;
import titan.math.*;
import titan.interfaces.*;
public class CelestialBody extends Sphere implements BodyInterface {

  private String materialFile;

  public String name;
  public Vector3d position;

  public Vector3d velocity;
  public Vector3d drawPosition;

  public double r;
  public double mass;
  public static double scalingFactor = 1e5;
  public CelestialBody(String name, double xPos, double yPos, double zPos, double vx, double vy, double vz, double mass, double radius, double drawRadius, String materialFile) {
    super(drawRadius);
    this.r = radius;
    this.name = name;
    this.mass = mass; // mass of the planet
    this.materialFile = materialFile; // Texture of the planet to show on the solar system
    this.position = new Vector3d(xPos, yPos, zPos); // Vector position of the planet
    this.velocity = new Vector3d(vx, vy, vz); // Vector of the velocity of the planet
    update(this.position, this.velocity);
  }

  public void setScalingFactor(double scale) {
    scalingFactor = scale;
  }
  // Method that scales vectors position of planets to fit on the screen
  @Override
  public void scale() {
    this.drawPosition = (Vector3d) this.position.mul(1/this.scalingFactor);
  }
  //This method prepares the textures for the body.

  public void prepareTexture() {
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseMap(new Image(materialFile));
    if(this.name.equals("Sun")) {
      material.setSelfIlluminationMap(new Image("assets/sun.jpg"));
    }
    setMaterial(material);
  }

  //This method updates the body's position.
  public void update(Vector3d position, Vector3d velocity) {
    this.position = position;
    this.velocity = velocity;
    scale();
    translateXProperty().set(this.drawPosition.getX());
    translateYProperty().set(this.drawPosition.getY());
    translateZProperty().set(this.drawPosition.getZ());
  }

  public void show(Vector3d pos) {
    this.position = pos;
    scale();
    translateXProperty().set(this.drawPosition.getX());
    translateYProperty().set(this.drawPosition.getY());
    translateZProperty().set(this.drawPosition.getZ());
  }

  //This method is used to get the CelestialBody's drawing position
  @Override
  public Vector3d getDrawingPosition() {

    return this.drawPosition;
  }
}
