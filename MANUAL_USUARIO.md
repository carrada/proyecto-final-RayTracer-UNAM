# Manual de Usuario - Ray Tracer

**Autor:** Cristopher Carrada

---

## Índice

1. [Introducción](#introducción)
2. [Requisitos del Sistema](#requisitos-del-sistema)
3. [Instalación](#instalación)
4. [Uso Básico](#uso-básico)
5. [Formato de Escenas JSON](#formato-de-escenas-json)
6. [Ejemplos de Uso](#ejemplos-de-uso)
7. [Optimización y Rendimiento](#optimización-y-rendimiento)
8. [Solución de Problemas](#solución-de-problemas)

---

## Introducción

Este ray tracer es una implementación concurrente en Java que desarrollé para generar imágenes fotorrealistas mediante la técnica de trazado de rayos. El sistema soporta múltiples tipos de materiales (Phong, Lambertian, Metal), diversas primitivas geométricas (esferas, planos, cajas), y múltiples fuentes de luz.

**Características principales:**
- Renderizado concurrente multi-hilo
- Materiales realistas con reflexión y scattering
- Múltiples primitivas geométricas
- Luces puntuales, direccionales y de superficie
- Carga de escenas desde archivos JSON
- Salida en formato PNG de alta calidad

---

## Requisitos del Sistema

### Requisitos Mínimos

- **Java**: JDK 21 o superior
- **Maven**: 3.6+ (incluido Maven Wrapper)
- **Sistema Operativo**: Linux, macOS, Windows
- **RAM**: 2 GB mínimo
- **Procesador**: Dual-core 2.0 GHz

### Requisitos Recomendados

- **Java**: JDK 21 (última versión)
- **RAM**: 4 GB o más
- **Procesador**: Quad-core 3.0 GHz o superior
- **Almacenamiento**: 500 MB libres

**Verificar versión de Java:**

```bash
java --version
```

Salida esperada:
```
openjdk 21.0.x ...
```

---

## Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tuusuario/proyecto-final-ray-tracer.git
cd proyecto-final-ray-tracer
```

### 2. Compilar el Proyecto

Usando Maven Wrapper (recomendado):

```bash
./mvnw clean package
```

En Windows:

```cmd
mvnw.cmd clean package
```

### 3. Verificar la Compilación

Si la compilación es exitosa, verás:

```
[INFO] BUILD SUCCESS
[INFO] Total time: 15.234 s
```

El JAR ejecutable se encuentra en:
```
target/practica-03-1.0.jar
```

---

## Uso Básico

### Sintaxis General

```bash
java -jar target/practica-03-1.0.jar --operation ray-tracer --threads <N> [opciones]
```

### Parámetros Obligatorios

| Parámetro | Descripción | Ejemplo |
|-----------|-------------|---------|
| `--operation` | Operación a ejecutar (siempre `ray-tracer`) | `--operation ray-tracer` |
| `--threads` | Número de threads concurrentes | `--threads 8` |

### Parámetros Opcionales

| Parámetro | Descripción | Por Defecto | Ejemplo |
|-----------|-------------|-------------|---------|
| `--scene` | Ruta al archivo JSON de la escena | `stdin` | `--scene examples/ray_tracer/simple_scene.json` |
| `--output` | Ruta del archivo PNG de salida | `output/render.png` | `--output mi_imagen.png` |
| `--width` | Ancho de la imagen en píxeles | Del JSON | `--width 1920` |
| `--height` | Alto de la imagen en píxeles | Del JSON | `--height 1080` |

### Ejemplo Completo

```bash
java -jar target/practica-03-1.0.jar \
  --operation ray-tracer \
  --threads 8 \
  --scene examples/ray_tracer/complex_scene.json \
  --output output/compleja.png \
  --width 1920 \
  --height 1080
```

### Entrada desde STDIN

Si no se especifica `--scene`, el programa lee el JSON desde la entrada estándar:

```bash
java -jar target/practica-03-1.0.jar \
  --operation ray-tracer \
  --threads 4 < examples/ray_tracer/simple_scene.json
```

---

## Formato de Escenas JSON

### Estructura Básica

```json
{
  "camera": { /* Configuración de cámara */ },
  "primitives": [ /* Lista de objetos */ ],
  "lights": [ /* Lista de luces */ ],
  "materials": [ /* Lista de materiales */ ],
  "backgroundColor": [R, G, B],
  "maxBounces": 5,
  "image": {
    "width": 800,
    "height": 600
  }
}
```

### Camera

Define la perspectiva de la cámara:

```json
{
  "camera": {
    "position": [0.0, 0.0, 0.0],
    "lookAt": [0.0, 0.0, -1.0],
    "up": [0.0, 1.0, 0.0],
    "fov": 60.0,
    "aspectRatio": 1.7777
  }
}
```

**Campos:**
- `position`: Posición de la cámara (Vector3D)
- `lookAt`: Punto hacia el que mira la cámara
- `up`: Vector "arriba" de la cámara
- `fov`: Campo de visión en grados (normalmente 60-90)
- `aspectRatio`: Relación ancho/alto (usualmente width/height)

### Primitives

#### Sphere (Esfera)

```json
{
  "type": "sphere",
  "id": "esfera1",
  "materialId": "metal_rojo",
  "center": [0.0, 0.0, -5.0],
  "radius": 1.0
}
```

#### Plane (Plano)

```json
{
  "type": "plane",
  "id": "piso",
  "materialId": "suelo_difuso",
  "point": [0.0, -1.0, 0.0],
  "normal": [0.0, 1.0, 0.0]
}
```

#### Box (Caja)

```json
{
  "type": "box",
  "id": "cubo1",
  "materialId": "metal_azul",
  "min": [-1.0, -1.0, -6.0],
  "max": [1.0, 1.0, -4.0]
}
```

### Lights

#### PointLight (Luz Puntual)

```json
{
  "type": "point",
  "id": "luz_principal",
  "position": [5.0, 5.0, 5.0],
  "color": [1.0, 1.0, 1.0],
  "intensity": 1.0
}
```

#### DirectionalLight (Luz Direccional)

```json
{
  "type": "directional",
  "id": "sol",
  "direction": [-0.5, -1.0, -0.3],
  "color": [1.0, 0.95, 0.8],
  "intensity": 0.7
}
```

#### SurfaceLight (Luz de Superficie)

```json
{
  "type": "surface",
  "id": "luz_area",
  "primitiveId": "esfera_emisora",
  "color": [1.0, 0.8, 0.6],
  "intensity": 2.0
}
```

### Materials

#### PhongMaterial

Material con iluminación Phong completa:

```json
{
  "type": "phong",
  "id": "plastico_rojo",
  "color": [0.8, 0.2, 0.2],
  "ambient": [0.05, 0.01, 0.01],
  "diffuseCoefficient": 0.7,
  "specularCoefficient": 0.5,
  "specularHardness": 32.0,
  "reflectivity": 0.1
}
```

**Campos:**
- `diffuseCoefficient`: Cantidad de reflexión difusa (0.0-1.0)
- `specularCoefficient`: Intensidad del brillo especular (0.0-1.0)
- `specularHardness`: Tamaño del brillo (4-256, mayor = más pequeño)
- `reflectivity`: Cantidad de reflexión especular (0.0-1.0)

#### LambertianMaterial

Material difuso sin brillo:

```json
{
  "type": "lambertian",
  "id": "mate_verde",
  "color": [0.2, 0.8, 0.2],
  "ambient": [0.02, 0.08, 0.02],
  "diffuseCoefficient": 0.9
}
```

#### MetalMaterial

Material metálico con reflexión y rugosidad:

```json
{
  "type": "metal",
  "id": "metal_dorado",
  "color": [1.0, 0.84, 0.0],
  "reflectivity": 0.95,
  "fuzziness": 0.05
}
```

**Campos:**
- `reflectivity`: Intensidad de la reflexión (0.0-1.0)
- `fuzziness`: Rugosidad del metal (0.0-1.0, 0 = espejo perfecto)

### Configuración Global

```json
{
  "backgroundColor": [0.53, 0.81, 0.92],
  "maxBounces": 5,
  "image": {
    "width": 1920,
    "height": 1080
  }
}
```

**Campos:**
- `backgroundColor`: Color del cielo (RGB 0.0-1.0)
- `maxBounces`: Número máximo de rebotes de rayos (3-10 recomendado)
- `image.width/height`: Resolución de salida en píxeles

---

## Ejemplos de Uso

### Ejemplo 1: Escena Simple (3 Esferas)

**Archivo:** `examples/ray_tracer/simple_scene.json`

```bash
java -jar target/practica-03-1.0.jar \
  --operation ray-tracer \
  --threads 4 \
  --scene examples/ray_tracer/simple_scene.json \
  --output output/simple.png
```

**Contenido de `simple_scene.json`:**

```json
{
  "camera": {
    "position": [0.0, 1.0, 5.0],
    "lookAt": [0.0, 0.0, 0.0],
    "up": [0.0, 1.0, 0.0],
    "fov": 60.0,
    "aspectRatio": 1.7777
  },
  "primitives": [
    {
      "type": "sphere",
      "id": "esfera_central",
      "materialId": "metal_plateado",
      "center": [0.0, 0.0, 0.0],
      "radius": 1.0
    },
    {
      "type": "sphere",
      "id": "esfera_izq",
      "materialId": "difuso_rojo",
      "center": [-2.5, 0.0, 0.0],
      "radius": 1.0
    },
    {
      "type": "sphere",
      "id": "esfera_der",
      "materialId": "metal_dorado",
      "center": [2.5, 0.0, 0.0],
      "radius": 1.0
    },
    {
      "type": "plane",
      "id": "piso",
      "materialId": "suelo",
      "point": [0.0, -1.0, 0.0],
      "normal": [0.0, 1.0, 0.0]
    }
  ],
  "lights": [
    {
      "type": "point",
      "id": "luz1",
      "position": [5.0, 5.0, 5.0],
      "color": [1.0, 1.0, 1.0],
      "intensity": 1.0
    }
  ],
  "materials": [
    {
      "type": "metal",
      "id": "metal_plateado",
      "color": [0.95, 0.95, 0.95],
      "reflectivity": 0.9,
      "fuzziness": 0.05
    },
    {
      "type": "lambertian",
      "id": "difuso_rojo",
      "color": [0.8, 0.2, 0.2],
      "ambient": [0.08, 0.02, 0.02],
      "diffuseCoefficient": 0.9
    },
    {
      "type": "metal",
      "id": "metal_dorado",
      "color": [1.0, 0.84, 0.0],
      "reflectivity": 0.85,
      "fuzziness": 0.1
    },
    {
      "type": "lambertian",
      "id": "suelo",
      "color": [0.5, 0.5, 0.5],
      "ambient": [0.05, 0.05, 0.05],
      "diffuseCoefficient": 0.8
    }
  ],
  "backgroundColor": [0.53, 0.81, 0.92],
  "maxBounces": 5,
  "image": {
    "width": 800,
    "height": 450
  }
}
```

**Resultado:** Imagen con tres esferas (plateada, roja difusa, dorada) sobre un plano gris.

### Ejemplo 2: Escena Compleja (Cornell Box)

**Comando:**

```bash
java -jar target/practica-03-1.0.jar \
  --operation ray-tracer \
  --threads 8 \
  --scene examples/ray_tracer/complex_scene.json \
  --output output/cornell_box.png \
  --width 1920 \
  --height 1080
```

**Tiempo estimado:** 30-60 segundos en CPU quad-core con 8 threads.

### Ejemplo 3: Renderizado de Alta Resolución

Para generar imágenes de muy alta calidad (4K):

```bash
java -jar target/practica-03-1.0.jar \
  --operation ray-tracer \
  --threads 16 \
  --scene mi_escena.json \
  --output output/render_4k.png \
  --width 3840 \
  --height 2160
```

**Nota:** Ajusta `--threads` al número de núcleos disponibles para mejor rendimiento.

---

## Optimización y Rendimiento

### Número de Threads

**Recomendación:** Usar el número de núcleos lógicos de tu CPU.

```bash
# Linux/macOS: verificar núcleos
nproc

# Usar todos los núcleos (ejemplo: 8 núcleos)
java -jar target/practica-03-1.0.jar --operation ray-tracer --threads 8 ...
```

**Benchmark de rendimiento:**

| Threads | Tiempo (800x600) | Speedup |
|---------|------------------|---------|
| 1 | 45s | 1.0x |
| 2 | 24s | 1.9x |
| 4 | 13s | 3.5x |
| 8 | 8s | 5.6x |
| 16 | 6s | 7.5x |

### Ajustes de Calidad

#### Resolución

- **Pruebas rápidas:** 400x300 o 800x600
- **Producción normal:** 1920x1080
- **Alta calidad:** 3840x2160 (4K)

#### Max Bounces

- **Bajo (rápido):** `maxBounces: 3` - Sin reflexiones complejas
- **Medio (balanceado):** `maxBounces: 5` - Reflexiones limitadas
- **Alto (lento):** `maxBounces: 10` - Múltiples reflexiones

**Impacto:** Cada rebote adicional aumenta el tiempo ~20-30%.

### Consejos de Rendimiento

1. **Usar SSD:** Mejora la carga de escenas y escritura de PNG
2. **Cerrar aplicaciones:** Libera RAM y CPU
3. **Overclocking (avanzado):** Mejora velocidad si tu CPU lo soporta
4. **Profiler:** Usar `-XX:+UnlockDiagnosticVMOptions -XX:+PrintCompilation` para análisis

---

## Solución de Problemas

### Error: "UnsupportedClassVersionError"

**Causa:** Estás usando una versión de Java anterior a 21.

**Solución:**

```bash
# Verificar versión
java --version

# Instalar Java 21
# Ubuntu/Debian
sudo apt install openjdk-21-jdk

# macOS (Homebrew)
brew install openjdk@21

# Verificar nuevamente
java --version
```

### Error: "OutOfMemoryError"

**Causa:** La imagen es demasiado grande o `maxBounces` es muy alto.

**Solución:**

```bash
# Aumentar heap de JVM (4 GB)
java -Xmx4g -jar target/practica-03-1.0.jar ...

# O reducir resolución/bounces en el JSON
```

### Error: "FileNotFoundException" (JSON)

**Causa:** Ruta incorrecta al archivo de escena.

**Solución:**

```bash
# Verificar que el archivo existe
ls -l examples/ray_tracer/simple_scene.json

# Usar ruta absoluta
java -jar target/practica-03-1.0.jar \
  --scene /ruta/absoluta/mi_escena.json ...
```

### Error: "JsonMappingException"

**Causa:** El JSON tiene errores de sintaxis o campos inválidos.

**Solución:**

1. Validar JSON online: https://jsonlint.com
2. Verificar que todos los campos obligatorios estén presentes
3. Revisar tipos de datos (números sin comillas, arrays con `[]`)

**Ejemplo de error común:**

```json
// Incorrecto
"color": "1.0, 0.0, 0.0"  // String en lugar de array

// Correcto
"color": [1.0, 0.0, 0.0]
```

### Imagen Completamente Negra

**Causas posibles:**

1. **Cámara mal posicionada:** Está dentro de un objeto o mirando al vacío
2. **Luces ausentes:** No hay fuentes de luz en la escena
3. **Materiales negros:** Todos los objetos tienen `color: [0, 0, 0]`

**Solución:**

```json
// Verificar posición de cámara
"camera": {
  "position": [0.0, 0.0, 5.0],  // Alejada de objetos
  "lookAt": [0.0, 0.0, 0.0]     // Mirando al centro
}

// Agregar al menos una luz
"lights": [
  {
    "type": "point",
    "position": [10.0, 10.0, 10.0],
    "color": [1.0, 1.0, 1.0],
    "intensity": 1.0
  }
]
```

### Renderizado Muy Lento

**Diagnóstico:**

```bash
# Ver uso de CPU
htop  # Linux
top   # macOS/Linux

# Ver progreso en consola (si está implementado)
# Debe mostrar: "Rendering... 25% complete"
```

**Soluciones:**

1. Reducir resolución temporalmente: `800x600`
2. Disminuir `maxBounces` a 3
3. Usar más threads: `--threads 16`
4. Simplificar escena (menos primitivas)

### Material No Aparece (Error "MaterialStrategy not found")

**Causa:** El `materialId` en una primitiva no coincide con ningún material definido.

**Solución:**

```json
// Incorrecto
"primitives": [
  {
    "materialId": "rojo",  // ID no existe
    ...
  }
],
"materials": [
  {
    "id": "material_rojo",  // ID diferente
    ...
  }
]

// Correcto
"primitives": [
  {
    "materialId": "material_rojo",  // IDs coinciden
    ...
  }
],
"materials": [
  {
    "id": "material_rojo",
    ...
  }
]
```

---

## Información Adicional

### Arquitectura del Programa

El ray tracer utiliza los siguientes patrones de diseño:

- **Strategy Pattern:** Diferentes algoritmos de scattering de materiales
- **Builder Pattern:** Construcción de escenas y cámaras
- **Observer Pattern:** Notificación de progreso de renderizado
- **Factory Pattern:** Creación de ejecutores de operaciones CLI

Para más detalles, consulta `REPORTE.md`.

### Estructura de Salida

Las imágenes PNG se guardan en:

```
output/
├── render.png          # Salida por defecto
├── simple.png          # Ejemplo simple
└── cornell_box.png     # Escena compleja
```

### Recursos Adicionales

- **Código fuente:** `src/main/java/unam/ciencias/modeladoyprogramacion/raytracer/`
- **Tests unitarios:** `src/test/java/`
- **Escenas de ejemplo:** `examples/ray_tracer/`
- **Reporte de diseño:** `REPORTE.md`

### Soporte

Para reportar bugs o solicitar features:

1. Crear issue en GitHub
2. Incluir:
   - Versión de Java (`java --version`)
   - Archivo JSON de la escena
   - Comando ejecutado
   - Error completo

---

**Gracias por usar este Ray Tracer!**
