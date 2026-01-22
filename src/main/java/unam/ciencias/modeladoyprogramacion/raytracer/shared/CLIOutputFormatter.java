package unam.ciencias.modeladoyprogramacion.raytracer.shared;

/**
 * Interfaz funcional para formatear datos a String.
 *
 * <p>Permite usar expresiones lambda y method references para operaciones de formateo.
 *
 * @param <T> Tipo de dato a formatear
 * @author Cristopher Carrada
 */
@FunctionalInterface
public interface CLIOutputFormatter<T> {
    String format(T result);
}
