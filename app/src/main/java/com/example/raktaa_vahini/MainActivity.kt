package com.example.raktaa_vahini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.raktaa_vahini.data.BloodGroup
import com.example.raktaa_vahini.data.FirebaseDonorRepository
import com.example.raktaa_vahini.ui.screens.DonorProfileScreen
import com.example.raktaa_vahini.ui.screens.EmergencySearchScreen
import com.example.raktaa_vahini.ui.screens.LoginScreen
import com.example.raktaa_vahini.ui.screens.RegistrationScreen
import com.example.raktaa_vahini.ui.theme.RaktaaVahiniTheme
import com.example.raktaa_vahini.ui.viewmodel.DonorViewModel
import com.example.raktaa_vahini.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = FirebaseDonorRepository()
        val factory = ViewModelFactory(repository)

        setContent {
            RaktaaVahiniTheme {
                AppNavigation(factory)
            }
        }
    }
}

sealed class Screen(val route: String, val label: String = "", val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Search : Screen("search", "Emergency", Icons.Default.Search)
    object Profile : Screen("profile", "My Profile", Icons.Default.Person)
}

@Composable
fun AppNavigation(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: DonorViewModel = viewModel(factory = factory)
    val currentDonor by viewModel.currentDonor.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (currentDonor == null) Screen.Login.route else "main_content"
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("main_content") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegistrationScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate("main_content") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable("main_content") {
            MainContent(viewModel, onLogout = {
                navController.navigate(Screen.Login.route) {
                    popUpTo("main_content") { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun MainContent(viewModel: DonorViewModel, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Search,
        Screen.Profile,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController, 
            startDestination = Screen.Search.route, 
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Search.route) { 
                LaunchedEffect(Unit) {
                    viewModel.searchDonors(BloodGroup.O_POSITIVE)
                }
                EmergencySearchScreen(viewModel) 
            }
            composable(Screen.Profile.route) { 
                DonorProfileScreen(viewModel, onLogout = onLogout) 
            }
        }
    }
}
