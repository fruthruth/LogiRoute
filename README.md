# LogiRoute — Sistema de Logística, Rutas y Seguimiento de Pedidos

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![JSON Web Tokens](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C00.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)

**LogiRoute** es una plataforma web académica de nivel empresarial diseñada para la gestión logística integral, la optimización en la asignación de repartidores y el seguimiento de pedidos en tiempo real. El sistema ha evolucionado de una arquitectura manual a un ecosistema robusto basado en **Spring Boot**, utilizando persistencia avanzada con **Spring Data JPA**, seguridad por tokens (**JWT**) y vistas dinámicas renderizadas mediante **Thymeleaf**.

---

## 🚀 Características Clave

- **Gestión Integral de Pedidos e Incidentes:** Control total del ciclo de vida de los envíos y registro automatizado de contratiempos en ruta.
- **Seguridad y Control de Accesos:** Autenticación robusta basada en **Spring Security** y **JWT (JSON Web Tokens)** con separación de roles (Administrador, Cliente, Repartidor).
- **Asignación Inteligente:** Capa de servicios optimizada para el emparejamiento eficiente de vehículos, rutas y transportistas.
- **Auditoría Técnica Nativa:** Integración completa de trazas operativas utilizando la infraestructura de logging interna de **Logback** configurada para el entorno Spring.

---

## 📐 Estructura y Arquitectura del Proyecto

El sistema adopta la arquitectura oficial de capas de **Spring Boot**, promoviendo el desacoplamiento mediante inyección de dependencias y la separación limpia entre persistencia, lógica de negocio y presentación.
