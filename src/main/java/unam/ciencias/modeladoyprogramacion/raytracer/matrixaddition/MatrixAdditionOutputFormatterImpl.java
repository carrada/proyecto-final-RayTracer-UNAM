package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;
import unam.ciencias.modeladoyprogramacion.raytracer.shared.CLIOutputFormatter;

/**
 * Implementación de CLIOutputFormatter para formatear la salida
 * de la suma de matrices.
 *
 * Ejemplos de salida formateada:
 * Para una matriz 2x2:
 * 1 2
 * 3 4
 *
 * @param <T> El tipo de los elementos de la matriz.
 */
public class MatrixAdditionOutputFormatterImpl<T>
    implements CLIOutputFormatter<Matrix<T>> {

    @Override
    public String format(Matrix<T> result) {
        int rows = result.getRows();
        int cols = result.getColumns();
        var stringBuilder = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                stringBuilder.append(result.getValue(i, j));
                if (j < cols - 1) {
                    stringBuilder.append(" ");
                }
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
