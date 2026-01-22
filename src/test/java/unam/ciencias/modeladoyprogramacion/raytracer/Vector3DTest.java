package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Vector3DTest {

  @Test
  void constructor_createsVector() {
    Vector3D v = new Vector3D(1.0, 2.0, 3.0);
    assertEquals(1.0, v.getX());
    assertEquals(2.0, v.getY());
    assertEquals(3.0, v.getZ());
  }

  @Test
  void add_sumsVectors() {
    Vector3D v1 = new Vector3D(1.0, 2.0, 3.0);
    Vector3D v2 = new Vector3D(4.0, 5.0, 6.0);
    Vector3D result = v1.add(v2);
    assertEquals(5.0, result.getX());
    assertEquals(7.0, result.getY());
    assertEquals(9.0, result.getZ());
  }

  @Test
  void subtract_subtractsVectors() {
    Vector3D v1 = new Vector3D(4.0, 5.0, 6.0);
    Vector3D v2 = new Vector3D(1.0, 2.0, 3.0);
    Vector3D result = v1.subtract(v2);
    assertEquals(3.0, result.getX());
    assertEquals(3.0, result.getY());
    assertEquals(3.0, result.getZ());
  }

  @Test
  void multiply_scalesVector() {
    Vector3D v = new Vector3D(1.0, 2.0, 3.0);
    Vector3D result = v.multiply(2.0);
    assertEquals(2.0, result.getX());
    assertEquals(4.0, result.getY());
    assertEquals(6.0, result.getZ());
  }

  @Test
  void dot_calculatesDotProduct() {
    Vector3D v1 = new Vector3D(1.0, 2.0, 3.0);
    Vector3D v2 = new Vector3D(4.0, 5.0, 6.0);
    double result = v1.dot(v2);
    assertEquals(32.0, result, 1e-6);
  }

  @Test
  void cross_calculatesCrossProduct() {
    Vector3D v1 = new Vector3D(1.0, 0.0, 0.0);
    Vector3D v2 = new Vector3D(0.0, 1.0, 0.0);
    Vector3D result = v1.cross(v2);
    assertEquals(0.0, result.getX(), 1e-6);
    assertEquals(0.0, result.getY(), 1e-6);
    assertEquals(1.0, result.getZ(), 1e-6);
  }

  @Test
  void magnitude_calculatesLength() {
    Vector3D v = new Vector3D(3.0, 4.0, 0.0);
    assertEquals(5.0, v.magnitude(), 1e-6);
  }

  @Test
  void normalize_createsUnitVector() {
    Vector3D v = new Vector3D(3.0, 4.0, 0.0);
    Vector3D normalized = v.normalize();
    assertEquals(1.0, normalized.magnitude(), 1e-6);
  }

  @Test
  void normalize_zeroVector_throws() {
    Vector3D v = new Vector3D(0.0, 0.0, 0.0);
    assertThrows(ArithmeticException.class, v::normalize);
  }

  @Test
  void negate_invertsDirection() {
    Vector3D v = new Vector3D(1.0, -2.0, 3.0);
    Vector3D negated = v.negate();
    assertEquals(-1.0, negated.getX());
    assertEquals(2.0, negated.getY());
    assertEquals(-3.0, negated.getZ());
  }

  @Test
  void getX_returnsXComponent() {
    Vector3D vec = new Vector3D(1.5, 2.5, 3.5);
    assertEquals(1.5, vec.getX(), 1e-6);
  }

  @Test
  void getY_returnsYComponent() {
    Vector3D vec = new Vector3D(1.5, 2.5, 3.5);
    assertEquals(2.5, vec.getY(), 1e-6);
  }

  @Test
  void getZ_returnsZComponent() {
    Vector3D vec = new Vector3D(1.5, 2.5, 3.5);
    assertEquals(3.5, vec.getZ(), 1e-6);
  }

  @Test
  void equals_returnsTrueForEqualVectors() {
    Vector3D vec1 = new Vector3D(1, 2, 3);
    Vector3D vec2 = new Vector3D(1, 2, 3);
    assertEquals(vec1, vec2);
  }

  @Test
  void equals_returnsFalseForDifferentVectors() {
    Vector3D vec1 = new Vector3D(1, 2, 3);
    Vector3D vec2 = new Vector3D(1, 2, 4);
    assertNotEquals(vec1, vec2);
  }

  @Test
  void hashCode_isConsistentForEqualVectors() {
    Vector3D vec1 = new Vector3D(1, 2, 3);
    Vector3D vec2 = new Vector3D(1, 2, 3);
    assertEquals(vec1.hashCode(), vec2.hashCode());
  }

  @Test
  void add_throwsOnNullVector() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> v.add(null));
  }

  @Test
  void subtract_throwsOnNullVector() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> v.subtract(null));
  }

  @Test
  void dot_throwsOnNullVector() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> v.dot(null));
  }

  @Test
  void cross_throwsOnNullVector() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> v.cross(null));
  }

  @Test
  void distance_throwsOnNullVector() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> v.distance(null));
  }

  @Test
  void equals_returnsFalseForNull() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertNotEquals(v, null);
  }

  @Test
  void equals_returnsFalseForDifferentClass() {
    Vector3D v = new Vector3D(1, 2, 3);
    assertNotEquals(v, "not a vector");
  }
}