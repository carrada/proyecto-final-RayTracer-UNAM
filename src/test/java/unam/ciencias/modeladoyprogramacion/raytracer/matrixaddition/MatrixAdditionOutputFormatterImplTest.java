package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;

class MatrixAdditionOutputFormatterImplTest {

  @Test
  @DisplayName("formats 2x3 integer matrix as space-separated rows with trailing newlines")
  void format_formatsRectangularMatrix() {
    var result = new Matrix<Integer>(
        new Integer[][] {
          {22, 14, 27},
          {19, 9, 15}
        }
    );

    var formatter = new MatrixAdditionOutputFormatterImpl<Integer>();

    var formatted = formatter.format(result);

    assertThat(formatted).isEqualTo("22 14 27\n19 9 15\n");
  }

  @Test
  @DisplayName("formats 1x1 matrix with a newline at the end")
  void format_singleElementMatrix() {
    var result = new Matrix<Integer>(
        new Integer[][] {
          {42}
        }
    );

    var formatter = new MatrixAdditionOutputFormatterImpl<Integer>();

    var formatted = formatter.format(result);

    assertThat(formatted).isEqualTo("42\n");
  }

  @Test
  @DisplayName("handles negative numbers and preserves exact spacing/newlines")
  void format_handlesNegativesAndSpacing() {
    var result = new Matrix<Integer>(
        new Integer[][] {
          {1, -2},
          {0, 5},
          {-10, 11}
        }
    );

    var formatter = new MatrixAdditionOutputFormatterImpl<Integer>();

    var formatted = formatter.format(result);

    assertThat(formatted).isEqualTo(
        """
        1 -2
        0 5
        -10 11
        """);
  }
}
