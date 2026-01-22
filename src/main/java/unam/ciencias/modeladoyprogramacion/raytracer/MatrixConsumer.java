package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Interfaz funcional para consumir elementos de matriz.
 *
 * <p>Permite usar expresiones lambda y method references para operaciones de consumo.
 *
 * @param <T> Tipo de elemento
 * @author Cristopher Carrada
 */
@FunctionalInterface
public interface MatrixConsumer<T> {
  void accept(int row, int column, T value);
}
