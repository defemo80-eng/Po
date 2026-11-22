# DeFitt — Aplicación Fitness Offline para Android

**TU CUERPO. TU META. TU PROGRESO.**

Aplicación Android nativa (Kotlin + Jetpack Compose + Room SQLite) diseñada para funcionar **100% sin conexión a internet**:

- **Contador real de pasos** mediante sensores de hardware (`TYPE_STEP_COUNTER`, `TYPE_STEP_DETECTOR` o acelerómetro compatible) y servicio en segundo plano.
- **Comida**: registro por desayuno, almuerzo, cena y merienda con fotografías locales guardadas en almacenamiento privado.
- **Ejercicios**: catálogo integrado en 8 categorías, calentamiento/recuperación, personalización por perfil y temporizador en vivo.
- **Logros y DeFitt Coins**: medallas verdes/doradas y puntos virtuales saludables.
- **Registros, Calendario y Respaldo**: historial diario, reinicio automático cada día y exportación/restauración `.json`.

## Instalar el APK desde GitHub (`defemo80-eng/Po`)

1. Sube este proyecto a la rama `main` de tu repositorio en GitHub (desde el menú **Push to GitHub** en AI Studio o con `git push`).
2. GitHub ejecutará automáticamente el flujo **Compilar APK DeFitt** en la pestaña **Acciones**.
3. Al finalizar:
   - Abre la sección **Releases** (`Lanzamientos`) del repositorio y toca **`DeFitt.apk`** para descargarlo e instalarlo directamente en tu teléfono Android, **o**
   - Entra a **Acciones** -> última ejecución -> descarga el artefacto **`DeFitt-APK`**.
4. *(Opcional para firma Release)*: Agrega en GitHub (`Settings -> Secrets and variables -> Actions`) los secretos `RELEASE_KEYSTORE_BASE64`, `STORE_PASSWORD` y `KEY_PASSWORD`. Si no están configurados, el flujo publica automáticamente el APK instalable firmado con `debug.keystore.base64`.
