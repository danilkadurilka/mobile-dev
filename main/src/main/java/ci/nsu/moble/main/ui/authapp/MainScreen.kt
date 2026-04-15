package ci.nsu.moble.main.ui.authapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ci.nsu.moble.main.ui.authapp.data.models.UserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    viewModel: MainViewModel
) {
    val usersState by viewModel.usersState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пользователи") },
                actions = {
                    Button(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Text("Выйти")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (usersState) {
                is MainViewModel.UsersState.Loading -> {
                    CircularProgressIndicator()
                }
                is MainViewModel.UsersState.Success -> {
                    val users = (usersState as MainViewModel.UsersState.Success).users
                    if (users.isEmpty()) {
                        Text("Нет пользователей")
                    } else {
                        LazyColumn {
                            items(users) { user ->
                                UserCard(user = user)
                            }
                        }
                    }
                }
                is MainViewModel.UsersState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (usersState as MainViewModel.UsersState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUsers() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: UserDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = user.login,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Телефон: ${user.phoneNumber}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "ID пользователя: ${user.userId}",
                style = MaterialTheme.typography.bodySmall
            )
            if (user.personId != null) {
                Text(
                    text = "ID персоны: ${user.personId}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}