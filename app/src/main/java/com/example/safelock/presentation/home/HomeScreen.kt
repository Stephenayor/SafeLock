package com.example.safelock.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.primarySurface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safelock.R
import com.example.safelock.presentation.dashboard.DashBoardScreen
import com.example.safelock.presentation.location.LocationComposable
import com.example.safelock.presentation.securemedia.SecureMediaActivity
import com.example.safelock.ui.theme.ThemeViewModel
import com.example.safelock.utils.Route
import com.example.safelock.utils.base.BaseViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               navController: NavController = rememberNavController(),
               themeViewModel: ThemeViewModel?
) {
    var selectedTab by remember { mutableStateOf<Screen>(Screen.DashBoard) }

    Box(
        modifier = Modifier.fillMaxSize().
        background(MaterialTheme.colors.background)
    ) {
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colors.background),
            topBar = { MainAppBar() },
            bottomBar = {
                BottomNavigationBar(selectedTab, themeViewModel) { screen ->
                    selectedTab = screen
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(MaterialTheme.colors.surface)
            ) {
                when (selectedTab) {
                    Screen.DashBoard -> DashBoardScreen(modifier, navController, themeViewModel)
                    Screen.SecuredMedia -> SecureMediaActivity.start(LocalContext.current)
                    Screen.Location -> LocationComposable(navController)
                }
            }

        }
    }
}

@Composable
fun ProfileScreen() {
    Text("Profile Screen")
}


@Composable
fun BottomNavigationBar(
    selectedTab: Screen,
    themeViewModel: ThemeViewModel?,
    onTabSelected: (Screen) -> Unit,
) {
    val darkThemeEnabled by themeViewModel?.theme!!.collectAsState()
    val bottomNavBackground = if (darkThemeEnabled) Color.Black else MaterialTheme.colors.background
    BottomNavigation(
        backgroundColor = bottomNavBackground,
        contentColor = Color.Gray
    ) {
        val items = listOf(Screen.DashBoard, Screen.SecuredMedia, Screen.Location)
        items.forEach { screen ->
            if (Screen.SecuredMedia == screen){
                BottomNavigationItem(
                    icon = { Icon(screen.icon, contentDescription = screen.label) },
                    label = { Text(screen.label, color = Color.Black) },
                    selected = selectedTab == screen,
                    onClick = { onTabSelected(screen) },
                    modifier = Modifier.background(
                        MaterialTheme.colors.background
                    )
                )
            }else {
                BottomNavigationItem(
                    icon = { Icon(screen.icon, contentDescription = screen.label) },
                    label = { Text(screen.label, color = Color.Black) },
                    selected = selectedTab == screen,
                    selectedContentColor = Color.Blue,
                    onClick = { onTabSelected(screen) },
                    modifier = Modifier
                        .offset(y = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}



@Preview
@Composable
fun MainAppBar() {
    TopAppBar(
        elevation = 6.dp,
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.height(58.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            text = stringResource(R.string.appname),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



@Preview
@Composable
fun HomeScreenPreview() {
    val modifier = Modifier
    val navController = rememberNavController()

    HomeScreen(modifier = modifier, navController, null)
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object DashBoard : Screen(Route.DASHBOARD, "DashBoard", Icons.Filled.Home)
    data object SecuredMedia : Screen(Route.SECURED_MEDIA, "SecuredMedia", Icons.Filled.PermMedia)
    data object Location : Screen(Route.LOCATION, "Location", Icons.Filled.LocationOn)
}