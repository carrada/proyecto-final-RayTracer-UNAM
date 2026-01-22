package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;

class MatrixAdditionInputReaderImplTest {

    private static ByteArrayInputStream inputOf(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("reads two matrices separated by blank lines (2x3 example)")
    void read_validInput_parsesMatrices() throws Exception {
        String input = """
                2 3
                
                8 9 11
                24 -6 19
                
                14 5 16
                -5 15 -4
                """;

        var reader = new MatrixAdditionInputReaderImpl();
        var actualInput = reader.read(inputOf(input));

        var matrixA = new Matrix<Integer>(new Integer[][] {
                { 8, 9, 11 },
                { 24, -6, 19 }
        });
        var matrixB = new Matrix<Integer>(new Integer[][] {
                { 14, 5, 16 },
                { -5, 15, -4 }
        });
        var expectedInput = new MatrixAdditionInput(matrixA, matrixB);

        assertThat(actualInput).isEqualTo(expectedInput);
    }

    @Test
    @DisplayName("trims whitespace and supports negative numbers")
    void read_handlesWhitespaceAndNegatives() {
        String input = """
                  3   2 \s
                
                  1   -2
                  0    5
                 -10  11
                
                  4   4
                 -3  -7
                  9   0
                """;

        var reader = new MatrixAdditionInputReaderImpl();
        var actualInput = reader.read(inputOf(input));

        var expectedA = new Integer[][] {
            { 1, -2 }, { 0, 5 }, { -10, 11 }
        };
        var expectedB = new Integer[][] {
            { 4, 4 }, { -3, -7 }, { 9, 0 }
        };
        var expectedInput = new MatrixAdditionInput(
                new Matrix<>(expectedA),
                new Matrix<>(expectedB)
        );

        assertThat(actualInput).isEqualTo(expectedInput);
    }

    @Test
    @DisplayName("throws RuntimeException when input is incomplete/malformed")
    void read_incompleteInput_throws() {
        // Declares a 2x2 but provides only one row for the first matrix
        String input = """
                2 2
                
                1 2
                
                5 6
                7 8
                """;

        var reader = new MatrixAdditionInputReaderImpl();

        assertThrows(RuntimeException.class, () -> reader.read(inputOf(input)));
    }
}
