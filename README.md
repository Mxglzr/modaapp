# 🚀 ModaApp · Sprint 4: WhatsApp, Gestión de Pedidos, Reportes y APK

> **Rama:** `Sprint4` *(Incluye la totalidad de los Sprints 1, 2, 3 y 4)*  
> **Objetivo del Sprint:** Integrar el contacto directo por WhatsApp para pedidos, implementar el ciclo de atención comercial con descuento automático de stock en SQLite, proveer métricas del negocio y directorio de clientes, persistencia de sesión con `SharedPreferences` y entrega del APK final compilado.

---

## 🎯 Historias de Usuario Implementadas

### 📌 HU-10: Notificación y Contacto por WhatsApp
- **CA1 (WhatsApp al Cliente):** En `PedidoConfirmadoActivity`, genera un enlace universal (`https://wa.me/51...`) con mensaje prearmado que incluye: saludo boutique, número de pedido, lista de prendas pedidas (modelo, talla, color, cantidad y subtotal), monto total a pagar y estado PENDIENTE.
- **CA2 (WhatsApp a la Tienda):** Botón alternativo para enviar alerta de nuevo pedido recibido al número de la tienda / administrador con datos completos del cliente y resumen.
- **CA3 (Lanzamiento nativo):** Abre directamente la app oficial de WhatsApp vía `Intent.ACTION_VIEW` con fallback seguro a navegador web si la app no estuviera instalada.

### 📌 HU-11: Atención de Pedidos y Descuento de Stock
- **CA1:** Pantalla `PedidosActivity` con selector tipo pestañas (`ChipGroup`) para filtrar entre pedidos **"Pendientes"** y **"Atendidos"**.
- **CA2:** Cuadro de diálogo modal detallado con la información del comprador, teléfono, prendas solicitadas y totales.
- **CA3:** Botón **"Marcar como Atendido"**:
  - Verifica previamente si hay stock suficiente en cada prenda. Si alguna no tiene stock, bloquea la acción y alerta al administrador.
  - Ejecuta una transacción atómica SQLite que descuenta las cantidades del stock y actualiza el estado a `"ATENDIDO"` registrando la fecha y hora de atención.

### 📌 HU-12: Reportes, Alertas de Stock y Directorio de Clientes
- **CA1:** Pantalla `ReportesActivity` con cálculo automático de:
  - Total vendido en el mes (sumatoria de pedidos en estado ATENDIDO en S/.).
  - Cantidad de pedidos atendidos en el mes.
- **CA2:** Lista de stock con pastillas de alerta visual en rojo pastel para prendas con stock crítico ($\le 3$).
- **CA3:** Pantalla `ClientesActivity` con listado de clientes, número de teléfono, total de pedidos acumulados y botón de contacto rápido por WhatsApp.

### 📌 HU-13: Sesión de Usuario y APK Final
- **CA1:** Casilla de verificación *"Recordar sesión"* en el Login que persiste las credenciales mediante `SharedPreferences`. Al reabrir la app, ingresa directamente al Menú sin solicitar login nuevamente.
- **CA2:** Diálogo de confirmación al presionar *"Cerrar Sesión"* en el Menú que borra las preferencias y redirige al Login.
- **CA3:** Generación y entrega del archivo ejecutable compilado listo para pruebas e instalación en la carpeta `apk/` del repositorio: [ModaApp.apk](file:///C:/Users/SENATI/Music/PROYECTOS_ANDROID/MODAAPP/apk/ModaApp.apk).

---

## 📦 Descarga del APK Compilado

El archivo ejecutable listo para instalar se encuentra disponible en:
- `apk/ModaApp.apk` (8.49 MB)

---

## 🚀 Cómo Ejecutar

```bash
git checkout Sprint4
./gradlew assembleDebug
```
