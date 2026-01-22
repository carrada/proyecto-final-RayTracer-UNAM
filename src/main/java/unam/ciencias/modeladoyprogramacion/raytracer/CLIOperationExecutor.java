package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Interfaz Strategy para ejecutar operaciones de línea de comandos.
 *
 * @author Cristopher Carrada
 */
public interface CLIOperationExecutor {
    void execute(CLIOptions options);
}
