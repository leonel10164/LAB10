from django.shortcuts import get_object_or_404
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Serie
from .serializer import SerieSerializer


class IndexView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        context = {'mensaje': 'servidor activo'}
        return Response(context)


class SeriesView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        data_series = Serie.objects.all()
        serialized_series = SerieSerializer(data_series, many=True)
        return Response(serialized_series.data)

    def post(self, request):
        serialized_serie = SerieSerializer(data=request.data)
        serialized_serie.is_valid(raise_exception=True)
        serialized_serie.save()
        return Response(serialized_serie.data)


class SerieDetailView(APIView):
    permission_classes = [AllowAny]

    def get(self, request, serie_id):
        data_serie = get_object_or_404(Serie, pk=serie_id)
        serialized_serie = SerieSerializer(data_serie)
        return Response(serialized_serie.data)

    def put(self, request, serie_id):
        data_serie = get_object_or_404(Serie, pk=serie_id)
        serialized_serie = SerieSerializer(data_serie, data=request.data)
        serialized_serie.is_valid(raise_exception=True)
        serialized_serie.save()
        return Response(serialized_serie.data)

    def delete(self, request, serie_id):
        data_serie = get_object_or_404(Serie, pk=serie_id)
        serialized_serie = SerieSerializer(data_serie)
        data_serie.delete()
        return Response(serialized_serie.data)
