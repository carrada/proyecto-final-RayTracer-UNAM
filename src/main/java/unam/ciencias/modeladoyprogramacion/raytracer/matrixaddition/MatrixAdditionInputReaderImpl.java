package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;
import unam.ciencias.modeladoyprogramacion.raytracer.shared.CLIInputReader;

/**
 * Implementación de CLIInputReader para leer entradas de suma de matrices.
 * El formato de entrada esperado es:
 * - Primera línea: dos enteros que representan el número de filas y columnas.
 * - Siguientes 'filas' líneas: elementos de la primera matriz.
 * - Una línea en blanco.
 * - Siguientes 'filas' líneas: elementos de la segunda matriz.
 */
public class MatrixAdditionInputReaderImpl
    implements CLIInputReader<MatrixAdditionInput> {

    @Override
    public MatrixAdditionInput read(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            // Read matrix dimensions
            String line = br.readLine();
            String[] dims = line.trim().split("\\s+");
            Integer rows = Integer.parseInt(dims[0]),
                    columns = Integer.parseInt(dims[1]);

            // read empty line
            line = br.readLine();

            // Read first matrix
            var matrixA = readMatrix(br, rows, columns);

            // read empty line
            line = br.readLine();

            // Read second matrix
            var matrixB = readMatrix(br, rows, columns);

            return new MatrixAdditionInput(matrixA, matrixB);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Matrix<Integer> readMatrix(BufferedReader br,
                                       Integer rows,
                                       Integer columns)
        throws IOException {
        String line;
        var matrix = new Integer[rows][columns];
        for (int row = 0; row < rows; row++) {
            line = br.readLine();
            String[] values = line.trim().split("\\s+");
            for (int col = 0; col < columns; col++) {
                matrix[row][col] = Integer.parseInt(values[col]);
            }
        }
        return new Matrix<>(matrix);
    }
}
