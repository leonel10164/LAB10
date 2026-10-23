from decimal import Decimal, InvalidOperation

from django.db.models import ProtectedError
from rest_framework import filters, status, viewsets
from rest_framework.exceptions import ValidationError
from rest_framework.permissions import IsAuthenticatedOrReadOnly
from rest_framework.response import Response

from .models import Espacio, RecursoTecnico
from .serializers import EspacioSerializer, RecursoTecnicoSerializer


class EspacioViewSet(viewsets.ModelViewSet):
    queryset = Espacio.objects.all()
    serializer_class = EspacioSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]
    filter_backends = [filters.SearchFilter]
    search_fields = ["nombre", "ubicacion"]

    def get_queryset(self):
        queryset = super().get_queryset()
        estado = self.request.query_params.get("estado")
        if estado:
            queryset = queryset.filter(estado=estado)
        return queryset

    def destroy(self, request, *args, **kwargs):
        try:
            return super().destroy(request, *args, **kwargs)
        except ProtectedError:
            return Response(
                {
                    "detail": (
                        "No se puede eliminar: el espacio tiene recursos "
                        "asignados."
                    )
                },
                status=status.HTTP_400_BAD_REQUEST,
            )


class RecursoTecnicoViewSet(viewsets.ModelViewSet):
    queryset = RecursoTecnico.objects.select_related("espacio")
    serializer_class = RecursoTecnicoSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]
    filter_backends = [filters.SearchFilter]
    search_fields = ["nombre", "codigo_inventario"]

    def get_queryset(self):
        queryset = super().get_queryset()
        params = self.request.query_params
        espacio = params.get("espacio")
        if espacio:
            try:
                espacio_id = int(espacio)
            except ValueError as exc:
                raise ValidationError(
                    {"espacio": "Debe ser un identificador numérico."}
                ) from exc
            if espacio_id < 1:
                raise ValidationError(
                    {"espacio": "Debe ser un identificador positivo."}
                )
            queryset = queryset.filter(espacio_id=espacio_id)

        costo_min = self._parse_cost(params.get("costo_min"), "costo_min")
        costo_max = self._parse_cost(params.get("costo_max"), "costo_max")
        if costo_min is not None and costo_max is not None:
            if costo_min > costo_max:
                raise ValidationError(
                    {"costo_min": "No puede superar a costo_max."}
                )
        if costo_min is not None:
            queryset = queryset.filter(costo__gte=costo_min)
        if costo_max is not None:
            queryset = queryset.filter(costo__lte=costo_max)
        return queryset

    @staticmethod
    def _parse_cost(value, field_name):
        if value is None or value == "":
            return None
        try:
            cost = Decimal(value)
        except InvalidOperation as exc:
            raise ValidationError(
                {field_name: "Debe ser un número decimal válido."}
            ) from exc
        if not cost.is_finite():
            raise ValidationError(
                {field_name: "Debe ser un número decimal finito."}
            )
        return cost
