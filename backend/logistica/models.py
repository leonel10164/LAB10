from decimal import Decimal

from django.core.validators import MinValueValidator
from django.db import models


class Espacio(models.Model):
    class Estado(models.TextChoices):
        DISPONIBLE = "DISPONIBLE", "Disponible"
        OCUPADO = "OCUPADO", "Ocupado"
        MANTENIMIENTO = "MANTENIMIENTO", "En mantenimiento"

    nombre = models.CharField(max_length=120)
    ubicacion = models.CharField(max_length=120, help_text="Bloque o ubicación")
    aforo_maximo = models.PositiveIntegerField(validators=[MinValueValidator(1)])
    estado = models.CharField(
        max_length=15,
        choices=Estado.choices,
        default=Estado.DISPONIBLE,
    )

    class Meta:
        ordering = ["nombre"]

    def __str__(self):
        return f"{self.nombre} ({self.ubicacion})"


class RecursoTecnico(models.Model):
    nombre = models.CharField(max_length=120)
    codigo_inventario = models.CharField(max_length=30, unique=True)
    costo = models.DecimalField(
        max_digits=10,
        decimal_places=2,
        validators=[MinValueValidator(Decimal("0.01"))],
    )
    stock = models.PositiveIntegerField(default=1)
    espacio = models.ForeignKey(
        Espacio,
        on_delete=models.PROTECT,
        related_name="recursos",
    )

    class Meta:
        ordering = ["nombre"]

    def __str__(self):
        return f"{self.nombre} [{self.codigo_inventario}]"
