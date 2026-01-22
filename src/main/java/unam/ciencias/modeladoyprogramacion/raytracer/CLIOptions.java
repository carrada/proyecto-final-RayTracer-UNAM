package unam.ciencias.modeladoyprogramacion.raytracer;

import lombok.Builder;

/**
 * Objeto de configuración de opciones de línea de comandos.
 *
 * <p>Utiliza Java 21 record para inmutabilidad automática y métodos generados
 * (equals, hashCode, toString). El builder de Lombok se mantiene para compatibilidad.
 *
 * @param operation nombre de la operación a ejecutar
 * @param threads número de hilos para procesamiento concurrente
 * @param input archivo de entrada (opcional)
 * @param output archivo de salida (opcional)
 * @author Cristopher Carrada
 */
@Builder
public record CLIOptions(String operation, int threads, String input, String output) {}
