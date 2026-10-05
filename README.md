# 👗 ModaApp · Sprint 2: Base de Datos SQLite v1, Ropa con Fotos y Catálogo

> **Rama:** `Sprint2` *(Incluye todo lo desarrollado en Sprint 1)*  
> **Objetivo del Sprint:** Implementar la base de datos local SQLite (`modaapp.db`), el registro y listado de prendas de vestir con captura de fotos desde la galería, y el catálogo interactivo para clientes clasificado por categorías de moda.

---

## 🎯 Historias de Usuario Implementadas

### 📌 HU-04: Autenticación en Base de Datos SQLite
- **CA1:** Creación de base de datos SQLite `modaapp.db` (versión 1) con tabla `usuario` y `categoria`.
- **CA2:** Precarga automática de usuario por defecto (`admin` / `admin123`) con rol `ADMIN`.
- **CA3:** Validación segura mediante consulta parametrizada con `?` en `UsuarioDao` para prevenir inyección SQL.

### 📌 HU-05: Registro de Ropa con Foto y Categoría
- **CA1:** Formulario de registro en `RopaFormActivity` con selector de fotos desde la galería del dispositivo.
- **CA2:** Guardado seguro de imágenes en el almacenamiento interno de la app (`filesDir/ropa_fotos`).
- **CA3:** Selectores desplegables (`AutoCompleteTextView`) para categorías (Polos, Vestidos, Casacas, Pantalones, Accesorios) y tallas estándar (XS, S, M, L, XL).
- **CA4:** Validaciones numéricas de stock ($\ge 0$) y precio unitario ($> 0$).
- **CA5:** Listado de prendas en `RopaActivity` mediante `RecyclerView` con diseño de tarjetas Material 3.

### 📌 HU-06: Catálogo de Prendas por Categorías
- **CA1:** Pantalla `CatalogoActivity` con cuadrícula de 2 columnas (`GridLayoutManager`).
- **CA2:** Filtro dinámico superior mediante `ChipGroup` con categorías cargadas directamente de SQLite.
- **CA3:** Visualización de foto de prenda, nombre del modelo, talla, categoría y precio en S/.

---

## 🗄️ Modelo de Datos SQLite (Versión 1)

```sql
CREATE TABLE usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario TEXT UNIQUE NOT NULL,
    clave TEXT NOT NULL,
    rol TEXT NOT NULL,
    telefono TEXT
);

CREATE TABLE categoria (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT
);

CREATE TABLE ropa (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_categoria INTEGER NOT NULL,
    modelo TEXT NOT NULL,
    marca TEXT NOT NULL,
    talla TEXT NOT NULL,
    color TEXT NOT NULL,
    precio REAL NOT NULL,
    cantidad INTEGER NOT NULL,
    foto TEXT,
    FOREIGN KEY(id_categoria) REFERENCES categoria(id)
);
```

---

## 🚀 Cómo Ejecutar

```bash
git checkout Sprint2
./gradlew assembleDebug
```
