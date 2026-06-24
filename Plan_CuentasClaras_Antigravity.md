# Cuentas Claras MX — Plan de construcción y contexto para Antigravity

> **Documento maestro del proyecto.** Esto es lo que le vas a dar a Antigravity para que entienda
> *qué* construimos, *cómo* está estructurado y *en qué orden* avanzar.
>
> **Nombre provisional:** *Cuentas Claras MX* (va con tu objetivo: certeza y trazabilidad; cámbialo si quieres).
>
> **Tu tercera app.** Las primeras fases (proyecto, Room, primer CRUD) ya te van a sonar de AutoBitácora y
> NutriAyuno; aquí lo nuevo es la **lógica** (presupuesto, arrastre, MSI). Es la app más completa de las tres,
> así que tiene **más fases — pero cada una más chica y clara**. Una a la vez.

---

## 0. Cómo usar este documento

1. Abre **Antigravity IDE**, crea un proyecto/carpeta nuevo.
2. Pega como contexto inicial el **Prompt de arranque** de la sección 9.
3. Si puedes, pega también este documento completo como `CONTEXTO.md` en la raíz.
4. Avanza **una fase a la vez** (sección 8). No pases de fase hasta que el *checkpoint* funcione.
5. Al terminar cada fase, "haz commit de lo que llevamos".
6. **Ajuste recomendado en Antigravity:** Terminal Execution Policy en **Auto** (corre lo seguro solo, frena lo destructivo). No uses Turbo mientras aprendes.

> Si algo no se entiende: *"explícamelo como si nunca hubiera programado Android"*.

---

## 1. Resumen del proyecto

**Qué es:** una app Android para **registrar y controlar las finanzas personales**: cuánto tienes y cuánto debes (patrimonio), repartido en cuentas (efectivo, débito, banco, crédito, inversiones, préstamos), con un **presupuesto mensual por categorías y subcategorías**, y registro de cada gasto con su trazabilidad.

**Método (importante):** lo que el usuario describió es un método probado — **presupuesto base cero / por sobres** (estilo YNAB) + **registro de patrimonio**. No lo estamos inventando; lo estamos localizando para México.

**Para quién:** personas en México que quieren orden y certeza en su dinero, llevándolo a mano (sin conexión bancaria).

**El gancho que la diferencia:** **Meses Sin Intereses (MSI)** bien modelados + 100% en español + pensado para cuentas y bancos mexicanos. Ninguna app gringa maneja bien los MSI, y aquí los sufrimos todos.

**Filosofía:** es una herramienta de **registro y presupuesto**, NO de consejos financieros (más simple y sin líos regulatorios).

**Idioma:** español (México). **Moneda:** MXN.

**Modelo de negocio:** *freemium* (gratis con anuncios + desbloqueo "Pro").

---

## 2. Decisiones clave de diseño (léelas, son el corazón)

### a) Dos capas separadas pero conectadas
La app maneja **dos sistemas distintos**. Mantenerlos limpios es *la* decisión más importante (es donde se traban todas las apps caseras):

- **Cuentas = ¿dónde está el dinero?** → tu patrimonio. Activos (banco, efectivo, débito, inversión) menos pasivos (tarjetas de crédito, préstamos).
- **Presupuesto = ¿para qué es el dinero?** → tus categorías/sobres con su monto mensual.

Un **gasto toca las dos capas**: saca dinero de una cuenta *y* consume presupuesto de una categoría.

### b) Tarjetas de crédito y MSI en la v1 (versión pragmática)
- Las **tarjetas de crédito SÍ** van, como cuentas de tipo **deuda**.
- Un **gasto con tarjeta** aumenta la deuda de la tarjeta *y* consume presupuesto (igual que un gasto normal).
- **Pagar la tarjeta** es una **transferencia** (de una cuenta de activo → la tarjeta) que baja la deuda; **NO** es un gasto nuevo ni toca presupuesto.
- Los **MSI SÍ** van, en **versión simplificada**: registras la compra y la app genera la mensualidad de cada mes y lleva el saldo pendiente como deuda.
- **Para v2 (fuera de v1):** la mecánica completa estilo YNAB de "reservar el pago de la tarjeta", el cálculo de intereses y las fechas de corte/pago.

### c) La regla de arrastre (rollover): configurable por categoría
- **Acumular:** el remanente positivo se junta mes con mes (sobres tipo "seguro del auto", "vacaciones").
- **Reiniciar:** el remanente positivo se borra y el sobre empieza limpio ("comida", "entretenimiento").
- **Sobregiro (remanente negativo):** **siempre se arrastra** restando del mes siguiente, salvo que lo cubras moviendo presupuesto de otra categoría dentro del mismo mes.
- Cada categoría tiene un **monto por defecto** que se rellena solo cada mes (editable).

### d) La regla de "no presupuestar más que el ingreso" — hazla visible
`Disponible para asignar (mes) = ingresos del mes − suma de presupuestos asignados`. Muéstralo en vivo y **ponlo en rojo si es negativo**.

---

## 3. Alcance de la versión 1 (MVP)

### ✅ Dentro del alcance
- Cuentas de todo tipo (efectivo, débito, banco, crédito/deuda, inversión, préstamo) + **patrimonio neto**.
- Transacciones de tres tipos: **ingreso, gasto, transferencia**.
- **Categorías madre** (solo agrupan/totalizan) y **subcategorías** (tienen presupuesto y reciben gastos).
- Presupuesto mensual base cero + indicador "disponible para asignar".
- Gasto que **consume el presupuesto** de su subcategoría.
- **Motor de arrastre** (acumular / reiniciar + sobregiro arrastra) + reasignar presupuesto entre categorías en el mes.
- **MSI** (versión simplificada): genera mensualidades y lleva la deuda pendiente.
- Transacciones **recurrentes** (renta, sueldo, suscripciones) y **foto de recibo** en cada gasto.
- Tablero con patrimonio, saldos y estado del presupuesto + gráficas.
- **Manual, sin conexión bancaria** (a propósito).

### 🚫 Fuera del alcance de la v1
- Conexión / sincronización bancaria automática (cara, poca cobertura en MX; quizá nunca).
- Mecánica YNAB completa de reserva de pago de tarjeta; intereses; fechas de corte.
- Respaldo en la nube; multimoneda; cuentas de usuario/login.
- Consejos o recomendaciones financieras/de inversión (solo registro y presupuesto).
- Versión iOS.

> Dile a Antigravity que **respete este alcance**. En esta app la tentación de agregar de más es enorme.

---

## 4. Modelo de datos

Estas son las "tablas" (entidades de Room). Antigravity las crea en la Fase 1.

### `Cuenta`
- `id` (PK), `nombre`
- `tipo` — efectivo / débito / banco / crédito / inversión / préstamo / otro
- `esPasivo` — booleano (true para crédito y préstamo: representa deuda)
- `saldoInicial`, (`saldoActual` se calcula)
- `limiteCredito` *(opcional, para tarjetas)*
- `archivada` — booleano

### `Categoria`
- `id` (PK), `nombre`
- `categoriaPadreId` *(nullable)* — **null = categoría madre (solo agrupa); con valor = subcategoría**
- `presupuestoMensualDefault` — **solo subcategorías** (las madre suman a sus hijas)
- `modoArrastre` — acumular / reiniciar (**solo subcategorías**)
- `icono`, `color` *(opcionales)*

### `PresupuestoMensual` (clave para el arrastre)
- `id` (PK), `subcategoriaId` (FK), `mes` (formato AAAA-MM)
- `montoAsignado` — presupuesto de ese mes
- `arrastre` — lo que entró del mes anterior
- (`gastado` se calcula de las transacciones; `disponible` se calcula)

### `Transaccion`
- `id` (PK), `tipo` — ingreso / gasto / transferencia
- `fecha`, `monto`, `descripcion`
- `cuentaId` (FK) — cuenta afectada (en transferencia, el "origen")
- `cuentaDestinoId` *(nullable, FK)* — solo transferencia ("destino")
- `subcategoriaId` *(nullable, FK)* — para ingreso/gasto; **null en transferencia**
- `fotoReciboUri` *(nullable)*
- `compraMsiId` *(nullable, FK)* — si la generó una compra a MSI
- `recurrenteId` *(nullable, FK)* — si la generó una regla recurrente

### `CompraMSI` (el diferenciador)
- `id` (PK), `descripcion`
- `montoTotal`, `numeroMeses`, `mesInicio`
- `cuentaId` (FK, la tarjeta), `subcategoriaId` (FK, dónde pega cada mensualidad)
- `montoMensual` (= montoTotal ÷ numeroMeses)
- (`saldoPendiente` y `mesesPagados` se calculan)

### `TransaccionRecurrente`
- `id` (PK), `tipo`, `monto`, `descripcion`
- `cuentaId` / `subcategoriaId`
- `frecuencia` (mensual…), `diaDelMes`, `activa`

### `Ajustes`
- `moneda` (MXN), día de inicio de mes, preferencias.

### Cálculos (explícaselos a Antigravity)
- `saldoActual` de una cuenta = `saldoInicial` + efecto de todas sus transacciones.
- `patrimonioNeto` = Σ saldos de activos − Σ saldos de pasivos.
- `disponible` de subcategoría (mes) = `montoAsignado` + `arrastre` − `gastado`.
- `disponible`/total de categoría madre = Σ de sus subcategorías.
- `disponibleParaAsignar` (mes) = ingresos del mes − Σ `montoAsignado` del mes.

---

## 5. Las reglas del motor (con ejemplos)

### Cómo afecta cada transacción
| Tipo | Efecto en cuenta | Efecto en presupuesto |
|---|---|---|
| Ingreso | sube el saldo de la cuenta | suma a "disponible para asignar" |
| Gasto (con débito/efectivo) | baja el saldo de esa cuenta | consume presupuesto de la subcategoría |
| Gasto (con tarjeta de crédito) | **sube la deuda** de la tarjeta | consume presupuesto (igual) |
| Transferencia | mueve saldo entre dos cuentas | **no toca presupuesto** |
| Pago de tarjeta | transferencia activo → tarjeta (baja deuda) | **no es gasto** |

### Arrastre — fórmula y ejemplos
Cada mes, por subcategoría: `disponible = montoAsignado + arrastre − gastado`.
Al cerrar el mes, el `arrastre` para el mes siguiente:
- Si el remanente ≥ 0 → `arrastre = remanente` solo si la categoría es **acumular**; si es **reiniciar**, `arrastre = 0`.
- Si el remanente < 0 (sobregiro) → `arrastre = remanente` **siempre**.

**Ejemplo "Comida" (reiniciar, default $4,000):**
- Mes 1: asignado 4,000, gastado 3,500 → remanente +500 → reinicia → arrastre 0.
- Mes 2: disponible = 4,000.

**Ejemplo "Seguro auto" (acumular, default $1,000):**
- Mes 1: asignado 1,000, gastado 0 → remanente +1,000 → acumula.
- Mes 2: disponible = 1,000 + 1,000 = **2,000**.

**Ejemplo sobregiro "Entretenimiento" (default $1,500):**
- Mes 1: asignado 1,500, gastado 2,000 → remanente −500 → siempre arrastra.
- Mes 2: disponible = 1,500 + (−500) = **1,000**. (O cubres los −500 en el mes 1 moviendo presupuesto de otra categoría.)

### MSI — ejemplo
"Refrigerador a 12 MSI", total $12,000, tarjeta "BBVA Crédito", subcategoría "Hogar", inicia enero:
- `montoMensual` = 1,000.
- Cada mes (ene–dic) la app genera un **gasto de $1,000** a "Hogar" que consume su presupuesto.
- `saldoPendiente` empieza en 12,000 y baja $1,000/mes, reflejado como **deuda de la tarjeta**.
- Resultado: ves "este mes debo $1,000 de MSI" en presupuesto y "$11,000 de MSI pendientes" en patrimonio.

---

## 6. Diseño responsable y privacidad — léelo

Una app de finanzas guarda datos muy sensibles. Hacerlo bien es tu mejor argumento de venta:

1. **Todo local y privado.** Los datos viven en el teléfono. Úsalo como ventaja ("tus finanzas no salen de tu dispositivo"). Necesitarás una **política de privacidad** por usar AdMob.
2. **Nunca pidas credenciales bancarias** (contraseñas, números completos de tarjeta, NIP). La app es manual; registra saldos, no accesos. No guardes datos sensibles de ese tipo.
3. **No es asesoría financiera.** Es registro y presupuesto. Incluye un aviso: *"Esta app es una herramienta de registro; no constituye asesoría financiera ni de inversión."*
4. **Cumple políticas de Play** para apps financieras (no eres prestamista ni casa de inversión, así que es directo, pero declara bien la categoría y el manejo de datos).

---

## 7. Principios de diseño / UX

- Lo más frecuente —**registrar un gasto**— a un toque desde el inicio, rápido (monto, categoría, cuenta, listo).
- El inicio debe responder de un vistazo: "¿cuánto tengo?" y "¿cómo voy con el presupuesto este mes?".
- **Español natural de México**; montos en MXN, fechas locales.
- Material 3, color principal + modo claro/oscuro.
- Tono neutro y claro; cero regaños.

---

## 8. Plan de construcción por fases

Cada fase: **objetivo**, **qué se construye**, **qué aprendes**, **checkpoint**.

### Fase 0 — Entorno y proyecto base
- **Objetivo:** proyecto corriendo en el emulador/teléfono.
- **Construye:** proyecto Kotlin + Compose, dependencias base, pantalla "Hola Cuentas Claras".
- **Aprendes:** estructura del proyecto, Gradle, correr la app.
- **Checkpoint:** la app abre. ✅

### Fase 1 — Capa de datos (Room) + categorías por defecto
- **Objetivo:** crear toda la base de datos local.
- **Construye:** todas las entidades (sección 4), DAOs, `RoomDatabase`, repositorios; y un **seed** con una estructura de categorías típica (Vivienda, Alimentación, Transporte, etc. con subcategorías).
- **Aprendes:** entidades, relaciones, importar datos iniciales.
- **Checkpoint:** la base existe y trae las categorías por defecto. ✅

### Fase 2 — Cuentas y patrimonio
- **Objetivo:** "¿cuánto tengo y debo?".
- **Construye:** alta/edición/baja de cuentas con su tipo (incl. crédito/deuda), lista de cuentas con saldos, y el cálculo de **patrimonio neto**.
- **Aprendes:** CRUD en Compose, ViewModel, cálculos de saldo.
- **Checkpoint:** creas tus cuentas y ves saldos y patrimonio neto; persisten. ✅

### Fase 3 — Transacciones básicas (ingreso y gasto)
- **Objetivo:** registrar dinero que entra y sale.
- **Construye:** registrar ingreso y gasto (sin transferencias aún), asignar el gasto a una subcategoría, actualizar el saldo de la cuenta; y un **ledger** (lista de movimientos) buscable.
- **Aprendes:** relacionar transacción ↔ cuenta ↔ categoría; formularios.
- **Checkpoint:** registras un gasto desde una cuenta; el saldo baja y el movimiento aparece en el ledger. ✅

### Fase 4 — Categorías y presupuesto mensual
- **Objetivo:** armar los sobres.
- **Construye:** gestión de categorías madre y subcategorías (relación padre/hija), asignar presupuesto a cada subcategoría, total de la madre = suma de hijas, y el indicador **"disponible para asignar"** (rojo si te pasas).
- **Aprendes:** datos jerárquicos (padre/hijo), agregaciones.
- **Checkpoint:** asignas presupuestos y el indicador de "disponible para asignar" funciona. ✅

### Fase 5 — Motor de gasto vs. presupuesto
- **Objetivo:** conectar gastos con sobres.
- **Construye:** que cada gasto **consuma el disponible** de su subcategoría; mostrar disponible por subcategoría y por categoría madre.
- **Aprendes:** unir las dos capas (cuentas + presupuesto) sin mezclarlas.
- **Checkpoint:** registrar un gasto reduce el disponible de su categoría. ✅

### Fase 6 — Transferencias y pago de tarjetas
- **Objetivo:** mover dinero sin que cuente como gasto.
- **Construye:** transacción tipo **transferencia** (mueve saldo entre dos cuentas, **no** toca presupuesto); pagar una tarjeta de crédito como transferencia que **baja la deuda**.
- **Aprendes:** el tercer tipo de transacción y por qué no afecta el presupuesto.
- **Checkpoint:** transfieres entre cuentas sin tocar presupuesto; pagar la tarjeta baja su deuda. ✅

### Fase 7 — Motor de arrastre (rollover)
- **Objetivo:** que los sobres se regeneren cada mes correctamente.
- **Construye:** lógica de cambio de mes según la sección 5 (acumular / reiniciar; el sobregiro siempre arrastra), rellenar presupuestos por defecto, y **reasignar presupuesto entre categorías** dentro del mes.
- **Aprendes:** lógica de negocio por periodos (meses), casos límite.
- **Checkpoint:** avanzas de mes y los remanentes/sobregiros se arrastran según la regla de cada categoría. ✅

### Fase 8 — Meses Sin Intereses (MSI) — el diferenciador
- **Objetivo:** modelar compras a MSI.
- **Construye:** registrar una `CompraMSI` (total, meses, tarjeta, subcategoría); generar la **mensualidad** de cada mes que consume presupuesto; llevar el **saldo pendiente** como deuda de la tarjeta.
- **Aprendes:** generar transacciones programadas y reflejarlas en dos lugares (presupuesto y deuda).
- **Checkpoint:** registras "Refri a 12 MSI" y ves la mensualidad en el presupuesto y la deuda MSI pendiente. ✅

### Fase 9 — Recurrentes y foto de recibo
- **Objetivo:** automatizar lo repetitivo y reforzar la trazabilidad.
- **Construye:** reglas **recurrentes** (renta, sueldo, suscripciones) que generan transacciones; adjuntar **foto del recibo** a un gasto.
- **Aprendes:** programar tareas (WorkManager) y manejar imágenes/permisos.
- **Checkpoint:** una regla recurrente genera su transacción; puedes adjuntar una foto a un gasto. ✅

### Fase 10 — Tablero y gráficas
- **Objetivo:** ver todo de un vistazo.
- **Construye:** tablero con patrimonio neto, saldos por cuenta, estado del presupuesto del mes, gráfica de gasto por categoría y tendencia.
- **Aprendes:** juntar datos de varias tablas y graficarlos (Vico).
- **Checkpoint:** el tablero muestra tus datos reales. ✅

### Fase 11 — Identidad visual, tema y localización
- **Objetivo:** que se vea pro y 100% en español.
- **Construye:** tema Material 3 (color, claro/oscuro), ícono, splash, textos en `strings.xml`, formato MXN y de fechas.
- **Checkpoint:** app consistente, con identidad y en español. ✅

### Fase 12 — Monetización (AdMob + Pro)
- **Objetivo:** activar el freemium.
- **Construye:**
  - **Gratis:** cuentas y categorías básicas, historial corto, banner discreto.
  - **Pro (desbloqueo único, ~$149–199 MXN):** sin anuncios, **módulo MSI**, recurrentes, foto de recibo, cuentas/categorías ilimitadas, exportar a CSV/PDF.
- **Aprendes:** AdMob, Play Billing, "bloquear" funciones tras el Pro.
- **Checkpoint:** banner de **prueba** + compra de **prueba** desbloquea Pro. ✅
- **Nota:** usa IDs de prueba de AdMob y licencias de prueba de Play hasta publicar.

### Fase 13 — Publicar en Google Play
- **Objetivo:** subir a la pista de prueba.
- **Construye:** App Bundle (.aab) firmado, **política de privacidad**, ficha de tienda, sección de seguridad de datos, clasificación, subir a *internal testing*.
- **Checkpoint:** la app está en la pista de prueba y la puedes instalar. ✅

> ⚠️ **Lo que haces tú directamente:** cuenta de Google Play Console (~$25 USD una vez; si ya la tienes de tus apps anteriores, listo), datos de pago, aceptar términos y publicar. Para finanzas, cuida las **políticas de Play** y el manejo de datos (sección 6).

### Fase opcional (v2+)
Reserva de pago de tarjeta estilo YNAB completo, intereses y fechas de corte, respaldo en la nube, multimoneda. **Conexión bancaria: probablemente nunca** (cara y con poca cobertura en MX).

---

## 9. Prompts listos para Antigravity

### 🚀 Prompt de arranque (pégalo como primer mensaje)

```
Vas a ser mi mentor y guía paso a paso para construir una app Android.
Tengo poca experiencia en Android (es mi tercera app). NO asumas que domino
Kotlin, Jetpack Compose ni las herramientas. Sí tengo experiencia en bases de
datos y backend, así que con SQL y lógica voy bien.

Vamos a construir "Cuentas Claras MX": una app de finanzas personales con
método de presupuesto base cero / por sobres + registro de patrimonio,
localizada para México (incluye Meses Sin Intereses, MSI).

CONCEPTO CLAVE — dos capas separadas pero conectadas:
- Cuentas (dónde está el dinero): activos menos pasivos = patrimonio.
- Presupuesto (para qué es el dinero): categorías madre que agrupan y
  subcategorías que tienen presupuesto mensual.
Un gasto toca las dos: baja una cuenta (o sube deuda de una tarjeta) Y consume
presupuesto de una subcategoría. Las transferencias NO tocan presupuesto.

REGLAS IMPORTANTES:
- Arrastre por categoría: "acumular" o "reiniciar"; el sobregiro siempre se
  arrastra al mes siguiente.
- Disponible para asignar = ingresos del mes − suma de presupuestos (rojo si <0).
- MSI (simplificado): registrar la compra genera la mensualidad de cada mes que
  consume presupuesto, y lleva el saldo pendiente como deuda de la tarjeta.

Stack: Kotlin + Jetpack Compose + Material 3, Room, MVVM, Navigation Compose,
WorkManager (recurrentes/MSI), gráficas con Vico, AdMob y Play Billing.
minSdk 26, solo vertical. Todo manual y local, SIN conexión bancaria.

Es una herramienta de registro/presupuesto, NO de asesoría financiera; datos
locales y privados; nunca pedir credenciales bancarias.

Trabajaremos por FASES, en orden, sin saltarnos ninguna. (Te paso el plan de
fases y el modelo de datos a continuación.)

Cómo quiero que me guíes:
1. Una fase a la vez; no sigas hasta que yo confirme.
2. Explícame cada paso como si nunca hubiera programado Android.
3. Dime EXACTAMENTE qué archivo crear, dónde, y qué código poner (completo).
4. Explica en 1–2 frases cada concepto nuevo (ViewModel, DAO, etc.).
5. Al final de cada fase, dime cómo PROBAR que funciona (el checkpoint).
6. Respeta el alcance de la v1; no agregues funciones que no pedí.

Empecemos por la Fase 0: preparar el entorno y el proyecto base.
```

### 🔁 Para avanzar de fase

```
La Fase [N] funciona, ya probé el checkpoint.
Ayúdame a hacer commit y empecemos la Fase [N+1] con las mismas reglas:
paso a paso, explicándome todo y diciéndome qué archivos y código necesito.
```

### 🆘 Si te atoras

```
No entiendo esto: [pega el error o la duda].
Explícamelo como a un principiante total en Android, por qué pasa y cómo
arreglarlo, paso a paso.
```

---

## 10. Glosario rápido

- **Gradle:** arma la app y maneja las librerías.
- **SDK / minSdk / targetSdk:** kit de Android; versión mínima / de optimización.
- **Emulador:** teléfono Android virtual para probar.
- **Jetpack Compose / Composable:** forma moderna de hacer pantallas; un Composable dibuja un pedazo de UI.
- **Room / Entity / DAO:** base de datos local; Entity = tabla, DAO = consultas.
- **ViewModel:** guarda el estado y la lógica de una pantalla.
- **State / Flow:** la fuente de la verdad de los datos; al cambiar, la pantalla se actualiza sola.
- **Navigation:** moverte entre pantallas.
- **WorkManager:** ejecuta tareas programadas (recurrentes, MSI) aunque la app esté cerrada.
- **Ledger:** la lista de todos los movimientos.
- **Base cero / sobres:** método donde cada peso del ingreso se asigna a una categoría.
- **Patrimonio neto:** activos − pasivos.
- **MSI:** Meses Sin Intereses.
- **APK / AAB:** instalable; Play pide AAB, el APK sirve para pruebas.
- **AdMob / Play Billing:** anuncios / compras dentro de la app.
- **ASO:** optimizar la ficha de Play para que te encuentren.
- **Logcat:** consola de mensajes y errores en vivo.

---

## 11. Checklist de publicación (Fase 13)

- [ ] Cuenta de Google Play Console (si ya la tienes, listo) — *lo haces tú*
- [ ] Ícono (512×512) y gráfico de cabecera (1024×500)
- [ ] Capturas de pantalla (mínimo 2)
- [ ] Descripción (en español; palabras clave: "finanzas personales", "presupuesto", "gastos", "MSI")
- [ ] **Política de privacidad** publicada (URL) — obligatoria
- [ ] Aviso de que **no es asesoría financiera** (en la app y la ficha)
- [ ] Sección de seguridad de datos (Data Safety) — destaca que todo es local
- [ ] Clasificación de contenido
- [ ] App Bundle (.aab) firmado
- [ ] App en *Internal testing* y probada en tu teléfono
- [ ] (Después) promocionarla en tu canal **NullPointer** 🎥

---

*Hecho para Raúl — tu tercera app, la más completa. Una fase a la vez. 💸📊*
