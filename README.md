# 🌱 SemillaIX - Sistema de Gestión de Semillero y Vivero

Aplicación cliente de escritorio desarrollada en **JavaFX** con **Maven** y **Scene Builder**, diseñada para conectarse a un backend mediante una **API REST** con autenticación **JWT**.

---

## 📋 Características

- **Autenticación JWT:** Inicio de sesión seguro con correo y contraseña.
- **Diferenciación de roles:**
  - `admin`: Gestión completa de productores y lotes (altas, modificaciones, bajas).
  - `user`: Consulta de productores y lotes, y registro de lotes propios.
- **Gestión de Productores:** Listado con búsqueda por nombre o DNI/CUIT.
- **Ficha de Productor y Lotes:** Visualización de la información del productor y tabla de sus lotes asociados.
- **Arquitectura limpia:** Separación en capas (Modelos, Vistas FXML, Controladores y Servicio REST).

---

## 📁 Estructura del Proyecto

```text
navas1/
├── pom.xml                                      # Configuración Maven y dependencias
├── .gitignore
├── README.md
├── src/main/
│   ├── java/com/semillaix/
│   │   ├── App.java                             # Punto de entrada JavaFX
│   │   ├── model/                               # Entidades y sesión
│   │   │   ├── Usuario.java
│   │   │   ├── Productor.java
│   │   │   ├── Lote.java
│   │   │   └── Session.java
│   │   ├── service/                             # Cliente HTTP REST
│   │   │   └── ApiService.java
│   │   └── controller/                          # Controladores JavaFX
│   │       ├── LoginController.java
│   │       ├── ProductoresController.java
│   │       ├── ProductorFormController.java
│   │       ├── ProductorFichaController.java
│   │       └── LoteFormController.java
│   └── resources/com/semillaix/view/            # Vistas FXML para Scene Builder
│       ├── LoginView.fxml
│       ├── ProductoresView.fxml
│       ├── ProductorFormView.fxml
│       ├── ProductorFichaView.fxml
│       └── LoteFormView.fxml
```

---

## 🚀 Requisitos y Ejecución

- **Java JDK 17+**
- **Maven 3.8+**
- **Scene Builder** (para edición visual de vistas `.fxml`)

### Ejecución:
```bash
mvn clean javafx:run
```
