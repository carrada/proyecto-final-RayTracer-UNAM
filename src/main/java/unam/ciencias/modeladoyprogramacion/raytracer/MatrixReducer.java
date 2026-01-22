package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Interfaz funcional para reducir elementos de matriz.
 *
 * <p>Permite usar expresiones lambda y method references para operaciones de reducción.
 *
 * @param <T> Tipo de elemento
 * @author Cristopher Carrada
 */
@FunctionalInterface
public interface MatrixReducer<T> {
  T reduce(int row, int column, T accValue, T value);
}
