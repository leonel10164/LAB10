# Laboratorio 10 Retrofit II

Aplicación Android en Kotlin y Jetpack Compose que consume el backend Django entregado.

## Funciones implementadas
- Inicio con descripción de la práctica.
- Series: listado, consulta individual, creación, edición y eliminación confirmada.
- EventFlow: CRUD de espacios e inicio de sesión mediante token.
- Validación de fecha, campos obligatorios y números.
- Estado de carga y errores HTTP; el listado se actualiza después de confirmar el cambio en el servidor.

## Backend
La carpeta que contiene manage.py es:
C:\Users\Tecsup\Downloads\djangoApp08\djangoApp08\lab08

En su .env, conservar los valores existentes y agregar 10.0.2.2 a ALLOWED_HOSTS:
ALLOWED_HOSTS=127.0.0.1,localhost,10.0.2.2

Desde un entorno Python válido para el proyecto:
python manage.py migrate
python manage.py createsuperuser
python manage.py runserver 0.0.0.0:8000

Los entornos virtuales entregados conservan rutas de otra computadora; si no funcionan, crear un entorno local e instalar requirements.txt.
La aplicación usa http://10.0.2.2:8000/ para el emulador. Espacios requiere usuario Django; Series no requiere token.

## Ejecución
Abrir esta carpeta en Android Studio, sincronizar Gradle y ejecutar en Medium Phone API 35.
Se conserva compileSdk 34 de la plantilla. El SDK debe tener Platform 34 y Build Tools 34 disponibles.

## Evidencias para colocar en la guía
1. Habilitar acceso a la aplicación web: servidor activo y respuesta GET de /serie.
2. Consulta individual: GET /serie/{id} de un registro de prueba.
3. Modificación de fecha: PUT del registro y respuesta con la fecha cambiada.
4. Eliminación web: DELETE de ese registro de prueba y consulta posterior.
5. Probar y ejecutar Android: capturas del listado, formulario de creación, registro guardado, edición guardada y confirmación de eliminación de Series.
6. Ejercicio de desarrollo: capturas de inicio de sesión y creación, edición y eliminación de un Espacio de prueba.
7. Insertar cada captura debajo del paso correspondiente y antes del espacio para compartir el video.

Usar únicamente registros de prueba creados para la demostración. Para Espacios, eliminar los recursos asociados antes de intentar eliminar un espacio protegido.

## Explicación técnica para el informe
Retrofit declara los endpoints mediante anotaciones GET, POST, PUT y DELETE. Gson convierte las respuestas JSON a objetos Kotlin. Las llamadas suspend se ejecutan en corrutinas; Compose observa el estado para refrescar las pantallas. El formulario valida las entradas antes de enviar el cuerpo JSON. Después de una escritura se comprueba el estado HTTP, se consulta nuevamente el listado y se muestra el resultado. EventFlow obtiene un token con /api/login/ y lo envía en Authorization para las escrituras.

## Observación para adaptar a tu experiencia
La coordinación del guardado y la actualización de la lista requiere esperar la respuesta HTTP antes de cerrar el formulario. La implementación mantiene el formulario abierto si el servidor devuelve un error y vuelve a cargar el listado cuando la operación termina correctamente. Adapta esta explicación a lo que hayas comprobado al ejecutar la app.

## Uso de IA
Se utilizó IA para analizar la guía y el backend, escribir el cliente Retrofit y las pantallas Compose, agregar validaciones y revisar problemas de configuración. Se verificaron los CRUD en el emulador y se documentaron con capturas reales y videos.

## Estado de validación
- Compilación satisfactoria con Gradle y desde Android Studio.
- APK instalado y ejecutado en Medium Phone API 35.
- CRUD de Series verificado contra Django: creación, lectura, edición y eliminación.
- CRUD de Espacios verificado con inicio de sesión mediante token.
- Cinco pruebas existentes de EventFlow ejecutadas correctamente.
- Informe Word con capturas del código tomadas en Android Studio y evidencias del funcionamiento.
- Videos de Series y EventFlow incluidos en informe/.
- Bookshelf corresponde a la actividad opcional y no está implementado.

## Archivos de la entrega
- informe/GLAB-S10-Lab10_desarrollado.docx
- informe/Lab10_Series_demo.mp4
- informe/Lab10_EventFlow_demo.mp4
- informe/capturas/: capturas completas originales, incluidas las de Android Studio.

## Ejecutar nuevamente el backend
El entorno local preparado está en:
C:\Users\Tecsup\Documents\Codex\2026-10-23\ana\work\backend-venv

Desde la carpeta del backend, ejecutar con ese Python:
python manage.py runserver 0.0.0.0:8000

La cuenta de pruebas se llama lab10_demo. Se puede asignar una nueva contraseña con:
python manage.py changepassword lab10_demo

Las claves y la base de datos del backend no se incluyen en el repositorio Android.
