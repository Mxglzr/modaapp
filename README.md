# 🌸 ModaApp · Sprint 1: Login, Menú y Navegación

> **Rama:** `Sprint1`  
> **Objetivo del Sprint:** Implementar la identidad visual de la tienda de ropa, pantalla de acceso (Login) con navegación condicional y el menú principal del administrador con tarjetas de acceso.

---

## 🎯 Historias de Usuario Implementadas

### 📌 HU-01: Autenticación y Pantalla de Login
- **CA1:** Formulario de inicio de sesión con campos de texto outlined legibles para Usuario y Contraseña.
- **CA2:** Validación de campos vacíos en cliente con alertas visuales de error (`TextInputLayout.error`).
- **CA3:** Navegación hacia `MenuActivity` al ingresar credenciales correctas.
- **CA4:** Bloqueo de avance con mensaje de error si las credenciales son incorrectas.
- **CA5:** Botón **"Ver Catálogo"** con acceso libre para clientes (sin requerir credenciales de administrador).

### 📌 HU-02: Menú Principal del Administrador
- **CA1:** Pantalla `MenuActivity` con saludo personalizado (`tvSaludoUsuario`) y rol del usuario.
- **CA2:** Tarjetas de navegación rápida (`MaterialCardView` con iconos temáticos de soporte):
  - 👗 **Gestión de Ropa:** Navega a `RopaActivity`.
  - 📋 **Pedidos:** Navega a `PedidosActivity`.
  - 👥 **Clientes:** Navega a `ClientesActivity`.
  - 📊 **Reportes:** Navega a `ReportesActivity`.
- **CA3:** Botón **"Salir"** que cierra el menú y retorna a la pantalla de Login.

### 📌 HU-03: Identidad Visual y Paleta de Colores
- **Paleta Oficial:** Rosa Boutique (`#E91E63`), Carbón / Negro Urbano (`#212121`), Fondo Claro (`#FDFBFC`).
- **Iconografía:** Diseñada completamente con vectores nativos XML (`res/drawable/ic_*.xml`), evitando emojis en componentes de interfaz.
- **Tipografía y Contraste:** Textos con alto contraste y visibilidad según pautas de Material Design 3.

---

## 🔐 Credenciales de Prueba

| Usuario | Contraseña | Rol |
| :--- | :--- | :--- |
| `admin` | `admin123` | Administrador |

---

## 🚀 Cómo Ejecutar

```bash
git checkout Sprint1
./gradlew assembleDebug
```
