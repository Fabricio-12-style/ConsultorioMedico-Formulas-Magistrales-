# Para Fabricio — arreglar la rama `feature/formulas-insumos`

## Qué pasó

Tu commit `4594bb2` ("Implementacion de los Crud Formula e Insumo") subió los 6 archivos, pero **todos pesan 0 bytes**. Están creados pero vacíos:

```
    0 bytes   FormulaMagistralService.java
    0 bytes   FormulaMagistralController.java
    0 bytes   InsumoService.java
    0 bytes   InsumoController.java
    0 bytes   FormulaMagistralServiceTest.java
    0 bytes   InsumoServiceTest.java
```

Seguramente hiciste `git add .` y `git commit` antes de pegar el código, o el editor no guardó.

Por eso tu rama **no se ha podido integrar** y `main` sigue sin Fórmulas ni Insumos. Sin eso, las preparaciones no se pueden probar: cualquier POST responde `400: La formula con ID X no tiene insumos registrados`.

---

## Pasos

### 1. Ir a tu rama

```bash
git checkout feature/formulas-insumos
```

### 2. Traer lo último de main

```bash
git checkout main
git pull
git checkout feature/formulas-insumos
git merge main
```

Si te marca conflicto en `src/test/resources/application.properties`, quédate con la versión de `main` y sigue:

```bash
git checkout main -- src/test/resources/application.properties
git add src/test/resources/application.properties
git commit
```

> Ese archivo ahora usa **H2 en memoria**, así que ya no necesita tu contraseña de MySQL. No lo vuelvas a editar: si le pones tus credenciales otra vez, rompes las pruebas de Jean y Jheymy.

### 3. Revisar si tu código sigue en tu máquina

Abre `src/main/java/ConsultorioMedico/service/FormulaMagistralService.java`.

- **Si tiene código** → solo te faltó commitear. Salta al paso 5.
- **Si está vacío** → sigue al paso 4.

### 4. Pegar el código

Está completo en **`docs/CODIGO_EQUIPO.md`**, en la sección **FABRICIO**. Cada bloque tiene arriba la ruta exacta del archivo. Son estos 6:

```
src/main/java/ConsultorioMedico/service/FormulaMagistralService.java
src/main/java/ConsultorioMedico/service/InsumoService.java
src/main/java/ConsultorioMedico/Controller/FormulaMagistralController.java
src/main/java/ConsultorioMedico/Controller/InsumoController.java
src/test/java/ConsultorioMedico/FormulaMagistralServiceTest.java
src/test/java/ConsultorioMedico/InsumoServiceTest.java
```

**Guarda cada archivo** (`Ctrl+S`) antes de pasar al siguiente.

### 5. Verificar que los archivos NO estén vacíos

Este es el paso que hay que hacer sí o sí, porque es lo que falló la vez pasada:

```bash
git diff --stat
```

Tiene que mostrar líneas agregadas en cada archivo, algo así:

```
 .../service/FormulaMagistralService.java   | 128 ++++++++++
 .../service/InsumoService.java             |  90 +++++++
 ...
```

**Si alguno sale con `0`, está vacío.** No sigas: vuelve al paso 4 y guarda bien.

### 6. Probar

```bash
mvn test
```

Tiene que decir **`BUILD SUCCESS`** y alrededor de **41 tests, 0 fallos**.

Si falla, **no subas nada** y avisa al grupo. Un push que rompe el build cuenta como "las pruebas presentadas no pasan" en la rúbrica.

### 7. Subir

```bash
git add .
git commit -m "CRUD de formulas magistrales e insumos"
git push origin feature/formulas-insumos
```

### 8. Pull Request

En GitHub: `base: main` ← `compare: feature/formulas-insumos`.

Antes de darle merge, entra a la pestaña **Files changed** y confirma que los 6 archivos muestran código. Si aparecen vacíos, el problema sigue ahí.

Avisa al grupo cuando esté mergeado.

---

## Checklist antes de subir

- [ ] `git merge main` hecho, sin conflictos pendientes
- [ ] Los 6 archivos abiertos y con código dentro
- [ ] `git diff --stat` muestra líneas en los 6, ninguno en `0`
- [ ] `mvn test` → `BUILD SUCCESS`
- [ ] No modifiqué `src/test/resources/application.properties`
- [ ] No toqué `pom.xml`, `model/` ni `repository/`

---

## Otra cosa

Existe también la rama **`feature/formula-configuracion`** (commit `2cc095d`), que nadie sabe qué contiene y no está integrada. Si es trabajo viejo o duplicado, bórrala para que no confunda en la entrega. Si tiene algo que sirve, avisa antes de que alguien la mergee por error.

Y ojo: en tu commit anterior quedó tu contraseña real de MySQL en el historial del repo. Es solo tu base local, pero conviene que la cambies.
