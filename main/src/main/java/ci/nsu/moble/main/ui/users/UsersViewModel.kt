package ci.nsu.moble.main.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.moble.main.data.models.UserDto
import ci.nsu.moble.main.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val repository: AuthRepository
) : ViewModel()
{
    sealed class UsersState
    {
        object Loading : UsersState()
        data class Success(val users: List<UserDto>) : UsersState()
        data class Error(val message: String) : UsersState()
    }
    private val _state = MutableStateFlow<UsersState>(UsersState.Loading)
    val state: StateFlow<UsersState> = _state
   init
    {
        loadUsers()
    }
    fun loadUsers()
    {
        viewModelScope.launch {
            _state.value = UsersState.Loading
            val result = repository.getUsers()
            _state.value = if (result.isSuccess) {
                UsersState.Success(result.getOrNull() ?: emptyList())
            } else {
                UsersState.Error(result.exceptionOrNull()?.message ?: "Ошибка загрузки")
            }
        }
    }
}