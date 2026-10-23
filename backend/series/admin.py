from django.contrib import admin

from .models import Serie


@admin.register(Serie)
class SerieAdmin(admin.ModelAdmin):
    list_display = ("name", "release_date", "rating", "category")
    list_filter = ("category",)
    search_fields = ("name",)
