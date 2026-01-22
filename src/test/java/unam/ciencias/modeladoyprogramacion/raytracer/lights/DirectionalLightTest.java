package unam.ciencias.modeladoyprogramacion.raytracer.lights;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Tests para DirectionalLight.
 *
 * @author Cristopher Carrada
 */
class DirectionalLightTest {

  @Test
  @DisplayName("constructor crea luz direccional válida")
  void constructor_createsValidDirectionalLight() {
    Vector3D color = new Vector3D(1, 1, 1);
    Vector3D direction = new Vector3D(0, -1, 0);

    DirectionalLight light = new DirectionalLight(color, 1.0, direction);

    assertThat(light.getColor()).isEqualTo(color);
    assertThat(light.getIntensity()).isEqualTo(1.0);
    assertThat(light.getDirection()).isEqualTo(direction.normalize());
  }

  @Test
  @DisplayName("constructor normaliza la dirección automáticamente")
  void constructor_normalizesDirection() {
    Vector3D color = new Vector3D(1, 1, 1);
    Vector3D direction = new Vector3D(0, -10, 0); // No normalizado

    DirectionalLight light = new DirectionalLight(color, 1.0, direction);

    assertThat(light.getDirection()).isEqualTo(new Vector3D(0, -1, 0));
  }

  @Test
  @DisplayName("constructor lanza excepción si color es null")
  void constructor_throwsOnNullColor() {
    assertThatThrownBy(() -> new DirectionalLight(null, 1.0, new Vector3D(0, -1, 0)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Color cannot be null");
  }

  @Test
  @DisplayName("constructor lanza excepción si dirección es null")
  void constructor_throwsOnNullDirection() {
    assertThatThrownBy(() -> new DirectionalLight(new Vector3D(1, 1, 1), 1.0, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Direction cannot be null");
  }

  @Test
  @DisplayName("constructor lanza excepción si intensidad es negativa")
  void constructor_throwsOnNegativeIntensity() {
    assertThatThrownBy(
            () -> new DirectionalLight(new Vector3D(1, 1, 1), -1.0, new Vector3D(0, -1, 0)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Intensity must be non-negative");
  }

  @Test
  @DisplayName("getPosition devuelve null para luz direccional")
  void getPosition_returnsNull() {
    DirectionalLight light =
        new DirectionalLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, -1, 0));

    assertThat(light.getPosition()).isNull();
  }

  @Test
  @DisplayName("getDirectionFrom devuelve dirección constante desde cualquier punto")
  void getDirectionFrom_returnsConstantDirection() {
    DirectionalLight light =
        new DirectionalLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, -1, 0));

    Vector3D point1 = new Vector3D(0, 0, 0);
    Vector3D point2 = new Vector3D(10, 5, -3);
    Vector3D point3 = new Vector3D(-5, 2, 8);

    // La dirección debe ser la misma desde cualquier punto
    Vector3D dir1 = light.getDirectionFrom(point1);
    Vector3D dir2 = light.getDirectionFrom(point2);
    Vector3D dir3 = light.getDirectionFrom(point3);

    // Comparar componentes individualmente para evitar problemas con -0.0 vs 0.0
    assertThat(dir1.getX()).isCloseTo(dir2.getX(), org.assertj.core.api.Assertions.within(1e-10));
    assertThat(dir1.getY()).isCloseTo(dir2.getY(), org.assertj.core.api.Assertions.within(1e-10));
    assertThat(dir1.getZ()).isCloseTo(dir2.getZ(), org.assertj.core.api.Assertions.within(1e-10));
    
    assertThat(dir2.getX()).isCloseTo(dir3.getX(), org.assertj.core.api.Assertions.within(1e-10));
    assertThat(dir2.getY()).isCloseTo(dir3.getY(), org.assertj.core.api.Assertions.within(1e-10));
    assertThat(dir2.getZ()).isCloseTo(dir3.getZ(), org.assertj.core.api.Assertions.within(1e-10));
    
    // Debe apuntar hacia la fuente (negación de la dirección de los rayos)
    assertThat(dir1.getY()).isCloseTo(1.0, org.assertj.core.api.Assertions.within(1e-10));
    assertThat(Math.abs(dir1.getX())).isCloseTo(0.0, org.assertj.core.api.Assertions.within(1e-10));
    assertThat(Math.abs(dir1.getZ())).isCloseTo(0.0, org.assertj.core.api.Assertions.within(1e-10));
  }

  @Test
  @DisplayName("getDirectionFrom lanza excepción si punto es null")
  void getDirectionFrom_throwsOnNullPoint() {
    DirectionalLight light =
        new DirectionalLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, -1, 0));

    assertThatThrownBy(() -> light.getDirectionFrom(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Point cannot be null");
  }

  @Test
  @DisplayName("getDistanceFrom devuelve infinito")
  void getDistanceFrom_returnsInfinity() {
    DirectionalLight light =
        new DirectionalLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, -1, 0));

    Vector3D point = new Vector3D(5, 3, -2);

    assertThat(light.getDistanceFrom(point)).isEqualTo(Double.POSITIVE_INFINITY);
  }

  @Test
  @DisplayName("getDistanceFrom lanza excepción si punto es null")
  void getDistanceFrom_throwsOnNullPoint() {
    DirectionalLight light =
        new DirectionalLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, -1, 0));

    assertThatThrownBy(() -> light.getDistanceFrom(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Point cannot be null");
  }

  @Test
  @DisplayName("toString devuelve representación legible")
  void toString_returnsReadableString() {
    DirectionalLight light =
        new DirectionalLight(new Vector3D(1, 1, 1), 0.8, new Vector3D(1, -1, 0));

    String result = light.toString();

    assertThat(result).contains("DirectionalLight");
    assertThat(result).contains("direction");
    assertThat(result).contains("intensity");
  }

  @Test
  @DisplayName("dirección diagonal se normaliza correctamente")
  void diagonalDirection_normalizesCorrectly() {
    DirectionalLight light =
        new DirectionalLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(1, -1, 1));

    Vector3D expectedDirection = new Vector3D(1, -1, 1).normalize();
    assertThat(light.getDirection()).isEqualTo(expectedDirection);

    // La dirección hacia la fuente es la negación
    Vector3D directionToLight = light.getDirectionFrom(new Vector3D(0, 0, 0));
    assertThat(directionToLight).isEqualTo(expectedDirection.negate());
  }
}
