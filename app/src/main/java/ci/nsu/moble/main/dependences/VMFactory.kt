package ci.nsu.moble.main.dependences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ci.nsu.moble.main.ui.auth.LoginViewModel
import ci.nsu.moble.main.ui.register.RegisterViewModel
import ci.nsu.moble.main.ui.deposit.DepositVM
import ci.nsu.moble.main.ui.users.UsersViewModel

class ViewModelFactory(private val locator: DependencesInjection) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        return when
        {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(locator.authRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(locator.authRepository) as T
            }
            modelClass.isAssignableFrom(UsersViewModel::class.java) -> {
                UsersViewModel(locator.authRepository) as T
            }
            modelClass.isAssignableFrom(DepositVM::class.java) -> {
                val userId = locator.tokenManager.getUserId() ?: -1L
                DepositVM(userId, locator.depositRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
