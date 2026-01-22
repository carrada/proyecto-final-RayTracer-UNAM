package unam.ciencias.modeladoyprogramacion.raytracer.utils;

import java.util.Random;

import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;

public class MatrixUtilsTestHelper {
  public static Float fillMatrixAndReturnMinimumValue(Matrix<Float> result) {
    float min = Float.MAX_VALUE;
    Random random = new Random();
    for (int r = 0; r < result.getRows(); r++) {
      for (int c = 0; c < result.getColumns(); c++) {
        result.setValue(r, c, random.nextFloat());
        min = Math.min(min, result.getValue(r, c));
      }
    }
    return min;
  }
}
