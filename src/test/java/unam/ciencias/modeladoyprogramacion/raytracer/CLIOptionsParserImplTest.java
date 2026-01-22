package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CLIOptionsParserImplTest {

  private CLIOptionsParser newParser() {
    return new CLIOptionsParserImpl();
  }

  @Test
  @DisplayName("parses required --operation using equals form and defaults threads to 1")
  void parsesOperationEqualsForm_defaultThreads() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation=word-search"};

    CLIOptions options = parser.parseOptions(args);

    CLIOptions expectedOptions =
        CLIOptions.builder().operation("word-search").threads(1).build();
    assertThat(options).isEqualTo(expectedOptions);
  }

  @Test
  @DisplayName("parses required --operation using space-separated form and defaults threads to 1")
  void parsesOperationSpaceSeparatedForm_defaultThreads() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation", "matrix-sum"};

    CLIOptions options = parser.parseOptions(args);

    CLIOptions expectedOptions =
        CLIOptions.builder().operation("matrix-sum").threads(1).build();
    assertThat(options).isEqualTo(expectedOptions);
  }

  @Test
  @DisplayName("parses optional --threads using equals form")
  void parsesThreadsEqualsForm() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation=word-search", "--threads=4"};

    CLIOptions options = parser.parseOptions(args);

    CLIOptions expectedOptions =
        CLIOptions.builder().operation("word-search").threads(4).build();
    assertThat(options).isEqualTo(expectedOptions);
  }

  @Test
  @DisplayName("parses optional --threads using space-separated form")
  void parsesThreadsSpaceSeparatedForm() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--threads", "8", "--operation", "matrix-convolution"};

    CLIOptions options = parser.parseOptions(args);

    CLIOptions expectedOptions =
        CLIOptions.builder().operation("matrix-convolution").threads(8).build();
    assertThat(options).isEqualTo(expectedOptions);
  }

  @Test
  @DisplayName("parses options regardless of order and ignores unknown flags")
  void orderIndependenceAndUnknownIgnored() {
    CLIOptionsParser parser = newParser();
    String[] args =
        new String[] {"--threads=3", "--verbose", "--operation", "image-process", "--flag"};

    CLIOptions options = parser.parseOptions(args);

    CLIOptions expectedOptions =
        CLIOptions.builder().operation("image-process").threads(3).build();
    assertThat(options).isEqualTo(expectedOptions);
  }

  @Test
  @DisplayName("throws when --operation is missing")
  void missingOperationThrows() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--threads=2"};

    assertThatThrownBy(() -> parser.parseOptions(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Missing --operation");
  }

  @Test
  @DisplayName("throws when --operation is present without value")
  void operationMissingValueThrows() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation"};

    assertThatThrownBy(() -> parser.parseOptions(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("--operation requires a value");
  }

  @Test
  @DisplayName("throws when --threads is present without value")
  void threadsMissingValueThrows() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation=ws", "--threads"};

    assertThatThrownBy(() -> parser.parseOptions(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("--threads requires a value");
  }

  @Test
  @DisplayName("throws when --threads is not an integer")
  void threadsNonIntegerThrows() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation=ws", "--threads=abc"};

    assertThatThrownBy(() -> parser.parseOptions(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("--threads must be a positive integer");
  }

  @Test
  @DisplayName("throws when --threads is zero")
  void threadsZeroThrows() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation=ws", "--threads=0"};

    assertThatThrownBy(() -> parser.parseOptions(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("--threads must be a positive integer");
  }

  @Test
  @DisplayName("throws when --threads is negative")
  void threadsNegativeThrows() {
    CLIOptionsParser parser = newParser();
    String[] args = new String[] {"--operation=ws", "--threads", "-2"};

    assertThatThrownBy(() -> parser.parseOptions(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("--threads must be a positive integer");
  }
}
