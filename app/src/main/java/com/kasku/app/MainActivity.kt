package com.kasku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kasku.app.repository.KaskuRepository
import com.kasku.app.theme.KaskuTheme
import com.kasku.app.theme.TextDark
import com.kasku.app.theme.TextGray
import com.kasku.app.ui.screens.AddTransactionDialog
import com.kasku.app.ui.screens.HomeScreen
import com.kasku.app.ui.screens.LoginScreen
import com.kasku.app.ui.screens.ProfileScreen
import com.kasku.app.ui.screens.RegisterScreen
import com.kasku.app.ui.screens.SettingsScreen
import com.kasku.app.ui.viewmodel.KaskuViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: KaskuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repository = KaskuRepository(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return KaskuViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[KaskuViewModel::class.java]

        setContent {
            val settings by viewModel.settings.collectAsState()
            KaskuTheme(darkTheme = settings.isDarkMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    KaskuApp(viewModel = viewModel)
                }
            }
        }
    }
}

enum class Screen(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    SETTINGS("Pengaturan", Icons.Filled.Settings, Icons.Outlined.Settings),
    HOME("Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    PROFILE("Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

enum class AuthScreen { LOGIN, REGISTER }

@Composable
fun KaskuApp(viewModel: KaskuViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var authScreen by remember { mutableStateOf(AuthScreen.LOGIN) }
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Auth flow
    if (!isLoggedIn) {
        Crossfade(
            targetState = authScreen,
            animationSpec = tween(250),
            label = "authTransition"
        ) { screen ->
            when (screen) {
                AuthScreen.LOGIN -> LoginScreen(
                    onLogin = { email, password -> viewModel.login(email, password) },
                    onNavigateToRegister = { authScreen = AuthScreen.REGISTER }
                )
                AuthScreen.REGISTER -> RegisterScreen(
                    onRegister = { name, email, password -> viewModel.register(name, email, password) },
                    onNavigateToLogin = { authScreen = AuthScreen.LOGIN }
                )
            }
        }
        return
    }

    // Main app (logged in)
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = if (settings.isDarkMode) Color(0xFF1E1E1E) else Color.White,
                tonalElevation = 4.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Screen.entries.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.label
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (settings.isDarkMode) Color.White else TextDark,
                            selectedTextColor = if (settings.isDarkMode) Color.White else TextDark,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Smooth crossfade transition between tabs
            Crossfade(
                targetState = currentScreen,
                animationSpec = tween(200),
                label = "screenTransition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onAddTransactionClick = { showAddDialog = true }
                    )
                    Screen.SETTINGS -> SettingsScreen(
                        userProfile = userProfile,
                        settings = settings,
                        onBack = { currentScreen = Screen.HOME },
                        onUpdateProfile = { name, email -> viewModel.updateProfile(name, email) },
                        onUpdateSettings = { viewModel.updateSettings(it) },
                        onResetData = { viewModel.resetData() }
                    )
                    Screen.PROFILE -> ProfileScreen(
                        userProfile = userProfile,
                        onLogout = { viewModel.logout() }
                    )
                }
            }

            if (showAddDialog) {
                AddTransactionDialog(
                    onDismiss = { showAddDialog = false },
                    onAddTransaction = { title, amount, type, category, memberName ->
                        viewModel.addTransaction(title, amount, type, category, memberName)
                    }
                )
            }
        }
    }
}
