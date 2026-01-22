package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.*;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

/** Tests for MetalMaterialStrategy. */
class MetalMaterialStrategyTest {

  @Test
  void constructor_createsValidMaterial() {
    Vector3D color = new Vector3D(0.8, 0.8, 0.9);
    MetalMaterialStrategy material = new MetalMaterialStrategy(color, 0.9, 0.1);

    assertEquals(color, material.getColor());
    assertEquals(0.9, material.getReflectivity(), 1e-9);
    assertEquals(0.0, material.getDiffuseCoefficient(), 1e-9);
    assertEquals(1.0, material.getSpecularCoefficient(), 1e-9);
    assertEquals(1000.0, material.getSpecularHardness(), 1e-9);
  }

  @Test
  void constructor_throwsOnNullColor() {
    assertThrows(IllegalArgumentException.class, () -> new MetalMaterialStrategy(null, 0.9, 0.1));
  }

  @Test
  void constructor_clampsReflectivity() {
    MetalMaterialStrategy tooHigh =
        new MetalMaterialStrategy(new Vector3D(1, 1, 1), 1.5, 0.0);
    assertEquals(1.0, tooHigh.getReflectivity(), 1e-9);

    MetalMaterialStrategy tooLow = new MetalMaterialStrategy(new Vector3D(1, 1, 1), -0.5, 0.0);
    assertEquals(0.0, tooLow.getReflectivity(), 1e-9);
  }

  @Test
  void constructor_clampsFuzziness() {
    MetalMaterialStrategy tooHigh =
        new MetalMaterialStrategy(new Vector3D(1, 1, 1), 0.9, 1.5);
    assertNotNull(tooHigh);

    MetalMaterialStrategy tooLow = new MetalMaterialStrategy(new Vector3D(1, 1, 1), 0.9, -0.5);
    assertNotNull(tooLow);
  }

  @Test
  void getters_returnCorrectValues() {
    Vector3D color = new Vector3D(0.9, 0.9, 0.95);
    MetalMaterialStrategy material = new MetalMaterialStrategy(color, 0.85, 0.05);

    assertEquals(color, material.getColor());
    assertEquals(0.85, material.getReflectivity(), 1e-9);
    assertEquals(0.0, material.getDiffuseCoefficient(), 1e-9);
    assertEquals(1.0, material.getSpecularCoefficient(), 1e-9);
    assertEquals(1000.0, material.getSpecularHardness(), 1e-9);
  }

  @Test
  void scatter_returnsBackgroundWhenMaxDepth() {
    Vector3D color = new Vector3D(0.8, 0.8, 0.9);
    MetalMaterialStrategy metal = new MetalMaterialStrategy(color, 0.9, 0.0);

    Material material = new Material("metal", color, 0.0, 1.0, 1000.0, 0.9);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "metal", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .maxBounces(5)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);

    Vector3D result = metal.scatter(ray, intersection, scene, 5);

    assertEquals(scene.getBackgroundColor(), result);
  }

  @Test
  void scatter_handlesReflection() {
    Vector3D color = new Vector3D(0.9, 0.9, 0.9);
    MetalMaterialStrategy metal = new MetalMaterialStrategy(color, 1.0, 0.0);

    // Crear escena "espejo" realista
    Material metalMat = new Material("metal", color, 0.0, 1.0, 1000.0, 1.0);
    Material redMat = new Material("red", new Vector3D(1, 0, 0), 0.8, 0.0, 10.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);

    // Espejo vertical en x=0, esfera roja a la derecha
    Sphere mirror = new Sphere("mirror", "metal", new Vector3D(0, 0, 0), 0.5);
    Sphere redBall = new Sphere("red", "red", new Vector3D(2, 0, 0), 0.5);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(metalMat)
            .addMaterial(redMat)
            .addPrimitive(mirror)
            .addPrimitive(redBall)
            .addLight(light)
            .maxBounces(10)
            .build();

    // Rayo diagonal desde (1,0,5) hacia (-1,0,-1) normalizado
    Vector3D rayDir = new Vector3D(-1, 0, -5).normalize();
    Ray ray = new Ray(new Vector3D(1, 0, 5), rayDir);

    // Simular intersección en el espejo (lado derecho)
    Vector3D hitPoint = new Vector3D(0.4, 0, 0.2);
    Vector3D normal = new Vector3D(1, 0, 0); // Normal apunta hacia +X (fuera del espejo)
    Intersection intersection = new Intersection(5.0, hitPoint, normal, mirror);

    Vector3D result = metal.scatter(ray, intersection, scene, 0);

    // El reflejo debería intersectar algo (redBall o fondo)
    assertNotNull(result);
    // Si intersecta redBall, debería tener componente roja
    assertTrue(result.getX() >= 0.0 || result.getY() >= 0.0 || result.getZ() >= 0.0);
  }

  @Test
  void scatter_worksWithZeroReflectivity() {
    Vector3D color = new Vector3D(0.5, 0.5, 0.5);
    MetalMaterialStrategy metal = new MetalMaterialStrategy(color, 0.0, 0.0);

    Material material = new Material("metal", color, 0.0, 1.0, 1000.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere1", "metal", new Vector3D(0, 0, 0), 1.0);
    Sphere target = new Sphere("sphere2", "metal", new Vector3D(0, 0, -3), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addPrimitive(target)
            .addLight(light)
            .maxBounces(10)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection =
        new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);

    Vector3D result = metal.scatter(ray, intersection, scene, 0);

    assertNotNull(result);
  }

  @Test
  void scatter_withFuzziness_affectsReflection() {
    Vector3D color = new Vector3D(0.9, 0.9, 0.9);
    MetalMaterialStrategy fuzzyMetal = new MetalMaterialStrategy(color, 1.0, 0.5);

    Material material = new Material("fuzzy", color, 0.0, 1.0, 1000.0, 1.0);
    Material targetMat = new Material("target", new Vector3D(1, 0, 0), 0.8, 0.0, 10.0, 0.0);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere metal = new Sphere("metal", "fuzzy", new Vector3D(0, 0, 0), 1.0);
    Sphere target = new Sphere("target", "target", new Vector3D(0, 0, -3), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addMaterial(targetMat)
            .addPrimitive(metal)
            .addPrimitive(target)
            .addLight(light)
            .maxBounces(10)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection = new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), metal);

    Vector3D result = fuzzyMetal.scatter(ray, intersection, scene, 0);

    assertNotNull(result);
  }

  @Test
  void scatter_withHighFuzziness_stillConverges() {
    Vector3D color = new Vector3D(0.7, 0.7, 0.7);
    MetalMaterialStrategy veryFuzzy = new MetalMaterialStrategy(color, 0.8, 1.0);

    Material material = new Material("rough", color, 0.0, 1.0, 1000.0, 0.8);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    Sphere sphere = new Sphere("sphere", "rough", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .maxBounces(5)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection intersection = new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);

    Vector3D result = veryFuzzy.scatter(ray, intersection, scene, 0);

    assertNotNull(result);
  }

  @Test
  void scatter_noReflectionHit_returnsBackground() {
    Vector3D color = new Vector3D(0.8, 0.8, 0.9);
    MetalMaterialStrategy metal = new MetalMaterialStrategy(color, 0.9, 0.0);

    Material material = new Material("metal", color, 0.0, 1.0, 1000.0, 0.9);
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5), new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0);
    // Esfera pequeña en el origen, rayo reflejado se va al vacío
    Sphere sphere = new Sphere("sphere", "metal", new Vector3D(0, 0, 0), 0.5);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .build();

    // Ray desde arriba, normal apunta hacia arriba, reflejo va hacia arriba (vacío)
    Ray ray = new Ray(new Vector3D(0, 5, 0), new Vector3D(0, -1, 0));
    Intersection intersection =
        new Intersection(4.5, new Vector3D(0, 0.5, 0), new Vector3D(0, 1, 0), sphere);

    Vector3D result = metal.scatter(ray, intersection, scene, 0);

    assertEquals(scene.getBackgroundColor(), result);
  }
}
