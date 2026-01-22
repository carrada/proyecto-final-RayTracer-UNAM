package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Interfaz para el análisis de opciones de línea de comandos.
 *
 * @author Cristopher Carrada
 */
public interface CLIOptionsParser {
    CLIOptions parseOptions(String[] args);
}
