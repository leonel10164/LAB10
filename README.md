# Laboratorio 10 · CRUD con Retrofit II

Aplicación Android con Kotlin, Jetpack Compose y Retrofit, conectada a una API Django REST Framework.

## Funcionalidades

- CRUD de series: listar, consultar, crear, editar y eliminar con confirmación.
- CRUD de espacios EventFlow con autenticación mediante token.
- Validación de campos, fechas y números; estados de carga y errores HTTP.

## Estructura

- `app/`: aplicación Android, package `com.example.lab10`.
- `backend/`: servidor Django con las aplicaciones `series` y `logistica`.
- `informe/GLAB-S10-Lab10_desarrollado.docx`: guía desarrollada con capturas reales del código en Android Studio y del funcionamiento.
- `informe/capturas/`: evidencias originales.
- `informe/Lab10_Series_demo.mp4` y `informe/Lab10_EventFlow_demo.mp4`: videos de las pruebas.

## Ejecutar Django

Desde PowerShell, en la carpeta del repositorio:

```powershell
cd backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
Copy-Item .env.example .env
```

Asigna en `.env` una `SECRET_KEY` propia antes de iniciar. Puedes generar una con:

```powershell
python -c "from django.core.management.utils import get_random_secret_key; print(get_random_secret_key())"
```

Después ejecuta:

```powershell
python manage.py migrate
python manage.py createsuperuser
python manage.py runserver 0.0.0.0:8000
```

Rutas principales:

| Operación | Ruta |
| --- | --- |
| Estado del servidor | `http://127.0.0.1:8000/` |
| Listar o crear series | `http://127.0.0.1:8000/serie` |
| Consultar, editar o eliminar una serie | `http://127.0.0.1:8000/serie/{id}` |
| Obtener token | `http://127.0.0.1:8000/api/login/` |
| CRUD de espacios | `http://127.0.0.1:8000/api/espacios/` |
| CRUD de recursos técnicos | `http://127.0.0.1:8000/api/recursos/` |

Las escrituras de EventFlow usan `Authorization: Token <token>`. Las rutas de series permiten acceso sin autenticación para la práctica. La base de datos se crea con las migraciones y no incluye registros ni cuentas personales.

## Ejecutar Android

1. Abre la raíz del repositorio en Android Studio.
2. Sincroniza Gradle e instala Android SDK Platform 34 y Build Tools 34 si hacen falta.
3. Inicia Django y ejecuta la aplicación en el emulador.
4. La URL base configurada es `http://10.0.2.2:8000/`.
5. Para Espacios, inicia sesión con la cuenta Django creada anteriormente.

El proyecto utiliza AGP 8.6, Gradle 8.7 y JDK 17. Se verificó con Medium Phone API 35. La actividad Bookshelf es opcional y no forma parte de esta entrega.

## Verificación realizada

- Compilación satisfactoria desde Gradle y Android Studio.
- Instalación y ejecución del APK en el emulador.
- CRUD de Series y Espacios probado contra Django.
- Cinco pruebas existentes de EventFlow satisfactorias.

Para ejecutar las pruebas del backend:

```powershell
cd backend
python manage.py test logistica
```

Los commits registran la implementación, la separación de componentes, la documentación y la incorporación de las evidencias.
