package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Implementación de CLIOptionsParser que analiza argumentos de línea de comandos.
 * Soporta las opciones --operation y --threads.
 * --operation es requerido.
 * --threads es opcional y por defecto es 1.
 *
 * Lanza IllegalArgumentException para opciones inválidas o faltantes.
 *
 * @author Cristopher Carrada
 */
public final class CLIOptionsParserImpl implements CLIOptionsParser {

  /**
   * Analiza las opciones proporcionadas desde la línea de comandos.
   *
   * @param args argumentos de línea de comandos
   * @return un objeto CLIOptions con las opciones parseadas
   */
  @Override
  public CLIOptions parseOptions(String[] args) {
    // Verificar --help o -h primero
    for (String arg : args) {
      if ("--help".equals(arg) || "-h".equals(arg)) {
        printUsage();
        System.exit(0);
      }
    }

    // Construye las opciones utilizando los métodos de parseo
    return CLIOptions.builder()
        .operation(parseOperationOption(args))
        .threads(parseThreadsOption(args))
        .input(parseStringOption(args, "--input", null))
        .output(parseStringOption(args, "--output", null))
        .build();
  }

  /**
   * Imprime el mensaje de ayuda para el usuario.
   */
  private void printUsage() {
    System.out.println("Usage: java -jar practica-03-1.0.jar [OPTIONS]");
    System.out.println();
    System.out.println("Required options:");
    System.out.println("  --operation <name>    Operation to execute (matrix-addition, ray-tracer)");
    System.out.println();
    System.out.println("Optional options:");
    System.out.println("  --threads <n>         Number of threads (default: 1)");
    System.out.println("  --input <file>        Input file path (operation specific)");
    System.out.println("  --output <file>       Output file path (operation specific)");
    System.out.println("  -h, --help            Show this help message");
    System.out.println();
    System.out.println("Examples:");
    System.out.println("  java -jar practica-03-1.0.jar --operation matrix-addition --threads 4 < input.txt");
    System.out.println("  java -jar practica-03-1.0.jar --operation ray-tracer --threads 8 --input scene.json --output image.png");
  }

  /**
   * Analiza la opción --operation.
   *
   * @param args argumentos de línea de comandos
   * @return el nombre de la operación
   */
  private String parseOperationOption(String[] args) {
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.startsWith("--operation=")) {
        return arg.substring("--operation=".length());
      } else if ("--operation".equals(arg)) {
        if (++i >= args.length) {
          throw new IllegalArgumentException("--operation requires a value");
        }
        return args[i];
      }
    }
    throw new IllegalArgumentException("Missing --operation");
  }

  /**
   * Analiza la opción --threads.
   *
   * @param args argumentos de línea de comandos
   * @return el número de hilos
   */
  private int parseThreadsOption(String[] args) {
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.startsWith("--threads=")) {
        return parseThreads(arg.substring("--threads=".length()));
      } else if ("--threads".equals(arg)) {
        if (++i >= args.length) {
          throw new IllegalArgumentException("--threads requires a value");
        }
        return parseThreads(args[i]);
      }
    }
    return 1; // valor por defecto
  }

  /**
   * Analiza y valida el valor de los hilos.
   *
   * @param value el valor a analizar
   * @return el número de hilos como un entero
   */
  private int parseThreads(String value) {
    try {
      int t = Integer.parseInt(value);
      if (t <= 0) throw new NumberFormatException();
      return t;
    } catch (NumberFormatException nfe) {
      throw new IllegalArgumentException("--threads must be a positive integer");
    }
  }

  /**
   * Analiza opciones de tipo cadena (String).
   *
   * @param args argumentos de línea de comandos
   * @param optionName el nombre de la opción
   * @param defaultValue valor por defecto si la opción no se encuentra
   * @return el valor de la opción como una cadena
   */
  private String parseStringOption(String[] args, String optionName, String defaultValue) {
    String prefix = optionName + "=";
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.startsWith(prefix)) {
        return arg.substring(prefix.length());
      } else if (optionName.equals(arg)) {
        if (++i >= args.length) {
          throw new IllegalArgumentException(optionName + " requires a value");
        }
        return args[i];
      }
    }
    return defaultValue;
  }
}
