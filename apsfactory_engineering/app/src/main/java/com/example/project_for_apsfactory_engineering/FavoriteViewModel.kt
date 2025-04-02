import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_for_apsfactory_engineering.AppDatabase
import com.example.project_for_apsfactory_engineering.FavoriteItem
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val favoriteDao = db.favoriteDao()

    var favorites = mutableStateListOf<FavoriteItem>()
        private set

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favorites.clear()
            favorites.addAll(favoriteDao.getAllFavorites())
        }
    }

    fun addFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            favoriteDao.insertFavorite(item)
            loadFavorites()
        }
    }

    fun removeFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(item)
            loadFavorites()
        }
    }
}
