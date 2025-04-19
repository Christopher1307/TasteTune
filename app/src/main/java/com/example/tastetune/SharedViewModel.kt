import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri

class SharedViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _playlistId = MutableLiveData<String?>()
    val playlistId: LiveData<String?> get() = _playlistId

    fun setImageUri(uri: Uri) {
        _imageUri.postValue(uri)
    }

    fun setPlaylistId(id: String) {
        _playlistId.postValue(id)
    }
    fun postPlaylistId(id: String) {
        _playlistId.postValue(id)
    }

    fun clear() {
        _imageUri.value = null
        _playlistId.value = null
    }


}