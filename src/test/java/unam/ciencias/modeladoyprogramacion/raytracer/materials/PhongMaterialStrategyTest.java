package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.*;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Plane;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

/** Tests for PhongMaterialStrategy including refraction physics. */
class PhongMaterialStrategyTest {

  @Test
  void constructor_createsValidPhongMaterial() {
    PhongMaterialStrategy material =
        new PhongMaterialStrategy(
            "phong1",
            new Vector3D(1, 0, 0),
            0.8,
            0.5,
            32.0,
            0.3,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals("phong1", material.getId());
    assertEquals(0.8, material.getDiffuseCoefficient(), 1e-6);
    assertEquals(0.5, material.getSpecularCoefficient(), 1e-6);
    assertEquals(32.0, material.getSpecularHardness(), 1e-6);
    assertEquals(0.3, material.getReflectivity(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullId() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new PhongMaterialStrategy(
                null,
                new Vector3D(1, 0, 0),
                0.8,
                0.5,
                32.0,
                0.3,
                new Vector3D(0.1, 0.1, 0.1)));
  }

  @Test
  void constructor_throwsOnNullColor() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new PhongMaterialStrategy(
                "phong1", null, 0.8, 0.5, 32.0, 0.3, new Vector3D(0.1, 0.1, 0.1)));
  }

  @Test
  void constructor_throwsOnEmptyId() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new PhongMaterialStrategy(
                "", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.3, new Vector3D(0.1, 0.1, 0.1)));
  }

  @Test
  void constructor_clampsCoefficientsToValidRange() {
    // Test negative values get clamped to 0
    PhongMaterialStrategy material1 =
        new PhongMaterialStrategy(
            "test1",
            new Vector3D(1, 0, 0),
            -0.5,
            -0.3,
            10.0,
            -0.2,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals(0.0, material1.getDiffuseCoefficient(), 1e-6);
    assertEquals(0.0, material1.getSpecularCoefficient(), 1e-6);
    assertEquals(0.0, material1.getReflectivity(), 1e-6);

    // Test values > 1.0 get clamped to 1.0
    PhongMaterialStrategy material2 =
        new PhongMaterialStrategy(
            "test2",
            new Vector3D(1, 0, 0),
            1.5,
            2.0,
            10.0,
            1.8,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals(1.0, material2.getDiffuseCoefficient(), 1e-6);
    assertEquals(1.0, material2.getSpecularCoefficient(), 1e-6);
    assertEquals(1.0, material2.getReflectivity(), 1e-6);
  }

  @Test
  void constructor_withRefraction_createsValidMaterial() {
    PhongMaterialStrategy glass =
        new PhongMaterialStrategy(
            "glass",
            new Vector3D(0.9, 0.9, 0.9),
            0.2,
            0.8,
            64.0,
            0.1,
            0.9,
            1.5,
            new Vector3D(0.05, 0.05, 0.05));

    assertEquals("glass", glass.getId());
    assertEquals(0.9, glass.getTransparency(), 1e-6);
    assertEquals(1.5, glass.getRefractiveIndex(), 1e-6);
  }

  @Test
  void constructor_clampsTransparency() {
    PhongMaterialStrategy material1 =
        new PhongMaterialStrategy(
            "test",
            new Vector3D(1, 0, 0),
            0.8,
            0.5,
            32.0,
            0.3,
            -0.5,
            1.5,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals(0.0, material1.getTransparency(), 1e-6);

    PhongMaterialStrategy material2 =
        new PhongMaterialStrategy(
            "test",
            new Vector3D(1, 0, 0),
            0.8,
            0.5,
            32.0,
            0.3,
            1.8,
            1.5,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals(1.0, material2.getTransparency(), 1e-6);
  }

  @Test
  void constructor_clampsRefractiveIndex() {
    PhongMaterialStrategy material =
        new PhongMaterialStrategy(
            "test",
            new Vector3D(1, 0, 0),
            0.8,
            0.5,
            32.0,
            0.3,
            0.9,
            0.5,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals(1.0, material.getRefractiveIndex(), 1e-6);
  }

  @Test
  void getTransparency_defaultsToZero() {
    PhongMaterialStrategy material =
        new PhongMaterialStrategy(
            "phong1",
            new Vector3D(1, 0, 0),
            0.8,
            0.5,
            32.0,
            0.3,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals(0.0, material.getTransparency(), 1e-6);
  }

  @Test
  void getRefractiveIndex_defaultsToOne() {
    PhongMaterialStrategy material =
        new PhongMaterialStrategy(
            "phong1",
            new Vector3D(1, 0, 0),
            0.8,
            0.5,
            32.0,
            0.3,
            new Vector3D(0.1, 0.1, 0.1));
    assertEquals(1.0, material.getRefractiveIndex(), 1e-6);
  }

  @Test
  void scatter_returnsColorForBasicMaterial() {
    Material material = new Material("mat1", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    PhongMaterialStrategy strategy = (PhongMaterialStrategy) material.getStrategy();
    Vector3D color = strategy.scatter(ray, intersection, scene, 5);
    assertNotNull(color);
    assertTrue(color.getX() >= 0.0 && color.getX() <= 1.0);
    assertTrue(color.getY() >= 0.0 && color.getY() <= 1.0);
    assertTrue(color.getZ() >= 0.0 && color.getZ() <= 1.0);
  }

  @Test
  void scatter_handlesReflection() {
    Material material = new Material("reflective", new Vector3D(1, 1, 1), 0.1, 0.9, 64.0, 0.8);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Plane plane = new Plane("plane1", "reflective", new Vector3D(0, -1, 0), new Vector3D(0, 1, 0));
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 0));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(plane)
            .addLight(light)
            .backgroundColor(new Vector3D(0.2, 0.2, 0.2))
            .build();

    Ray ray = new Ray(new Vector3D(0, 5, 0), new Vector3D(0, -1, 0));
    Intersection intersection =
        new Intersection(6.0, new Vector3D(0, -1, 0), new Vector3D(0, 1, 0), plane);
    PhongMaterialStrategy strategy = (PhongMaterialStrategy) material.getStrategy();
    Vector3D color = strategy.scatter(ray, intersection, scene, 5);
    assertNotNull(color);
    assertTrue(color.getX() >= 0.0 && color.getX() <= 1.0);
  }

  @Test
  void scatter_stopsAtMaxDepth() {
    Material material = new Material("reflective", new Vector3D(1, 1, 1), 0.1, 0.9, 64.0, 0.8);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Plane plane = new Plane("plane1", "reflective", new Vector3D(0, -1, 0), new Vector3D(0, 1, 0));
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 0));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(plane)
            .addLight(light)
            .backgroundColor(new Vector3D(0.2, 0.2, 0.2))
            .build();

    Ray ray = new Ray(new Vector3D(0, 5, 0), new Vector3D(0, -1, 0));
    Intersection intersection =
        new Intersection(6.0, new Vector3D(0, -1, 0), new Vector3D(0, 1, 0), plane);
    PhongMaterialStrategy strategy = (PhongMaterialStrategy) material.getStrategy();
    Vector3D color = strategy.scatter(ray, intersection, scene, 0);
    assertNotNull(color);
  }

  @Test
  void scatter_handlesRefraction_enteringGlass() {
    PhongMaterialStrategy glass =
        new PhongMaterialStrategy(
            "glass",
            new Vector3D(0.9, 0.9, 0.9),
            0.2,
            0.8,
            64.0,
            0.1,
            0.9,
            1.5,
            new Vector3D(0.05, 0.05, 0.05));
    Material material = new Material("glass", glass);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "glass", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = glass.scatter(ray, intersection, scene, 5);
    assertNotNull(color);
  }

  @Test
  void scatter_handlesRefraction_exitingGlass() {
    PhongMaterialStrategy glass =
        new PhongMaterialStrategy(
            "glass",
            new Vector3D(0.9, 0.9, 0.9),
            0.2,
            0.8,
            64.0,
            0.1,
            0.9,
            1.5,
            new Vector3D(0.05, 0.05, 0.05));
    Material material = new Material("glass", glass);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "glass", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    // Ray exiting glass (from inside sphere)
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, 1));
    Intersection intersection =
        new Intersection(1.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, -1), sphere);
    Vector3D color = glass.scatter(ray, intersection, scene, 5);
    assertNotNull(color);
  }

  @Test
  void scatter_handlesTotalInternalReflection() {
    PhongMaterialStrategy glass =
        new PhongMaterialStrategy(
            "glass",
            new Vector3D(0.9, 0.9, 0.9),
            0.2,
            0.8,
            64.0,
            0.1,
            0.9,
            1.5,
            new Vector3D(0.05, 0.05, 0.05));
    Material material = new Material("glass", glass);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "glass", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    // Ray at grazing angle from inside glass
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0.9, 0.4, 0.0).normalize());
    Intersection intersection =
        new Intersection(1.0, new Vector3D(0.9, 0.4, 0.0), new Vector3D(-0.9, -0.4, 0.0), sphere);
    Vector3D color = glass.scatter(ray, intersection, scene, 5);
    assertNotNull(color);
  }

  @Test
  void scatter_withWater() {
    PhongMaterialStrategy water =
        new PhongMaterialStrategy(
            "water",
            new Vector3D(0.7, 0.8, 0.9),
            0.5,
            0.6,
            32.0,
            0.3,
            0.8,
            1.33,
            new Vector3D(0.1, 0.1, 0.1));
    Material material = new Material("water", water);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "water", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = water.scatter(ray, intersection, scene, 5);
    assertNotNull(color);
    assertEquals(1.33, water.getRefractiveIndex(), 1e-6);
  }

  @Test
  void scatter_withDiamond() {
    PhongMaterialStrategy diamond =
        new PhongMaterialStrategy(
            "diamond",
            new Vector3D(1, 1, 1),
            0.1,
            0.9,
            128.0,
            0.2,
            0.95,
            2.42,
            new Vector3D(0.02, 0.02, 0.02));
    Material material = new Material("diamond", diamond);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "diamond", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = diamond.scatter(ray, intersection, scene, 5);
    assertNotNull(color);
    assertEquals(2.42, diamond.getRefractiveIndex(), 1e-6);
  }

  @Test
  void scatter_clampsColorValues() {
    Material material =
        new Material("bright", new Vector3D(10, 10, 10), 1.0, 1.0, 32.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "bright", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(10, 10, 10), 10.0, new Vector3D(0, 0, 2));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    PhongMaterialStrategy strategy = (PhongMaterialStrategy) material.getStrategy();
    Vector3D color = strategy.scatter(ray, intersection, scene, 5);
    assertTrue(color.getX() <= 1.0);
    assertTrue(color.getY() <= 1.0);
    assertTrue(color.getZ() <= 1.0);
  }

  @Test
  void scatter_handlesZeroDepthWithRefraction() {
    PhongMaterialStrategy glass =
        new PhongMaterialStrategy(
            "glass",
            new Vector3D(0.9, 0.9, 0.9),
            0.2,
            0.8,
            64.0,
            0.1,
            0.9,
            1.5,
            new Vector3D(0.05, 0.05, 0.05));
    Material material = new Material("glass", glass);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "glass", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = glass.scatter(ray, intersection, scene, 0);
    assertNotNull(color);
  }

  @Test
  void getters_returnCorrectValues() {
    PhongMaterialStrategy material =
        new PhongMaterialStrategy(
            "test",
            new Vector3D(0.5, 0.5, 0.5),
            0.7,
            0.6,
            40.0,
            0.4,
            0.85,
            1.4,
            new Vector3D(0.15, 0.15, 0.15));

    assertEquals("test", material.getId());
    assertEquals(0.5, material.getColor().getX(), 1e-6);
    assertEquals(0.7, material.getDiffuseCoefficient(), 1e-6);
    assertEquals(0.6, material.getSpecularCoefficient(), 1e-6);
    assertEquals(40.0, material.getSpecularHardness(), 1e-6);
    assertEquals(0.4, material.getReflectivity(), 1e-6);
    assertEquals(0.85, material.getTransparency(), 1e-6);
    assertEquals(1.4, material.getRefractiveIndex(), 1e-6);
  }
}
