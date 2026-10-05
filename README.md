# 🛒 ModaApp · Sprint 3: Base de Datos v2, Carrito y Pedidos con Teléfono

> **Rama:** `Sprint3` *(Incluye todo lo desarrollado en Sprint 1 y Sprint 2)*  
> **Objetivo del Sprint:** Actualizar la base de datos a la versión 2, incorporar edición y búsqueda en tiempo real de ropa, implementar el carrito de compras con control de cantidades y permitir el registro de pedidos comerciales identificando clientes por su teléfono.

---

## 🎯 Historias de Usuario Implementadas

### 📌 HU-07: Búsqueda, Edición y Eliminación de Ropa
- **CA1:** Edición de datos de una prenda (modelo, talla, color, precio, stock, categoría y foto) en `RopaFormActivity`.
- **CA2:** Eliminación segura con confirmación: valida mediante SQLite si la prenda tiene pedidos asociados (`tienePedidos`). Si tiene pedidos, bloquea la eliminación para proteger la integridad referencial.
- **CA3:** Búsqueda en tiempo real en `RopaActivity` por modelo, marca o color mediante `TextWatcher` y consulta `LIKE` insensible a mayúsculas.

### 📌 HU-08: Carrito de Compras
- **CA1:** Botón "Agregar al Carrito" en cada prenda del catálogo con selector de cantidad ($1 \dots \text{stock}$).
- **CA2:** Singleton `Carrito` en memoria para persistencia durante la sesión de compra.
- **CA3:** Contador / badge dinámico en el icono de carrito de la barra superior.
- **CA4:** Pantalla `CarritoActivity` con listado de prendas, precio unitario, cantidad, cálculo de subtotal y total general.
- **CA5:** Botón para eliminar prendas del carrito con recálculo automático y vaciado si queda sin ítems.

### 📌 HU-09: Identificación de Cliente y Registro de Pedidos
- **CA1:** Pantalla `PedidoCheckoutActivity`: ingreso del número de teléfono celular (exactamente 9 dígitos numéricos).
- **CA2:** Consulta instantánea a SQLite en tabla `cliente`:
  - Si el cliente ya existe: saluda por su nombre y autocompleta los datos.
  - Si es cliente nuevo: solicita nombres y apellidos y lo registra en la base de datos.
- **CA3:** Registro de pedido y sus líneas de detalle utilizando una **transacción atómica SQLite** (`db.beginTransaction()`). Si ocurre cualquier fallo, revierte la operación asegurando consistencia.
- **CA4:** Generación del pedido en estado `"PENDIENTE"` y redirección a `PedidoConfirmadoActivity`.

---

## 🗄️ Modelo de Datos SQLite (Versión 2)

Se añaden 3 nuevas tablas mediante `DBHelper.onUpgrade()`:

```sql
CREATE TABLE cliente (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    telefono TEXT UNIQUE NOT NULL,
    nombres TEXT NOT NULL,
    apellidos TEXT NOT NULL,
    fecha_registro TEXT NOT NULL
);

CREATE TABLE pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER NOT NULL,
    fecha TEXT NOT NULL,
    total REAL NOT NULL,
    estado TEXT NOT NULL DEFAULT 'PENDIENTE',
    fecha_atencion TEXT,
    FOREIGN KEY(id_cliente) REFERENCES cliente(id)
);

CREATE TABLE detalle_pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_pedido INTEGER NOT NULL,
    id_ropa INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unit REAL NOT NULL,
    subtotal REAL NOT NULL,
    FOREIGN KEY(id_pedido) REFERENCES pedido(id),
    FOREIGN KEY(id_ropa) REFERENCES ropa(id)
);
```

---

## 🚀 Cómo Ejecutar

```bash
git checkout Sprint3
./gradlew assembleDebug
```
