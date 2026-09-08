# Plan de Desarrollo: API REST - Fórmulas Magistrales (Avance 01)

## 1. Objetivo
Desarrollar el núcleo (backend) de una API REST para la gestión de un centro médico especializado en **Fórmulas Magistrales**. El alcance de este primer avance se limita a la configuración del entorno, endpoints (solo JSON), lógica de negocio y pruebas unitarias (TDD). No se desarrollarán interfaces gráficas.

## 2. Nuevo Modelo de Dominio (Entidades)
Para cubrir el flujo de fórmulas magistrales, el sistema se dividirá en 4 entidades clave:
*   **`Formula`**: Catálogo de preparados magistrales disponibles en el laboratorio.
*   **`Paciente`**: Cliente que requiere la preparación.
*   **`Medico`**: Profesional que prescribe la fórmula.
*   **`OrdenPreparacion`**: Entidad transaccional que une al Paciente, al Médico y a la Fórmula solicitada.

---

## 3. Asignación de Tareas por Integrante

Cada integrante es responsable de desarrollar el CRUD completo (Create, Read, Update, Delete) de su entidad asignada, respetando la arquitectura en capas: `controller`, `service`, `repository` y `model`.

| Integrante | Módulo Asignado | Responsabilidades Técnicas (Obligatorias) |
| :--- | :--- | :--- |
| **Integrante 1** | **Limpieza y `Formula`** | 1. Eliminar la entidad obsoleta `CitaMedica` y todos sus archivos asociados para limpiar errores de compilación.<br>2. Crear el CRUD de `Formula` (id, nombre, descripcion, instrucciones).<br>3. **TDD:** Validar que no se pueda registrar una fórmula sin nombre. |
| **Integrante 2** | **`Paciente`** | 1. Crear el CRUD de `Paciente` (id, nombre, dni, telefono).<br>2. Simular el repositorio con datos iniciales en memoria.<br>3. **TDD:** Validar que no se registre un paciente si el DNI ya existe. |
| **Integrante 3** | **`Medico`** | 1. Crear el CRUD de `Medico` (id, nombre, cmp, especialidad).<br>2. Simular el repositorio con datos iniciales en memoria.<br>3. **TDD:** Validar que no se registre un médico con un CMP vacío o inválido. |
| **Integrante 4** | **`OrdenPreparacion` y Postman** | 1. Crear el CRUD de `OrdenPreparacion` (id, pacienteId, medicoId, formulaId, cantidad).<br>2. Inyectar los 3 repositorios anteriores en el `OrdenPreparacionService` para validar dependencias.<br>3. **TDD:** Validar que no se registre la orden si la Fórmula, Paciente o Médico no existen.<br>4. Armar y exportar la colección general de pruebas en **Postman** (.json). |

---

## 4. Criterios de Aceptación (Checklist de la Rúbrica)
Para que un módulo se considere terminado, el integrante debe verificar que su código cumple con:
- [ ] **Arquitectura:** Clases ubicadas en los paquetes correctos (todo en minúsculas). Compila sin errores.
- [ ] **API REST:** Endpoints devuelven y reciben JSON. Se usan correctamente los verbos `GET`, `POST`, `PUT` y `DELETE`.
- [ ] **Estados HTTP:** Responder `201 Created` al registrar, `200 OK` al listar/buscar, y `400/404` para errores.
- [ ] **Inyección de Dependencias:** Uso de `@Service` en la lógica. Inyección de repositorios simulados **mediante constructor** (prohibido usar `@Autowired` en los atributos).
- [ ] **Pruebas (TDD):** Mínimo 3 pruebas unitarias por servicio utilizando `JUnit 5` y `Mockito`.