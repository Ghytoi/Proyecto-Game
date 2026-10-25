1. Descripción del proyecto  
SubwaySurfer es un videojuego desarrollado en Java utilizando el framework libGDX.
El proyecto utiliza Gradle para gestionar sus dependencias, compilar el código y ejecutar la aplicación de escritorio.

2. Requisitos previos  
Para instalar y ejecutar el proyecto, se necesita:

  - Java JDK 8: versión compatible con la configuración de Gradle del proyecto.
  - Eclipse IDE: recomendado para importar, revisar y ejecutar el código fuente.
  - Gradle Wrapper: incluido en los archivos del proyecto, por lo que no es necesario instalar Gradle manualmente.

3. Instalación del proyecto  
  - Descargar el .zip del proyecto
  - Descomprimir el archivo .zip
  - Importar el proyecto dentro de eclipse IDE
  - En caso de no poder lanzar el proyecto revisar si el botón de "Run" está configurado,
    ya que en algunos casos este se configura automaticamente tras importar un archivo y en otros no

4. Ejecución del proyecto  
  - Una vez configurado lo básico solo basta con presionar el botón de "Run" dentro de eclipse
    y el software se ejecutará sin mayor problema

5. Estructura general del proyecto
```text
   SubwaySurfer/  
   ├── assets/  
   ├── core/  
   │  └── src/  
   │      └── com/  
   │          └── mygdx/  
   │              └── game/  
   │                  ├── entidades/  
   │                  ├── escenarios/  
   │                  ├── gestion/  
   │                  ├── jugador/  
   │                  ├── movimiento/  
   │                  ├── principal/  
   │                  ├── recogibles/  
   │                  └── recursos/  
   ├── desktop/  
   │   └── src/  
   │       └── com/  
   │           └── mygdx/  
   │               └── game/  
   │                   └── DesktopLauncher.java  
   ├── gradle/  
   ├── .gitignore
   ├── README.md
   ├── build.gradle  
   ├── gradle.properties  
   ├── gradlew  
   ├── gradlew.bat  
   └── settings.gradle  
```
- assets: contiene los recursos del videojuego, como imágenes, texturas, sonidos, música y otros archivos necesarios para su funcionamiento.
- core: contiene la lógica principal del videojuego, organizada en paquetes:
  - entidades: clases que representan las entidades del juego.
  - escenarios: clases relacionadas con los escenarios.
  - gestion: clases encargadas de administrar funcionalidades del videojuego.
  - jugador: clases relacionadas con el jugador.
  - movimiento: lógica de movimiento.
  - principal: clases principales del videojuego.
  - recogibles: elementos que el jugador puede recoger.
  - recursos: gestión de recursos utilizados por el juego.
- desktop: contiene el punto de entrada para ejecutar el videojuego como aplicación de escritorio.
- gradle: contiene los archivos del Gradle Wrapper.
- build.gradle: configura la compilación y las dependencias.
- gradle.properties: contiene propiedades utilizadas por Gradle.
- settings.gradle: define la estructura de los módulos del proyecto.
- gradlew y gradlew.bat: permiten ejecutar tareas de Gradle en sistemas compatibles.
