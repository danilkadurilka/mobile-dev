package ci.nsu.moble.main.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.moble.main.data.models.UserDto
import ci.nsu.moble.main.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val repository: AuthRepository
) : ViewModel()
{
    sealed class UsersState
    {
        object FirstLaunch : UsersState()
        object Loading : UsersState()
        data class Success(val users: List<UserDto>) : UsersState()
        data class Error(val message: String) : UsersState()
    }
    private val _state = MutableStateFlow<UsersState>(UsersState.FirstLaunch)
    val state: StateFlow<UsersState> = _state
    private var allUsers = emptyList<UserDto>()
    fun loadUsers() {
        if (_state.value is UsersState.Loading || _state.value is UsersState.Success) return
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = UsersState.Loading
            try {
                val result = repository.getUsers()
                _state.value = if (result.isSuccess) {
                    val users = result.getOrNull() ?: emptyList()
                    UsersState.Success(users)
                } else {
                    UsersState.Error(result.exceptionOrNull()?.message ?: "Ошибка загрузки")
                }
            } catch (e: Exception) {
                _state.value = UsersState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }
    fun logout() {
        repository.logout()
    }
}