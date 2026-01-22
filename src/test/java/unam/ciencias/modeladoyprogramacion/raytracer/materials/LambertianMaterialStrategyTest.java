package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.*;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

/** Tests for LambertianMaterialStrategy. */
class LambertianMaterialStrategyTest {

  @Test
  void constructor_createsValidMaterial() {
    Vector3D color = new Vector3D(0.8, 0.2, 0.1);
    LambertianMaterialStrategy material =
        new LambertianMaterialStrategy(color, 0.7, new Vector3D(0.1, 0.1, 0.1));

    assertEquals(color, material.getColor());
    assertEquals(0.7, material.getDiffuseCoefficient(), 1e-9);
    assertEquals(0.0, material.getSpecularCoefficient(), 1e-9);
    assertEquals(0.0, material.getSpecularHardness(), 1e-9);
    assertEquals(0.0, material.getReflectivity(), 1e-9);
  }

  @Test
  void constructor_throwsOnNullColor() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new LambertianMaterialStrategy(null, 0.7, new Vector3D(0.1, 0.1, 0.1)));
  }

  @Test
  void constructor_clampsDiffuseCoefficient() {
    LambertianMaterialStrategy tooHigh =
        new LambertianMaterialStrategy(new Vector3D(1, 1, 1), 1.5, new Vector3D(0.1, 0.1, 0.1));
    assertEquals(1.0, tooHigh.getDiffuseCoefficient(), 1e-9);

    LambertianMaterialStrategy tooLow =
        new LambertianMaterialStrategy(new Vector3D(1, 1, 1), -0.5, new Vector3D(0.1, 0.1, 0.1));
    assertEquals(0.0, tooLow.getDiffuseCoefficient(), 1e-9);
  }

  @Test
  void constructor_usesDefaultAmbientWhenNull() {
    LambertianMaterialStrategy material =
        new LambertianMaterialStrategy(new Vector3D(1, 0, 0), 0.8, null);
    assertNotNull(material);
  }

  @Test
  void getters_returnCorrectValues() {
    Vector3D color = new Vector3D(0.5, 0.5, 0.5);
    LambertianMaterialStrategy material =
        new LambertianMaterialStrategy(color, 0.9, new Vector3D(0.2, 0.2, 0.2));

    assertEquals(color, material.getColor());
    assertEquals(0.9, material.getDiffuseCoefficient(), 1e-9);
    assertEquals(0.0, material.getSpecularCoefficient(), 1e-9);
    assertEquals(0.0, material.getSpecularHardness(), 1e-9);
    assertEquals(0.0, material.getReflectivity(), 1e-9);
  }

  @Test
  void scatter_returnsValidColor() {
    Vector3D color = new Vector3D(0.8, 0.2, 0.1);
    Vector3D ambient = new Vector3D(0.1, 0.1, 0.1);
    LambertianMaterialStrategy lambertian = new LambertianMaterialStrategy(color, 0.7, ambient);

    Material material = new Material("lambertian", color, 0.7, 0.0, 10.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "lambertian", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);

    Vector3D result = lambertian.scatter(ray, intersection, scene, 0);

    assertNotNull(result);
    assertTrue(result.getX() >= 0.0 && result.getX() <= 1.0);
    assertTrue(result.getY() >= 0.0 && result.getY() <= 1.0);
    assertTrue(result.getZ() >= 0.0 && result.getZ() <= 1.0);
  }

  @Test
  void scatter_clampsColorValues() {
    Vector3D color = new Vector3D(1.0, 1.0, 1.0);
    Vector3D ambient = new Vector3D(0.5, 0.5, 0.5);
    LambertianMaterialStrategy lambertian = new LambertianMaterialStrategy(color, 1.0, ambient);

    Material material = new Material("lambertian", color, 1.0, 0.0, 10.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "lambertian", new Vector3D(0, 0, 0), 1.0);
    PointLight light1 = new PointLight(new Vector3D(1, 1, 1), 2.0, new Vector3D(5, 5, 5));
    PointLight light2 = new PointLight(new Vector3D(1, 1, 1), 2.0, new Vector3D(-5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light1)
            .addLight(light2)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);

    Vector3D result = lambertian.scatter(ray, intersection, scene, 0);

    assertTrue(result.getX() <= 1.0);
    assertTrue(result.getY() <= 1.0);
    assertTrue(result.getZ() <= 1.0);
    assertTrue(result.getX() >= 0.0);
    assertTrue(result.getY() >= 0.0);
    assertTrue(result.getZ() >= 0.0);
  }

  @Test
  void scatter_handlesZeroDiffuseCoefficient() {
    Vector3D color = new Vector3D(1.0, 0.0, 0.0);
    Vector3D ambient = new Vector3D(0.2, 0.2, 0.2);
    LambertianMaterialStrategy lambertian = new LambertianMaterialStrategy(color, 0.0, ambient);

    Material material = new Material("lambertian", color, 0.0, 0.0, 10.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "lambertian", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);

    Vector3D result = lambertian.scatter(ray, intersection, scene, 0);

    assertNotNull(result);
    assertEquals(ambient.getX() * color.getX(), result.getX(), 0.01);
  }
}
