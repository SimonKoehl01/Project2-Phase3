package titan.solarsystem;
import javafx.scene.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.*;
import titan.bodies.*;

import titan.solver.*;

public class SolarSystem extends Group {
  public CelestialBody[] planets;
  Rotate rotation;
  Transform t = new Rotate();

  public int size;
  public double scalingFactor;
  int iterator = 0;

  double[] masses;
  /**
  * This class constuctor creates objects of the class "CelestialBody" that represent the planets (and moons) in the solar system that we are looking at
  * Each object contains the co-ordinates of the given planet, the radius of the body and their masses.
  * @param size The size of the array containing the objects of CelestialBody (the planets)
  */
  public SolarSystem(int size, double scale) {
    super();
    this.size = size;
    this.scalingFactor = scale;
    this.planets = new CelestialBody[size];
    //Add planets with their specific coordinates scaled to the solar system
    this.planets[0] = new CelestialBody("Sun", -6.806783239281648e+08, 1.080005533878725e+09, 6.564012751690170e+06, -1.420511669610689e+01, -4.954714716629277e+00, 3.994237625449041e-01 , 1.988500e30, 695700000.0, scalePlanets(695700000.00), "assets/sun.jpg");
    this.planets[1] = new CelestialBody("Earth", -1.471922101663588e+11, -2.860995816266412e+10, 8.278183193596080e+06, 5.427193405797901e+03, -2.931056622265021e+04, 6.575428158157592e-01, 5.97219e24, 6371e3, scalePlanets(6371000.00), "assets/earth.jpg");
    this.planets[2] = new CelestialBody("Mercury", 6.047855986424127e+06, -6.801800047868888e+10, -5.702742359714534e+09, 3.892585189044652e+04, 2.978342247012996e+03, -3.327964151414740e+03, 3.302e23, 4879000.00, scalePlanets(4879000.00), "assets/mercury.jpeg");
    this.planets[3] = new CelestialBody("Venus", -9.435345478592035e+10, 5.350359551033670e+10, 6.131453014410347e+09, -1.726404287724406e+04, -3.073432518238123e+04, 5.741783385280979e-04, 4.8685e24, 12104000.00, scalePlanets(12104000.00), "assets/venus.jpeg");
    this.planets[4] = new CelestialBody("Moon", -1.472343904597218e+11, -2.822578361503422e+10, 1.052790970065631e+07, 4.433121605215677e+03, -2.948453614110320e+04, 8.896598225322805e+01, 7.349e22, 3475000.00, scalePlanets(3475000.00), "assets/moon.jpg");
    this.planets[5] = new CelestialBody("Mars", -3.615638921529161e+10, -2.167633037046744e+11, 3.687670305939779e+09, 2.481551975121696e+04, -1.816368005464070e+03, -6.467321619018108e+02, 6.4171e23, 6792000.0, scalePlanets(6792000.00), "assets/mars.jpeg");
    this.planets[6] = new CelestialBody("Jupiter", 1.781303138592153e+11, -7.551118436250277e+11, -8.532838524802327e+08, 1.255852555185220e+04, 3.622680192790968e+03, -2.958620380112444e+02, 1.89813e27, 142984000.0, scalePlanets(142984000.00), "assets/jupiter.jpeg");
    this.planets[7] = new CelestialBody("Saturn", 6.328646641500651e+11, -1.358172804527507e+12, - 1.578520137930810e+09,  8.220842186554890e+03,  4.052137378979608e+03, -3.976224719266916e+02, 5.6834e26, 120536000.0, scalePlanets(120536000.00), "assets/saturn.jpeg");
    this.planets[8] = new CelestialBody("Titan", 6.332873118527889e+11, -1.357175556995868e+12, -2.134637041453660e+09, 3.056877965721629e+03, 6.125612956428791e+03, -9.523587380845593e+02, 1.34553e23, 2575.5e3, scalePlanets(2575.5e3), "assets/titan.jpg");
    this.planets[9] = new CelestialBody("Uranus", 2.395195786685187e+12, 1.744450959214586e+12, - 2.455116324031639e+10, -4.059468635313243e+03, 5.187467354884825e+03, 7.182516236837899e+01, 8.6813e25, 51118000.0, scalePlanets(51118000.00), "assets/uranus.jpeg");
    this.planets[10] = new CelestialBody("Neptune", 4.382692942729203e+12, -9.093501655486243e+11,-8.227728929479486e+10, 1.068410720964204e+03, 5.354959501569486e+03, -1.343918199987533e+02, 1.02413e26, 49528000.0, scalePlanets(49528000.00), "assets/neptune.jpeg");
    this.planets[0].setScalingFactor(this.scalingFactor);
    init();
  }
  public void init() {
    // Creation of array that will contain masses of planets
    // and adding all the planets
    this.masses = new double[this.size];
    for(int i = 0; i < this.planets.length; i++) {
      this.masses[i] = this.planets[i].mass;
    }
    getChildren().addAll(this.planets);
  }
  //Update the planets in the solar system and their moons
  public void update(State state) {
    for(int i = 0; i < this.size; i++) {
      this.planets[i].update(state.positions[i], state.velocities[i]);
    }
  }

  public void show(State currentState) {
    for(int i = 0; i < this.size; i++) {
      this.planets[i].show(currentState.positions[i]);
    }
  }

  public double scalePlanets(double x){
    return (x/this.scalingFactor);
  }
  public double[] getMasses() {
    return this.masses;
  }

  //Graphics initialization
  public void initGraphics() {
    for(int i = 0; i < this.planets.length; i++) {
      this.planets[i].prepareTexture();
    }
  }
}
