package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;
import unam.ciencias.modeladoyprogramacion.raytracer.MultiThreadedOperation;

import static java.lang.Math.min;

/**
 * MultiThreadedMatrixAddition es una clase que implementa la interfaz MatrixAddition
 * usando múltiples hilos para realizar la operación de suma.
 *
 * @param <T> El tipo de los elementos en las matrices.
 */
public class MultiThreadedMatrixAddition<T>
    extends MultiThreadedOperation
    implements MatrixAddition<T> {

    private BiFunction<T,T,T> addition;

    public MultiThreadedMatrixAddition(BiFunction<T,T,T> addition) {
        super();
        this.addition = addition;
    }

    public MultiThreadedMatrixAddition(int threads, BiFunction<T,T,T> addition) {
        super(threads);
        this.addition = addition;
    }

    @Override
    public Matrix<T> add(Matrix<T> matrixA, Matrix<T> matrixB) {
        if (matrixA.getRows() != matrixB.getRows()
            || matrixA.getColumns() != matrixB.getColumns()) {
          throw new IllegalArgumentException("Matrices must have the same dimensions to be added");
        }
        try {
            var result = new Matrix<T>(matrixA.getRows(), matrixA.getColumns());
            var threads = initializeThreads(matrixA, matrixB, result);
            this.runAndWaitForThreads(threads);
            return result;
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Thread> initializeThreads(Matrix<T> matrixA,
                                           Matrix<T> matrixB,
                                           Matrix<T> result) {
      if (result.getRows() == 0) {
        return Collections.emptyList();
      }
      final int totalRows = matrixA.getRows();
      int threadsToUse = min(this.threads, totalRows);
      int remainingRows = totalRows % threadsToUse;
      int chunkSize = totalRows / threadsToUse  + (remainingRows > 0 ? 1 : 0);

      return IntStream.range(0, threadsToUse)
        .mapToObj(i -> {
          int initialRow = i * chunkSize;
          int lastRow = min((i+1) * chunkSize, totalRows);
          Runnable task = () -> {
              for(int row = initialRow; row < lastRow; row++) {
                  for (int column = 0; column < matrixA.getColumns(); column++) {
                      var add = addition.apply(matrixA.getValue(row, column),
                                               matrixB.getValue(row, column));
                      result.setValue(row, column, add);
                  }
              }
          };
          return new Thread(task);
        }).toList();
    }
}
