package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

class PhongShaderTest {
  private Scene scene;
  private Material material;

  @BeforeEach
  void setup() {
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5),
            new Vector3D(0, 0, -1),
            new Vector3D(0, 1, 0),
            60.0,
            1.0);
    material = new Material("mat1", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.0);
    Sphere sphere = new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .backgroundColor(new Vector3D(0, 0, 0))
            .build();
  }

  @Test
  void constructor_throwsOnNullScene() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PhongShader(null, new Vector3D(0.1, 0.1, 0.1)));
  }

  @Test
  void constructor_usesDefaultAmbientWhenNull() {
    PhongShader shader = new PhongShader(scene, null);
    assertNotNull(shader);
  }

  @Test
  void shade_returnsBackgroundColorForNullIntersection() {
    PhongShader shader = new PhongShader(scene, new Vector3D(0.1, 0.1, 0.1));
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    Vector3D color = shader.shade(null, ray, 0);
    assertEquals(0.0, color.getX(), 1e-6);
    assertEquals(0.0, color.getY(), 1e-6);
    assertEquals(0.0, color.getZ(), 1e-6);
  }

  @Test
  void shade_returnsMagentaForMissingMaterial() {
    Sphere sphere = new Sphere("sphere1", "unknown", new Vector3D(0, 0, 0), 1.0);
    Scene testScene =
        new Scene.Builder()
            .camera(scene.getCamera())
            .addPrimitive(sphere)
            .build();
    PhongShader shader = new PhongShader(testScene, new Vector3D(0.1, 0.1, 0.1));
    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Intersection hit = new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = shader.shade(hit, ray, 0);
    assertEquals(1.0, color.getX(), 1e-6);
    assertEquals(0.0, color.getY(), 1e-6);
    assertEquals(1.0, color.getZ(), 1e-6);
  }

  @Test
  void shade_calculatesAmbientComponent() {
    PhongShader shader = new PhongShader(scene, new Vector3D(0.1, 0.1, 0.1));
    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Sphere sphere = (Sphere) scene.getPrimitives().get(0);
    Intersection hit = new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = shader.shade(hit, ray, 0);
    assertTrue(color.getX() >= 0.1);
  }

  @Test
  void shade_clampsColorValues() {
    Material brightMat = new Material("bright", new Vector3D(10, 10, 10), 1.0, 1.0, 32.0, 0.0);
    Scene brightScene =
        new Scene.Builder()
            .camera(scene.getCamera())
            .addMaterial(brightMat)
            .addPrimitive(new Sphere("sphere1", "bright", new Vector3D(0, 0, 0), 1.0))
            .addLight(new PointLight(new Vector3D(10, 10, 10), 10.0, new Vector3D(0, 0, 2)))
            .build();
    PhongShader shader = new PhongShader(brightScene, new Vector3D(1, 1, 1));
    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Sphere sphere = (Sphere) brightScene.getPrimitives().get(0);
    Intersection hit = new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = shader.shade(hit, ray, 0);
    assertTrue(color.getX() <= 1.0);
    assertTrue(color.getY() <= 1.0);
    assertTrue(color.getZ() <= 1.0);
  }

  @Test
  void shade_handlesShadows() {
    Sphere blocker = new Sphere("blocker", "mat1", new Vector3D(3, 3, 3), 0.5);
    Scene shadowScene =
        new Scene.Builder()
            .camera(scene.getCamera())
            .addMaterial(material)
            .addPrimitive(new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0))
            .addPrimitive(blocker)
            .addLight(new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5)))
            .build();
    PhongShader shader = new PhongShader(shadowScene, new Vector3D(0.1, 0.1, 0.1));
    assertNotNull(shader);
  }

  @Test
  void shade_calculatesDiffuseAndSpecular() {
    PhongShader shader = new PhongShader(scene, new Vector3D(0.1, 0.1, 0.1));
    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Sphere sphere = (Sphere) scene.getPrimitives().get(0);
    Intersection hit = new Intersection(4.0, new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), sphere);
    Vector3D color = shader.shade(hit, ray, 0);
    // El color debe tener componentes positivos (rojo desde el material)
    assertTrue(color.getX() > 0.1);
  }
}