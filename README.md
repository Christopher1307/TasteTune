🎧 TasteTune — Música para tu comida 🍽️🎶

**TasteTune** es una aplicación Android que combina inteligencia artificial y música para generar playlists personalizadas según el tipo de comida que el usuario escanea. Utiliza reconocimiento de imágenes para identificar alimentos y la API de Spotify para crear automáticamente una experiencia musical temática y emocional.

---

## 🧠 ¿Cómo funciona?

1. El usuario selecciona o toma una **foto de comida**.
2. La imagen se analiza con **ML Kit (Google)** para identificar la comida.
3. Se genera automáticamente una **playlist temática** en Spotify basada en la comida detectada.
4. Toda la información (foto, tipo de comida, playlist) se guarda en **Firebase Firestore**.
5. El usuario puede consultar su historial y ver detalles completos de playlists anteriores.

---

## 🛠️ Tecnologías utilizadas

| Componente | Tecnología |
| --- | --- |
| 🧠 IA / análisis de imágenes | [ML Kit - Image Labeling](https://developers.google.com/ml-kit/vision/image-labeling) |
| 🎵 Música y playlists | [Spotify Web API](https://developer.spotify.com/documentation/web-api/) |
| ☁️ Base de datos en la nube | [Firebase Firestore](https://firebase.google.com/products/firestore) |
| 🔐 Autenticación Spotify | OAuth 2.0 (Authorization Code Flow) |
| 💾 Persistencia local | SharedPreferences |
| 📦 Arquitectura | MVVM (ViewModel + LiveData) |
| 📷 Selección de imagen | Galería (Google Fotos compatible) y Cámara |
| 🌐 HTTP client | OkHttp |
| 🔧 JSON parsing | org.json |
| 🖼️ Carga de imágenes | Glide |

---

## 📱 Capturas de pantalla


---

## 🚀 Instalación y ejecución

### Requisitos

- Android Studio **Hedgehog** o superior
- SDK mínimo: `API 24` (Android 7.0)
- Conexión a Internet
- Cuenta de Spotify

### Clona el repositorio

```bash
git clone https://github.com/tuusuario/tastetune.git
cd tastetune
```

### Configura tus claves

1. Crea un proyecto en [Spotify Developer Dashboard](https://developer.spotify.com/dashboard).
2. Configura tu `CLIENT_ID`, `CLIENT_SECRET` y `REDIRECT_URI` en `SpotifyAuth.kt`.

```kotlin
private const val CLIENT_ID = "TU_CLIENT_ID"
private const val CLIENT_SECRET = "TU_SECRET"
private const val REDIRECT_URI = "tastetune://callback"
```

3. Añade tu archivo `google-services.json` de Firebase en `/app`.

### Ejecuta el proyecto

1. Abre Android Studio
2. Selecciona el dispositivo o emulador
3. Pulsa ▶️ para compilar y lanzar la app

---

## 📂 Estructura del proyecto

```
.
├── app
│   ├── Api_Music/
│   │   └── SpotifyAuth.kt, SpotifyApi.kt, SpotifyTracks.kt
│   ├── Api_imagen/
│   │   └── ImageSelectorFragment.kt, PlaylistDetailFragment.kt
│   ├── data/
│   │   └── Analysis.kt, FirestoreHelper.kt
│   ├── ui/
│   │   └── AnalysisHistoryFragment.kt, adapters, xmls
│   ├── MainActivity.kt
│   ├── SharedViewModel.kt
│   └── ...
```

---

## 📌 Funcionalidades principales

- 🔍 Escaneo de comida por foto (galería o cámara)
- 🎧 Playlist temática creada automáticamente en Spotify
- 🧾 Historial de análisis con imagen y fecha
- 📂 Visualización de detalles completos: nombre, descripción e imagen de playlist
- 🎵 Muestra en tiempo real las canciones reales dentro de la playlist
- 🔄 Persistencia entre sesiones: guarda la última imagen
- 🔐 Login con Spotify (OAuth2)
- 🔒 Gestión segura del token (SharedPreferences)

---

## 🧪 Tests y calidad

- ✔️ Validación de errores comunes (token nulo, fallos de red)
- ✔️ Manejo de URIs persistentes para compatibilidad con Google Fotos
- ✔️ Interfaz probada en dispositivos reales y emuladores (Pixel 4, API 33 y 34)

---

## 🛡️ Licencia

Este proyecto está licenciado bajo la **MIT License**. Consulta el archivo `LICENSE` para más información.

---

## 🙋‍♂️ Autor

**Christopher Araujo Peña**  
📧 contacto: [christopher56@gmail.com]  
🎓 IES CANARIAS — Desarrollo de Aplicaciones Multiplataforma (DAM)  
📅 Curso 2024/2025

---

> ¡Gracias por probar TasteTune! ¡Disfruta de tu música mientras comes! 🎶🍔🍣🌮
