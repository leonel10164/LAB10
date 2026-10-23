# Guion para grabar la API Django

Inicia el backend y abre http://127.0.0.1:8000/ para mostrar la respuesta del servidor.

## 1 Listar las series

Abre http://127.0.0.1:8000/serie y muestra la respuesta JSON del GET.

## 2 Crear una serie

En la misma página, selecciona `application/json`, pega este contenido y pulsa POST:

```json
{
  "name": "VIDEO - Nueva serie",
  "release_date": "2026-10-23",
  "rating": 8,
  "category": "drama"
}
```

Anota el `id` devuelto. Se utiliza ese identificador en los pasos siguientes.

## 3 Consultar el registro creado

Abre `http://127.0.0.1:8000/serie/ID`, sustituyendo ID por el identificador obtenido. Muestra la respuesta del GET individual.

## 4 Modificar el registro

En Content, pega este JSON y pulsa PUT:

```json
{
  "name": "VIDEO - Serie modificada",
  "release_date": "2027-01-15",
  "rating": 10,
  "category": "action"
}
```

Muestra en la respuesta el nombre, la fecha, el rating y la categoría actualizados.

## 5 Eliminar el registro de prueba

En la misma URL pulsa DELETE y confirma. Luego pulsa GET y muestra la respuesta HTTP 404 Not Found.

## Explicación breve

La API utiliza GET para consultar, POST para crear, PUT para actualizar y DELETE para eliminar. Django REST Framework serializa los datos y guarda los cambios en SQLite. La aplicación Android consume estas mismas rutas mediante Retrofit.

En este backend, POST y DELETE de series devuelven HTTP 200. EventFlow devuelve HTTP 201 al crear un espacio y HTTP 204 al eliminarlo.
