package ci.nsu.moble.main.ui.authapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ci.nsu.moble.main.ui.authapp.data.models.GroupDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("MALE") }
    var selectedGroupId by remember { mutableStateOf(0) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState()
    val groupsState by viewModel.groupsState.collectAsState()

    LaunchedEffect(registerState) {
        if (registerState is RegisterViewModel.RegisterState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    val groups: List<GroupDto> = when (groupsState) {
        is RegisterViewModel.GroupsState.Success -> (groupsState as RegisterViewModel.GroupsState.Success).groups
        else -> emptyList<GroupDto>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Фамилия *") },
            modifier = Modifier.fillMaxWidth(),
            isError = registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя *") },
            modifier = Modifier.fillMaxWidth(),
            isError = registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Отчество") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Дата рождения (ГГГГ-ММ-ДД)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("1990-01-01") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Пол", style = MaterialTheme.typography.labelLarge)
        Row {
            Row(modifier = Modifier.weight(1f)) {
                RadioButton(
                    selected = gender == "MALE",
                    onClick = { gender = "MALE" }
                )
                Text("Мужской", modifier = Modifier.padding(start = 8.dp))
            }
            Row(modifier = Modifier.weight(1f)) {
                RadioButton(
                    selected = gender == "FEMALE",
                    onClick = { gender = "FEMALE" }
                )
                Text("Женский", modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (groupsState) {
            is RegisterViewModel.GroupsState.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            is RegisterViewModel.GroupsState.Success -> {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = groups.find { group -> group.id == selectedGroupId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Группа *") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        groups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group.name) },
                                onClick = {
                                    selectedGroupId = group.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            is RegisterViewModel.GroupsState.Error -> {
                Text(
                    text = (groupsState as RegisterViewModel.GroupsState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин *") },
            modifier = Modifier.fillMaxWidth(),
            isError = registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль *") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Телефон *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.register(
                    firstName, lastName, middleName, birthDate,
                    gender, selectedGroupId, login, password, email, phoneNumber
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is RegisterViewModel.RegisterState.Loading
        ) {
            if (registerState is RegisterViewModel.RegisterState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Зарегистрироваться")
            }
        }

        if (registerState is RegisterViewModel.RegisterState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (registerState as RegisterViewModel.RegisterState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}