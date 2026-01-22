package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;

class MatrixAdditionTest {

    @Test
    @DisplayName("sums two 2x2 matrices correctly")
    void test_sumMatrix() throws Exception {
        var matrixAddition = new MultiThreadedMatrixAddition<Integer>(1, (a, b) -> a + b);
        var matrixA = new Matrix<Integer>(new Integer[][] { { 1, 2 }, { 3, 4 } });
        var matrixB = new Matrix<Integer>(new Integer[][] { { 5, 6 }, { 7, 8 } });

        var actualResult = matrixAddition.add(matrixA, matrixB);

        var expectedResult = new Matrix<Integer>(new Integer[][] { { 6, 8 }, { 10, 12 } });

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("handles more threads than rows (work distribution)")
    void add_moreThreadsThanRows_producesCorrectResult() {
        var matrixAddition = new MultiThreadedMatrixAddition<Integer>(8, Integer::sum);
        var a = new Matrix<Integer>(new Integer[][] { { 1, 2, 3 }, { 4, 5, 6 } }); // 2 rows
        var b = new Matrix<Integer>(new Integer[][] { { 6, 5, 4 }, { 3, 2, 1 } });

        var actual = matrixAddition.add(a, b);
        var expected = new Matrix<Integer>(new Integer[][] { { 7, 7, 7 }, { 7, 7, 7 } });

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("handles non-divisible rows by threads (work distribution)")
    void add_nonDivisibleRowsByThreads_producesCorrectResult() {
        var matrixAddition = new MultiThreadedMatrixAddition<Integer>(3, Integer::sum);
        var a = new Matrix<Integer>(new Integer[][] {
            { 1, 2, 3 },
            { 4, 5, 6 },
            { 7, 8, 9 },
            { 10, 11, 12 }
        }); // 4 rows
        var b = new Matrix<Integer>(new Integer[][] {
            { 9, 8, 7 },
            { 6, 5, 4 },
            { 3, 2, 1 },
            { 0, -1, -2 }
        });

        var actual = matrixAddition.add(a, b);
        var expected = new Matrix<Integer>(new Integer[][] {
            { 10, 10, 10 },
            { 10, 10, 10 },
            { 10, 10, 10 },
            { 10, 10, 10 }
        });

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throws on mismatched dimensions (rows/columns)")
    void add_mismatchedDimensions_throws() {
        var matrixAddition = new MultiThreadedMatrixAddition<Integer>(2, Integer::sum);
        var a = new Matrix<Integer>(new Integer[][] { { 1, 2 }, { 3, 4 } }); // 2x2
        var b = new Matrix<Integer>(new Integer[][] { { 9 } }); // 1x1 (both fewer rows and cols)

        assertThrows(IllegalArgumentException.class,
                () -> matrixAddition.add(a, b));
    }

    @Test
    @DisplayName("supports empty matrices (0x0)")
    void add_emptyMatrices_returnsEmptyMatrix() {
        var matrixAddition = new MultiThreadedMatrixAddition<Integer>(4, Integer::sum);
        var a = new Matrix<Integer>(0, 0);
        var b = new Matrix<Integer>(0, 0);

        var actual = matrixAddition.add(a, b);
        var expected = new Matrix<Integer>(0, 0);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getRows()).isEqualTo(0);
        assertThat(actual.getColumns()).isEqualTo(0);
    }

    @Test
    @DisplayName("works with non-integer types (Double)")
    void add_doubleMatrices_works() {
        var add = new MultiThreadedMatrixAddition<Double>(3, (x, y) -> x + y);
        var a = new Matrix<Double>(new Double[][] { { 1.5, -2.0 }, { 0.25, 3.75 } });
        var b = new Matrix<Double>(new Double[][] { { 2.5, 2.0 }, { 0.75, 1.25 } });

        var actual = add.add(a, b);
        var expected = new Matrix<Double>(new Double[][] { { 4.0, 0.0 }, { 1.0, 5.0 } });

        assertThat(actual).isEqualTo(expected);
    }
}
