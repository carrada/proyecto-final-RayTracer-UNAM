package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Matrix3x3Test {

  @Test
  void identity_createsIdentityMatrix() {
    Matrix3x3 identity = Matrix3x3.identity();
    assertEquals(1.0, identity.get(0, 0));
    assertEquals(0.0, identity.get(0, 1));
    assertEquals(1.0, identity.get(1, 1));
    assertEquals(1.0, identity.get(2, 2));
  }

  @Test
  void add_sumsMatrices() {
    Matrix3x3 m1 = new Matrix3x3(new double[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
    Matrix3x3 m2 = new Matrix3x3(new double[][] {{9, 8, 7}, {6, 5, 4}, {3, 2, 1}});
    Matrix3x3 result = m1.add(m2);
    assertEquals(10.0, result.get(0, 0));
    assertEquals(10.0, result.get(1, 1));
    assertEquals(10.0, result.get(2, 2));
  }

  @Test
  void multiply_multipliesMatrices() {
    Matrix3x3 m1 = Matrix3x3.identity();
    Matrix3x3 m2 = new Matrix3x3(new double[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
    Matrix3x3 result = m1.multiply(m2);
    assertEquals(m2.get(0, 0), result.get(0, 0));
    assertEquals(m2.get(1, 1), result.get(1, 1));
    assertEquals(m2.get(2, 2), result.get(2, 2));
  }

  @Test
  void multiply_multipliesMatrixByVector() {
    Matrix3x3 m = new Matrix3x3(new double[][] {{1, 0, 0}, {0, 2, 0}, {0, 0, 3}});
    Vector3D v = new Vector3D(1, 2, 3);
    Vector3D result = m.multiply(v);
    assertEquals(1.0, result.getX());
    assertEquals(4.0, result.getY());
    assertEquals(9.0, result.getZ());
  }

  @Test
  void rotationX_createsRotationMatrix() {
    Matrix3x3 rotation = Matrix3x3.rotationX(Math.PI / 2);
    Vector3D v = new Vector3D(1, 0, 0);
    Vector3D rotated = rotation.multiply(v);
    assertEquals(1.0, rotated.getX(), 1e-6);
    assertEquals(0.0, rotated.getY(), 1e-6);
    assertEquals(0.0, rotated.getZ(), 1e-6);
  }

  @Test
  void constructor_createsMatrixWithValues() {
    Matrix3x3 mat = new Matrix3x3(new double[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
    assertEquals(1.0, mat.get(0, 0), 1e-6);
    assertEquals(5.0, mat.get(1, 1), 1e-6);
    assertEquals(9.0, mat.get(2, 2), 1e-6);
  }

  @Test
  void rotationY_createsRotationMatrix() {
    Matrix3x3 rot = Matrix3x3.rotationY(Math.PI / 2);
    assertNotNull(rot);
    assertEquals(0.0, rot.get(0, 0), 1e-6);
  }

  @Test
  void rotationZ_createsRotationMatrix() {
    Matrix3x3 rot = Matrix3x3.rotationZ(Math.PI / 2);
    assertNotNull(rot);
    assertEquals(0.0, rot.get(0, 0), 1e-6);
  }
}