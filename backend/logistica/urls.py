from rest_framework.routers import DefaultRouter

from .views import EspacioViewSet, RecursoTecnicoViewSet

router = DefaultRouter()
router.register("espacios", EspacioViewSet)
router.register("recursos", RecursoTecnicoViewSet)

urlpatterns = router.urls
