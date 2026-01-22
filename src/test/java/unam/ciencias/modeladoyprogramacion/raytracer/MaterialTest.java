package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.materials.LambertianMaterialStrategy;

class MaterialTest {

  @Test
  void constructor_createsValidMaterial() {
    Material mat = new Material("mat1", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.2);
    assertEquals("mat1", mat.getId());
    assertEquals(1.0, mat.getColor().getX(), 1e-6);
    assertEquals(0.8, mat.getDiffuseCoefficient(), 1e-6);
    assertEquals(0.5, mat.getSpecularCoefficient(), 1e-6);
    assertEquals(32.0, mat.getSpecularHardness(), 1e-6);
    assertEquals(0.2, mat.getReflectivity(), 1e-6);
  }

  @Test
  void constructor_clampsCoefficientsToValidRange() {
    Material mat = new Material("mat1", new Vector3D(1, 0, 0), 1.5, -0.5, 32.0, 0.2);
    assertEquals(1.0, mat.getDiffuseCoefficient(), 1e-6);
    assertEquals(0.0, mat.getSpecularCoefficient(), 1e-6);
  }

  @Test
  void constructor_clampsReflectivityToValidRange() {
    Material mat = new Material("mat1", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 1.5);
    assertEquals(1.0, mat.getReflectivity(), 1e-6);
  }

  @Test
  void constructor_ensuresMinimumSpecularHardness() {
    Material mat = new Material("mat1", new Vector3D(1, 0, 0), 0.8, 0.5, 0.5, 0.2);
    assertEquals(1.0, mat.getSpecularHardness(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Material(null, new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.2));
  }

  @Test
  void constructor_throwsOnEmptyId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Material("", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.2));
  }

  @Test
  void constructor_throwsOnNullColor() {
    assertThrows(
        IllegalArgumentException.class, () -> new Material("mat1", null, 0.8, 0.5, 32.0, 0.2));
  }

  @Test
  void getId_returnsCorrectId() {
    Material mat = new Material("testMat", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.2);
    assertEquals("testMat", mat.getId());
  }

  @Test
  void getColor_returnsCorrectColor() {
    Vector3D color = new Vector3D(0.5, 0.3, 0.1);
    Material mat = new Material("mat1", color, 0.8, 0.5, 32.0, 0.2);
    assertEquals(0.5, mat.getColor().getX(), 1e-6);
    assertEquals(0.3, mat.getColor().getY(), 1e-6);
    assertEquals(0.1, mat.getColor().getZ(), 1e-6);
  }

  @Test
  void strategyConstructor_createsValidMaterial() {
    LambertianMaterialStrategy strategy =
        new LambertianMaterialStrategy(new Vector3D(1, 0, 0), 0.8, null);
    Material mat = new Material("mat1", strategy);
    assertEquals("mat1", mat.getId());
    assertEquals(strategy, mat.getStrategy());
  }

  @Test
  void strategyConstructor_throwsOnNullId() {
    LambertianMaterialStrategy strategy =
        new LambertianMaterialStrategy(new Vector3D(1, 0, 0), 0.8, null);
    assertThrows(IllegalArgumentException.class, () -> new Material(null, strategy));
  }

  @Test
  void strategyConstructor_throwsOnEmptyId() {
    LambertianMaterialStrategy strategy =
        new LambertianMaterialStrategy(new Vector3D(1, 0, 0), 0.8, null);
    assertThrows(IllegalArgumentException.class, () -> new Material("", strategy));
    assertThrows(IllegalArgumentException.class, () -> new Material("   ", strategy));
  }

  @Test
  void strategyConstructor_throwsOnNullStrategy() {
    assertThrows(IllegalArgumentException.class, () -> new Material("mat1", null));
  }

  @Test
  void toString_returnsFormattedString() {
    Material mat = new Material("testId", new Vector3D(0.5, 0.3, 0.1), 0.8, 0.5, 32.0, 0.2);
    String result = mat.toString();
    assertTrue(result.contains("testId"));
    assertTrue(result.contains("0.80"));
    assertTrue(result.contains("0.50"));
  }
}