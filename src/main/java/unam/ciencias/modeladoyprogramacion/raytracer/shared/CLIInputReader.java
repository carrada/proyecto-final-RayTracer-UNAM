package unam.ciencias.modeladoyprogramacion.raytracer.shared;

import java.io.InputStream;

/**
 * Interfaz funcional para leer datos desde un InputStream.
 *
 * <p>Permite usar expresiones lambda y method references para operaciones de lectura.
 *
 * @param <T> Tipo de dato a leer
 * @author Cristopher Carrada
 */
@FunctionalInterface
public interface CLIInputReader<T> {
    T read(InputStream input);
}
