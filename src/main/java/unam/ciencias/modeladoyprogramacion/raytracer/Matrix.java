package unam.ciencias.modeladoyprogramacion.raytracer;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Esta clase representa una matriz 2D como un arreglo bidimensional aplanado.
 *
 * @param <T> Tipo de elemento
 * @author Cristopher Carrada
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Matrix<T> {
  private final int columns;
  private final int rows;
  private final ArrayList<ArrayList<T>> data;

  public Matrix(int rows, int columns) {
    this.columns = columns;
    this.rows = rows;
    this.data = new ArrayList<>(this.rows);
    initData();
  }

  public Matrix(T[][] array) {
    this.rows = array.length;
    this.columns = array[0].length;
    this.data = new ArrayList<>(this.rows);
    initData();
    forEach((row, col, value) -> this.setValue(row, col, array[row][col]));
  }

  private void initData() {
    for (int i = 0; i < this.rows; i++) {
      data.add(new ArrayList<>(this.columns));
      for (int j = 0; j < this.columns; j++) {
        data.get(i).add(null);
      }
    }
  }

  public Matrix(int width, int height, T[] flattenedMatrix) {
    this(height, width);
    forEach(
        (r, c, v) ->
            this.data.get(r).set(c, flattenedMatrix[getPositionInFlattenedMatrix(r, c)]));
  }

  private int getPositionInFlattenedMatrix(int row, int column) {
    return row * columns + column;
  }

  public void forEach(MatrixConsumer<T> consumer) {
    for (int r = 0; r < this.rows; r++) {
      for (int c = 0; c < this.columns; c++) {
        consumer.accept(r, c, this.getValue(r, c));
      }
    }
  }

  public T reduce(T initialValue, MatrixReducer<T> reducer) {
    T currentValue = initialValue;
    for (int r = 0; r < this.rows; r++) {
      for (int c = 0; c < this.columns; c++) {
        currentValue = reducer.reduce(r, c, currentValue, this.getValue(r, c));
      }
    }
    return currentValue;
  }

  public void setValue(int row, int column, T value) {
    data.get(row).set(column, value);
  }

  public T getValue(int row, int column) {
    return data.get(row).get(column);
  }

  public List<T> getRow(int row) {
    return data.get(row);
  }
}
