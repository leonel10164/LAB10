from decimal import Decimal

from django.contrib.auth import get_user_model
from rest_framework import status
from rest_framework.test import APITestCase

from .models import Espacio, RecursoTecnico


class EventFlowAPITests(APITestCase):
    def setUp(self):
        self.user = get_user_model().objects.create_user(
            username="eventflow-user",
            password="correct-horse-battery",
        )

    def test_reads_are_public_and_writes_require_authentication(self):
        response = self.client.get("/api/espacios/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        response = self.client.get(
            "/api/espacios/",
            HTTP_ORIGIN="http://127.0.0.1:5500",
        )
        self.assertEqual(
            response["Access-Control-Allow-Origin"],
            "http://127.0.0.1:5500",
        )

        response = self.client.post(
            "/api/espacios/",
            {"nombre": "Auditorio", "ubicacion": "Bloque A", "aforo_maximo": 50},
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_login_returns_token(self):
        response = self.client.post(
            "/api/login/",
            {
                "username": "eventflow-user",
                "password": "correct-horse-battery",
            },
            format="json",
        )

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertTrue(response.data["token"])

    def test_space_validation_and_resource_serialization(self):
        self.client.force_authenticate(user=self.user)
        invalid_space = self.client.post(
            "/api/espacios/",
            {"nombre": "  A ", "ubicacion": "Bloque A", "aforo_maximo": 0},
            format="json",
        )
        self.assertEqual(invalid_space.status_code, status.HTTP_400_BAD_REQUEST)

        space_response = self.client.post(
            "/api/espacios/",
            {
                "nombre": "  Auditorio Central ",
                "ubicacion": "Bloque A",
                "aforo_maximo": 300,
            },
            format="json",
        )
        self.assertEqual(space_response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(space_response.data["nombre"], "Auditorio Central")

        resource_response = self.client.post(
            "/api/recursos/",
            {
                "nombre": "Proyector",
                "codigo_inventario": " eq-0001 ",
                "costo": "125.50",
                "stock": 2,
                "espacio": space_response.data["id"],
            },
            format="json",
        )
        self.assertEqual(resource_response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(resource_response.data["codigo_inventario"], "EQ-0001")
        self.assertEqual(
            resource_response.data["espacio_detalle"]["nombre"],
            "Auditorio Central",
        )

        duplicate_response = self.client.post(
            "/api/recursos/",
            {
                "nombre": "Proyector duplicado",
                "codigo_inventario": "eq-0001",
                "costo": "125.50",
                "stock": 1,
                "espacio": space_response.data["id"],
            },
            format="json",
        )
        self.assertEqual(
            duplicate_response.status_code,
            status.HTTP_400_BAD_REQUEST,
        )

    def test_resource_validation_rejects_maintenance_space_and_bad_filters(self):
        self.client.force_authenticate(user=self.user)
        space = Espacio.objects.create(
            nombre="Sala de mantenimiento",
            ubicacion="Bloque B",
            aforo_maximo=25,
            estado=Espacio.Estado.MANTENIMIENTO,
        )
        response = self.client.post(
            "/api/recursos/",
            {
                "nombre": "Proyector",
                "codigo_inventario": "EQ-0002",
                "costo": "25.00",
                "stock": 1,
                "espacio": space.pk,
            },
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        response = self.client.get("/api/recursos/?costo_min=no-es-numero")
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        response = self.client.get("/api/recursos/?espacio=no-es-id")
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_protected_delete_and_missing_resource(self):
        self.client.force_authenticate(user=self.user)
        space = Espacio.objects.create(
            nombre="Auditorio",
            ubicacion="Bloque A",
            aforo_maximo=100,
        )
        resource = RecursoTecnico.objects.create(
            nombre="Proyector",
            codigo_inventario="EQ-0003",
            costo=Decimal("25.00"),
            stock=1,
            espacio=space,
        )

        response = self.client.delete(f"/api/espacios/{space.pk}/")
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        response = self.client.delete(f"/api/recursos/{resource.pk}/")
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)

        response = self.client.get(f"/api/recursos/{resource.pk}/")
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        response = self.client.delete(f"/api/espacios/{space.pk}/")
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
