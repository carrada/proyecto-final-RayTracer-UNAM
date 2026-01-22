package unam.ciencias.modeladoyprogramacion.raytracer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase principal para la aplicación de Ray Tracer de línea de comandos.
 * 
 * <p>Esta clase actúa como punto de entrada principal de toda la aplicación, siendo responsable de:
 * <ul>
 *   <li>Inicializar el sistema de logging mediante SLF4J</li>
 *   <li>Parsear los argumentos de línea de comandos proporcionados por el usuario</li>
 *   <li>Crear el ejecutor apropiado según la operación solicitada</li>
 *   <li>Coordinar la ejecución de operaciones (ray tracing, suma de matrices, etc.)</li>
 *   <li>Manejar errores y excepciones de manera elegante</li>
 * </ul>
 * 
 * <h2>Patrones de Diseño Implementados</h2>
 * <p>Esta clase utiliza dos patrones de diseño fundamentales:
 * <ul>
 *   <li><b>Factory Method</b>: Delega la creación de ejecutores a {@link CLIOperationExecutorFactory},
 *       permitiendo que el tipo exacto de ejecutor se determine en tiempo de ejecución basándose
 *       en las opciones proporcionadas.</li>
 *   <li><b>Strategy</b>: Cada ejecutor implementa una estrategia diferente para realizar operaciones
 *       específicas (renderizado, suma de matrices), permitiendo intercambiar comportamientos
 *       sin modificar el código cliente.</li>
 * </ul>
 * 
 * <h2>Flujo de Ejecución</h2>
 * <ol>
 *   <li>El usuario invoca la aplicación desde la línea de comandos con argumentos específicos</li>
 *   <li>Los argumentos son parseados por {@link CLIOptionsParser} para extraer las opciones</li>
 *   <li>Se utiliza {@link CLIOperationExecutorFactory} para crear el ejecutor apropiado</li>
 *   <li>El ejecutor procesa la operación solicitada</li>
 *   <li>Los resultados se muestran al usuario o se guardan en archivos</li>
 * </ol>
 * 
 * <h2>Manejo de Errores</h2>
 * <p>La aplicación implementa un robusto sistema de manejo de errores:
 * <ul>
 *   <li>Excepciones son capturadas y registradas mediante el sistema de logging</li>
 *   <li>Códigos de salida apropiados son retornados al sistema operativo</li>
 *   <li>Mensajes de error descriptivos son mostrados al usuario</li>
 * </ul>
 * 
 * <h2>Ejemplo de Uso</h2>
 * <pre>
 * {@code
 * // Renderizar una escena
 * java -jar raytracer.jar --operation raytracer --scene scene.json --output image.ppm
 * 
 * // Suma de matrices
 * java -jar raytracer.jar --operation matrix_addition --input1 matrix1.txt --input2 matrix2.txt
 * }
 * </pre>
 * 
 * <h2>Dependencias</h2>
 * <ul>
 *   <li>SLF4J para logging configurable y eficiente</li>
 *   <li>{@link CLIOptionsParser} para parseo de argumentos</li>
 *   <li>{@link CLIOperationExecutorFactory} para creación de ejecutores</li>
 * </ul>
 * 
 * <h2>Consideraciones de Diseño</h2>
 * <ul>
 *   <li>La clase es final para prevenir herencia innecesaria</li>
 *   <li>Los componentes son estáticos ya que no se requiere estado de instancia</li>
 *   <li>Se utiliza inyección de dependencias implícita mediante factories</li>
 *   <li>El diseño sigue el principio de responsabilidad única (SRP)</li>
 * </ul>
 * 
 * @author Cristopher Carrada
 * @version 1.0
 * @since 1.0
 * @see CLIOptionsParser
 * @see CLIOperationExecutor
 * @see CLIOperationExecutorFactory
 */
public final class App {
  /**
   * Logger configurado para la clase App.
   * 
   * <p>Utiliza SLF4J como fachada de logging, permitiendo configurar el framework
   * de logging subyacente (Logback, Log4j, etc.) sin modificar el código.
   * Este logger se usa para registrar:
   * <ul>
   *   <li>Errores de ejecución y excepciones</li>
   *   <li>Información de depuración durante el desarrollo</li>
   *   <li>Advertencias sobre configuraciones incorrectas</li>
   * </ul>
   */
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  
  /**
   * Parser de opciones de línea de comandos.
   * 
   * <p>Responsable de convertir los argumentos raw proporcionados por el usuario
   * en un objeto {@link CLIOptions} estructurado y validado. Esta separación
   * de responsabilidades permite:
   * <ul>
   *   <li>Testear el parseo de argumentos independientemente</li>
   *   <li>Cambiar la implementación del parser sin afectar la lógica principal</li>
   *   <li>Reutilizar el parser en otros contextos si es necesario</li>
   * </ul>
   * 
   * @see CLIOptionsParser
   * @see CLIOptionsParserImpl
   */
  static CLIOptionsParser cliOptionsParser = new CLIOptionsParserImpl();
  
  /**
   * Fábrica de ejecutores de operaciones CLI.
   * 
   * <p>Implementa el patrón Factory Method para crear instancias apropiadas de
   * {@link CLIOperationExecutor} basándose en las opciones proporcionadas.
   * Esta fábrica:
   * <ul>
   *   <li>Encapsula la lógica de creación de objetos complejos</li>
   *   <li>Permite agregar nuevas operaciones sin modificar código existente (Open/Closed Principle)</li>
   *   <li>Retorna Optional para manejar casos donde no existe un ejecutor apropiado</li>
   * </ul>
   * 
   * <p><b>Tipo Genérico:</b>
   * <ul>
   *   <li>Input: {@link CLIOptions} - Opciones parseadas de la línea de comandos</li>
   *   <li>Output: {@link CLIOperationExecutor} - Ejecutor apropiado para la operación</li>
   * </ul>
   * 
   * @see FactoryMethod
   * @see CLIOperationExecutorFactory
   */
  static FactoryMethod<CLIOptions, CLIOperationExecutor> executorFactory
    = new CLIOperationExecutorFactory();

  /**
   * Método principal para ejecutar la aplicación de línea de comandos.
   * 
   * <p>Este es el punto de entrada de toda la aplicación. Orquesta el flujo completo:
   * <ol>
   *   <li><b>Parseo:</b> Convierte los argumentos raw en objetos {@link CLIOptions}</li>
   *   <li><b>Creación:</b> Usa la fábrica para obtener el ejecutor apropiado</li>
   *   <li><b>Validación:</b> Verifica que existe un ejecutor para la operación solicitada</li>
   *   <li><b>Ejecución:</b> Delega la operación al ejecutor correspondiente</li>
   *   <li><b>Manejo de Errores:</b> Captura y registra cualquier excepción</li>
   * </ol>
   * 
   * <h3>Códigos de Salida</h3>
   * <ul>
   *   <li><b>0:</b> Ejecución exitosa</li>
   *   <li><b>2:</b> Error de ejecución (operación no soportada, excepción, etc.)</li>
   * </ul>
   * 
   * <h3>Patrones Aplicados</h3>
   * <ul>
   *   <li><b>Template Method:</b> Define el esqueleto del algoritmo, delegando pasos específicos</li>
   *   <li><b>Factory Method:</b> Usa la fábrica para crear ejecutores</li>
   *   <li><b>Strategy:</b> Cada ejecutor implementa una estrategia diferente</li>
   * </ul>
   * 
   * <h3>Ejemplo de Invocaciones</h3>
   * <pre>
   * {@code
   * // Ray Tracing básico
   * main(new String[]{"--operation", "raytracer", "--scene", "scene.json", "--output", "render.ppm"});
   * 
   * // Ray Tracing con opciones avanzadas
   * main(new String[]{"--operation", "raytracer", "--scene", "complex.json", 
   *                   "--output", "render.ppm", "--width", "1920", "--height", "1080", 
   *                   "--samples", "100", "--threads", "8"});
   * 
   * // Suma de matrices
   * main(new String[]{"--operation", "matrix_addition", "--input1", "A.txt", 
   *                   "--input2", "B.txt", "--output", "result.txt"});
   * }
   * </pre>
   * 
   * <h3>Manejo de Excepciones</h3>
   * <p>Las excepciones son manejadas de la siguiente manera:
   * <ul>
   *   <li><b>IllegalArgumentException:</b> Argumentos inválidos o mal formateados</li>
   *   <li><b>IOException:</b> Problemas de lectura/escritura de archivos</li>
   *   <li><b>RuntimeException:</b> Errores inesperados durante la ejecución</li>
   *   <li>Todas son loggeadas y resultan en un código de salida 2</li>
   * </ul>
   * 
   * <h3>Thread Safety</h3>
   * <p>Este método es thread-safe ya que:
   * <ul>
   *   <li>Los componentes estáticos son efectivamente inmutables</li>
   *   <li>Cada invocación usa variables locales independientes</li>
   *   <li>No se comparte estado mutable entre invocaciones</li>
   * </ul>
   * 
   * @param args Argumentos de línea de comandos proporcionados por el usuario.
   *             Típicamente incluyen:
   *             <ul>
   *               <li>--operation: Tipo de operación a realizar</li>
   *               <li>--scene: Archivo de escena para ray tracing</li>
   *               <li>--output: Archivo de salida</li>
   *               <li>Parámetros adicionales según la operación</li>
   *             </ul>
   * 
   * @throws IllegalArgumentException Si los argumentos son inválidos (capturada internamente)
   * @throws RuntimeException Si ocurre un error durante la ejecución (capturada internamente)
   * 
   * @see CLIOptions
   * @see CLIOperationExecutor
   * @see System#exit(int)
   */
  public static void main(String[] args) {
    try {
      var options = cliOptionsParser.parseOptions(args);
      var executor = executorFactory.createObj(options);
      if (executor.isPresent()) {
        executor.get().execute(options);
      } else {
        logger.error("Unsupported or missing --operation");
        System.exit(2);
      }
    } catch (Exception e) {
      logger.error("Application error", e);
      System.exit(2);
    }
  }
}
