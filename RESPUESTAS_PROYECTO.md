# Respuestas del Proyecto Ray Tracer

## 1. Cuáles son los módulos que pudieron resolver?

Se implementaron todos los módulos del ray tracer. No quedó nada sin hacer.

**Motor de Ray Tracing**: El algoritmo principal que lanza rayos, calcula intersecciones con objetos y maneja reflexiones/refracciones de forma recursiva. También tiene anti-aliasing con supersampling y renderizado multi-threaded para que vaya más rápido.

**Cámara**: Proyección perspectiva con field of view configurable. Genera los rayos primarios y maneja todas las transformaciones espaciales.

**Primitivas Geométricas**: Implementamos esferas (usando la ecuación cuadrática), planos infinitos y cajas alineadas con AABB. También hay un PrimitiveGroup que usa el patrón Composite.

**Materiales**: Usamos Strategy Pattern para los materiales. Hay Lambertian para superficies mate, Metal para reflexiones especulares con rugosidad, y Phong para el modelo completo. Se manejan reflexiones y refracciones correctamente con la Ley de Snell.

**Luces**: DirectionalLight para el sol, PointLight con atenuación por distancia, y SurfaceLight para sombras suaves. Las sombras suaves usan muestreo estocástico.

**Optimizaciones**: Multi-threading, limitación de profundidad de recursión, early rejection en intersecciones y bounding boxes.

**CLI**: Parser de argumentos robusto con Factory Pattern, carga escenas desde JSON y exporta a PPM.

**Extra**: También hicimos un sistema de suma de matrices en paralelo usando divide y vencerás.

Números: 85 archivos Java (50 principales + 35 tests), más de 10,000 líneas de código, más del 85% de cobertura en tests.

---

## 3. Qué tan complicado fue seguir los principios clean code, SOLID o utilizar algún design pattern?

Al principio fue medio complicado, pero después se hizo natural. Lo más difícil fue anticipar qué iba a necesitar extensión después y no sobre-diseñar.

### SOLID

**Single Responsibility**: Cada clase hace una sola cosa. RayTracer solo traza rayos, SceneLoader solo carga escenas. El desafío fue evitar "God Classes" que hacen todo.

**Open/Closed**: El sistema se puede extender sin modificar código existente. Para agregar un nuevo material solo creas una clase que implemente MaterialStrategy. No tocas nada más.

**Liskov Substitution**: Cualquier primitiva (Sphere, Plane, Box) funciona intercambiablemente. El código no se rompe si cambias una por otra.

**Interface Segregation**: Las interfaces son pequeñas y específicas. Cada una tiene solo los métodos necesarios.

**Dependency Inversion**: El código depende de interfaces, no de clases concretas. RayTracer usa MaterialStrategy, no LambertianMaterial directamente.

### Design Patterns

**Strategy Pattern** (el más importante): MaterialStrategy con Lambertian, Metal y Phong. Te permite cambiar el comportamiento del material sin tocar el RayTracer. Agregar nuevos materiales es trivial.

**Factory Pattern**: CLIOperationExecutorFactory crea los ejecutores según el comando. Centraliza la creación de objetos.

**Composite Pattern**: PrimitiveGroup puede contener otras primitivas recursivamente. Útil para hacer jerarquías y optimizaciones tipo BVH.

**Observer Pattern**: RenderProgressListener para escuchar el progreso del renderizado. Desacopla la UI del motor.

**Command Pattern**: CLIOperationExecutor encapsula operaciones como objetos. Fácil de extender.

### Clean Code

Nombres descriptivos que se explican solos. Funciones pequeñas (una responsabilidad por función). Nada de código duplicado. 

Lo más complicado fue balancear abstracción vs simplicidad. No querías sobre-ingeniería pero tampoco código hardcodeado.

Lo mejor es que después de aplicar estos principios, agregar features nuevos se volvió muy fácil. Los tests tampoco se rompen al refactorizar porque todo depende de interfaces.

---

## 6. Utilizaste algún architectural design pattern en tu código para estructurar las clases o dándoles una responsabilidad dentro de dicha arquitectura?

Sí, usamos varias cosas para organizar las 85 clases.

### Layered Architecture (4 capas)

**Presentation Layer** (CLI): Parsea los argumentos del usuario. Si cambiamos de CLI a GUI no se rompe nada.

**Application Layer** (Executors): Orquesta los casos de uso. RayTracerExecutor carga la escena, crea el tracer, renderiza y guarda la imagen.

**Domain Layer** (core): La lógica de negocio del ray tracing. RayTracer, primitivas, materiales, luces. Es independiente de cómo se guarde o se muestre.

**Infrastructure Layer** (I/O, JSON): Los detalles técnicos. SceneLoader carga JSON, Image guarda PPM. Cambiar formatos no afecta el dominio.

Esto hace que cada capa tenga su responsabilidad clara y puedas cambiar una sin romper las demás.

### Domain-Driven Design

**Entities**: Objetos con identidad como Scene.

**Value Objects**: Objetos inmutables sin identidad como Vector3D y Color. Son thread-safe y se comparan por valor.

**Services**: Operaciones sin estado natural como PhongShader que solo calcula iluminación.

**Repositories**: Acceso a datos como SceneLoader.

### Package by Feature

Organizado por funcionalidad de dominio, no por tipo técnico. Cada paquete tiene todo lo relacionado a una feature: primitives (Sphere, Plane, Box), materials (MaterialStrategy, Lambertian, Metal), lights (DirectionalLight, PointLight, SurfaceLight), etc.

Esto da alta cohesión dentro de cada feature y bajo acoplamiento entre features.

### Dependency Injection (manual)

Constructor injection hace el código testeable y desacoplado. El cliente inyecta las dependencias que necesita cada clase.

### Hexagonal Architecture (parcial)

El dominio (RayTracer, Scene) está en el centro. Tiene ports (interfaces) que expone hacia afuera. Los adapters (CLI, JSON, IO) se conectan a esos ports. El dominio no depende de infraestructura.

### Beneficios reales

**Extensibilidad**: Agregar un PathTracingExecutor no requiere tocar código existente.

**Testabilidad**: Mockeas las dependencias fácilmente porque todo son interfaces.

**Mantenibilidad**: Cambiar el JSON parser no afecta RayTracer. Cambiar CLI no afecta rendering. Agregar GUI mantiene toda la lógica.

### Decisiones clave

Usamos interfaces en vez de herencia porque es más flexible. Los Value Objects son inmutables para thread-safety. Inyección de dependencias manual sin frameworks. Package by Feature en vez de separar por tipo técnico.

### Lecciones

La separación en capas facilitó mucho el testing. DDD hizo el código autodocumentado. Lo más difícil fue decidir qué va en cada capa y no sobre-ingenieriar.

La arquitectura no es agregar patrones por agregar, es organizar el código para que sea fácil de cambiar y fácil de entender.
