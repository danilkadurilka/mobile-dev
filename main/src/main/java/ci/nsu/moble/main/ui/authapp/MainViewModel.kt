package ci.nsu.moble.main.ui.authapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.moble.main.ui.authapp.data.models.UserDto
import ci.nsu.moble.main.ui.authapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    sealed class UsersState {
        object Loading : UsersState()
        data class Success(val users: List<UserDto>) : UsersState()
        data class Error(val message: String) : UsersState()
    }
    private val _usersState = MutableStateFlow<UsersState>(UsersState.Loading)
    val usersState: StateFlow<UsersState> = _usersState
    init {
    //    loadUsers()
    }
    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UsersState.Loading
            val result = repository.getUsers()
            _usersState.value = if (result.isSuccess) {
                UsersState.Success(result.getOrNull() ?: emptyList())
            } else {
                UsersState.Error(result.exceptionOrNull()?.message ?: "Ошибка загрузки пользователей")
            }
        }
    }
    fun logout() {
        repository.logout()
    }
}