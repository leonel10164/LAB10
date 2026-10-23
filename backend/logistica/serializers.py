import re
from decimal import Decimal

from rest_framework import serializers

from .models import Espacio, RecursoTecnico


class EspacioSerializer(serializers.ModelSerializer):
    total_recursos = serializers.IntegerField(
        source="recursos.count",
        read_only=True,
    )

    class Meta:
        model = Espacio
        fields = [
            "id",
            "nombre",
            "ubicacion",
            "aforo_maximo",
            "estado",
            "total_recursos",
        ]

    def validate_aforo_maximo(self, value):
        if value <= 0:
            raise serializers.ValidationError(
                "El aforo debe ser mayor que cero."
            )
        return value

    def validate_nombre(self, value):
        value = value.strip()
        if len(value) < 3:
            raise serializers.ValidationError(
                "El nombre debe tener al menos 3 caracteres."
            )
        return value


class EspacioResumenSerializer(serializers.ModelSerializer):
    class Meta:
        model = Espacio
        fields = ["id", "nombre", "ubicacion", "estado"]


class RecursoTecnicoSerializer(serializers.ModelSerializer):
    espacio_detalle = EspacioResumenSerializer(source="espacio", read_only=True)

    class Meta:
        model = RecursoTecnico
        fields = [
            "id",
            "nombre",
            "codigo_inventario",
            "costo",
            "stock",
            "espacio",
            "espacio_detalle",
        ]

    def validate_codigo_inventario(self, value):
        value = value.strip().upper()
        if not re.fullmatch(r"[A-Z0-9-]{4,30}", value):
            raise serializers.ValidationError(
                "Use 4-30 caracteres: letras, números y guiones "
                "(ej. EQ-0001)."
            )
        matches = RecursoTecnico.objects.filter(codigo_inventario=value)
        if self.instance is not None:
            matches = matches.exclude(pk=self.instance.pk)
        if matches.exists():
            raise serializers.ValidationError(
                "Ya existe un recurso con este código de inventario."
            )
        return value

    def validate_costo(self, value):
        if value <= Decimal("0"):
            raise serializers.ValidationError(
                "El costo debe ser mayor que cero."
            )
        return value

    def validate_stock(self, value):
        if value < 0:
            raise serializers.ValidationError(
                "El stock no puede ser negativo."
            )
        return value

    def validate_espacio(self, value):
        if value.estado == Espacio.Estado.MANTENIMIENTO:
            raise serializers.ValidationError(
                "No se pueden asignar equipos a un espacio en mantenimiento."
            )
        return value
