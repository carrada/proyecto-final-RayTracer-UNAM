package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Tests unitarios para AmbientCalculator.
 *
 * @author Cristopher Carrada
 */
class AmbientCalculatorTest {
  private AmbientCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new AmbientCalculator();
  }

  @Test
  void calculate_withValidInputs_returnsComponentWiseProduct() {
    Vector3D ambientLight = new Vector3D(0.2, 0.3, 0.4);
    Vector3D materialColor = new Vector3D(1.0, 0.5, 0.25);

    Vector3D result = calculator.calculate(ambientLight, materialColor);

    assertEquals(0.2, result.getX(), 0.001);
    assertEquals(0.15, result.getY(), 0.001);
    assertEquals(0.1, result.getZ(), 0.001);
  }

  @Test
  void calculate_withZeroAmbientLight_returnsBlack() {
    Vector3D ambientLight = new Vector3D(0, 0, 0);
    Vector3D materialColor = new Vector3D(1.0, 1.0, 1.0);

    Vector3D result = calculator.calculate(ambientLight, materialColor);

    assertEquals(0.0, result.getX(), 0.001);
    assertEquals(0.0, result.getY(), 0.001);
    assertEquals(0.0, result.getZ(), 0.001);
  }

  @Test
  void calculate_withNullAmbientLight_throwsException() {
    Vector3D materialColor = new Vector3D(1.0, 1.0, 1.0);

    assertThrows(
        IllegalArgumentException.class, () -> calculator.calculate(null, materialColor));
  }

  @Test
  void calculate_withNullMaterialColor_throwsException() {
    Vector3D ambientLight = new Vector3D(0.1, 0.1, 0.1);

    assertThrows(
        IllegalArgumentException.class, () -> calculator.calculate(ambientLight, null));
  }

  @Test
  void calculate_withBothNull_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null, null));
  }

  @Test
  void calculate_withWhiteLight_returnsMaterialColor() {
    Vector3D ambientLight = new Vector3D(1.0, 1.0, 1.0);
    Vector3D materialColor = new Vector3D(0.8, 0.2, 0.5);

    Vector3D result = calculator.calculate(ambientLight, materialColor);

    assertEquals(0.8, result.getX(), 0.001);
    assertEquals(0.2, result.getY(), 0.001);
    assertEquals(0.5, result.getZ(), 0.001);
  }

  @Test
  void calculate_isCommutative() {
    Vector3D v1 = new Vector3D(0.2, 0.3, 0.4);
    Vector3D v2 = new Vector3D(0.5, 0.6, 0.7);

    Vector3D result1 = calculator.calculate(v1, v2);
    Vector3D result2 = calculator.calculate(v2, v1);

    assertEquals(result1.getX(), result2.getX(), 0.001);
    assertEquals(result1.getY(), result2.getY(), 0.001);
    assertEquals(result1.getZ(), result2.getZ(), 0.001);
  }
}
