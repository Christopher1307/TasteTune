import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tastetune.Api.SpotifyAuth
import com.example.tastetune.Api.createPlaylist
import com.example.tastetune.Api.getUserProfile

// `app/src/main/java/com/example/tastetune/Api/Main.kt`
@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    println("Inicia sesión en Spotify visitando esta URL:")
    println(SpotifyAuth.getAuthUrl())
    println("\nIntroduce el código que aparece en la URL después de autorizar la aplicación:")

    val code = readLine() ?: error("No se proporcionó ningún código.")

    SpotifyAuth.requestAccessToken(code) { accessToken ->
        if (accessToken == null) {
            println("Error: no se pudo obtener el token de acceso.")
            return@requestAccessToken
        }
        println("Token de acceso obtenido exitosamente: $accessToken")

        getUserProfile(accessToken) { userProfile ->
            println("Perfil de usuario: $userProfile")

            val userId = "31n5pznawv42ml54pih7425bd6me"
            createPlaylist(accessToken, userId, "Playlist de comida favorita") { playlistId ->
                println("Playlist creada con ID: $playlistId")
            }
        }
    }
}