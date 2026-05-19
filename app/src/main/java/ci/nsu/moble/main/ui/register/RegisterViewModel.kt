package ci.nsu.moble.main.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.moble.main.data.models.GroupDto
import ci.nsu.moble.main.data.models.PersonDto
import ci.nsu.moble.main.data.models.RegisterRequest
import ci.nsu.moble.main.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    sealed class RegisterState
    {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
    sealed class GroupsState
    {
        object Loading : GroupsState()
        data class Success(val groups: List<GroupDto>) : GroupsState()
        data class Error(val message: String) : GroupsState()
    }
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState
    private val _groupsState = MutableStateFlow<GroupsState>(GroupsState.Loading)
    val groupsState: StateFlow<GroupsState> = _groupsState
    init
    {
        loadGroups()
    }
    private fun loadGroups()
    {
        viewModelScope.launch {
            _groupsState.value = GroupsState.Loading
            val result = repository.getGroups()
            _groupsState.value = if (result.isSuccess)
            {
                GroupsState.Success(result.getOrNull() ?: emptyList())
            }
            else
            {
                GroupsState.Error(result.exceptionOrNull()?.message ?: "Ошибка загрузки групп")
            }
        }
    }
    fun register(
        firstName: String,
        lastName: String,
        middleName: String,
        birthDate: String,
        gender: String,
        groupId: Int,
        login: String,
        password: String,
        email: String,
        phoneNumber: String
    )
    {
        if (firstName.isBlank() || lastName.isBlank() || login.isBlank() ||
            password.isBlank())
        {
            _registerState.value = RegisterState.Error("Заполните все обязательные поля")
            return
        }
        if (password.length < 6)
        {
            _registerState.value = RegisterState.Error("Пароль должен содержать минимум 6 символов")
            return
        }
        if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            _registerState.value = RegisterState.Error("Введите корректный email")
            return
        }
        val person = PersonDto(
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = birthDate,
            gender = gender,
            groupId = groupId
        )
        val request = RegisterRequest(
            login = login,
            password = password,
            email = email,
            phoneNumber = phoneNumber,
            roleId = 1,
            authAllowed = true,
            person = person
        )
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val result = repository.register(request)
            _registerState.value = if (result.isSuccess) {
                RegisterState.Success
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Ошибка регистрации")
            }
        }
    }
    fun resetState()
    {
        _registerState.value = RegisterState.Idle
    }
}