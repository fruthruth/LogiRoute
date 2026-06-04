# LogiRoute - Sistema de Logística, Rutas y Seguimiento de Pedidos

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Markdown](https://img.shields.io/badge/Markdown-000000?style=for-the-badge&logo=markdown&logoColor=white)

LogiRoute es un sistema académico diseñado para la gestión logística integral de pedidos, optimización de asignación de repartidores y el seguimiento de entregas en tiempo real. Combina una arquitectura robusta en Java (Modelo-Controlador-DAO) acoplada a MySQL mediante JDBC, junto con un prototipo web interactivo para la exposición del flujo del negocio.

---

## Integrantes y Responsabilidades
* **Chambi Mamani, Sebastián** — Capa DAO (Acceso a Datos)
* **Coasaca Cconislla, Saul Teodoro** — Capa Web (Interfaz de Usuario)
* **Huamaní Fernández, Ruth Nayaly** — Capa Controller (Lógica de Control)
* **Palomino Valeriano, Anthony Olivery** — Capa Model (Entidades de Negocio)

---

##  Tecnologías y Librerías Utilizadas

| Tecnología / Librería | Uso dentro del Proyecto |
| :--- | :--- |
| **Java** | Implementación del núcleo, modelos, controladores y lógica de negocio. |
| **JDBC** | Conexión e intercambio de datos entre Java y el motor MySQL. |
| **MySQL** | Base de datos relacional local para persistencia de datos. |
| **HTML5 / CSS3 / JS** | Prototipo web interactivo con persistencia local (*LocalStorage*). |
| **SLF4J & Logback** | Framework de rastreo y auditoría técnica (*Logging*). |

---

##  Implementación de Logback (Auditoría Técnica)
Para mitigar riesgos de fallas técnicas y mantener un control estricto del flujo de ejecución, el sistema incorpora **Logback** bajo la fachada **SLF4J**. Debido a que el proyecto se gestiona de manera local sin un gestor de dependencias automatizado, se ha configurado la arquitectura manual mediante la carpeta `lib/`:

###  Componentes de la Arquitectura de Logs:
1. **`slf4j-api-2.0.18.jar` (Fachada):** Capa de abstracción. Permite declarar e invocar los eventos de log (`logger.info()`) en las clases sin amarrar el código a un motor específico.
2. **`logback-core-1.5.34.jar` (Núcleo):** Contiene la lógica pesada encargada del procesamiento de textos, gestión de memoria interna y escrituras físicas.
3. **`logback-classic-1.5.34.jar` (Puente/Formateador):** Acopla el motor con la fachada y procesa activamente las directivas del archivo `logback.xml` para estructurar las salidas por consola.

###  Archivos de Configuración Estructurales:
* **`.classpath`:** Configurado para instruir al entorno de desarrollo (IDE) a reconocer las librerías `.jar` dentro del entorno de ejecución.
* **`logback.xml`:** Contiene las reglas de negocio de los logs (formato de texto, marcas de tiempo, niveles de alerta `INFO/ERROR` y salida por consola).

###  Inyección en la Capa Modelo (Ejemplo base):
```java
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 

public class Administrador { 
    private static final Logger logger = LoggerFactory.getLogger(Administrador.class);
    // Lógica de la entidad...
}