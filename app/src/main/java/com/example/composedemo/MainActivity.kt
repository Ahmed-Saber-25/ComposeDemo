package com.example.composedemo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composedemo.navigation.BottomScreen
import com.example.composedemo.navigation.bottomNavigationItems
import com.example.composedemo.screens.*
import com.example.composedemo.ui.theme.ComposeDemoTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDemoTheme{
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    BottomCollapse()
                }
            }
        }
    }
    @ExperimentalMaterialApi
    @Composable
    private fun BottomCollapse() {
        val title = remember { mutableStateOf(BottomScreen.Home.title) }
        val bottomBarHeight = 55.dp
        val bottomBarHeightPx = with(LocalDensity.current) {
            bottomBarHeight.roundToPx().toFloat()
        }
        val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    val newOffset = bottomBarOffsetHeightPx.value + delta
                    bottomBarOffsetHeightPx.value =
                        newOffset.coerceIn(-bottomBarHeightPx, 0f)
                    return Offset.Zero
                }
            }
        }
        val scaffoldState = rememberScaffoldState()
        val navController = rememberNavController()
        val navShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        Scaffold(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(title = { Text(text = title.value) },
                    actions = {
                        IconButton(onClick = {
                            lifecycleScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("you clicked me",null,SnackbarDuration.Short)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                        }
                    })
            },

            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .height(bottomBarHeight)
                        .clip(navShape)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = -bottomBarOffsetHeightPx.value.roundToInt()
                            )
                        }
                ) {
                    BottomNavigation(navController)
                }
            }
        ) { innerPadding ->
            ScreenController(innerPadding, navController, title)
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun BottomNavigation(navController: NavHostController) {
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            bottomNavigationItems.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Column(horizontalAlignment = CenterHorizontally) {
                            if (screen.badgeCount > 0) {
                                BadgeBox(
                                    badgeContent = {
                                        Text(text = screen.badgeCount.toString())
                                    }
                                ) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            }
                            if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                                Text(
                                    text = screen.title,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    //                                label = { Text(text = screen.title) },
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // re selecting the same item
                            launchSingleTop = true
                            // Restore state when re selecting a previously selected item
                            restoreState = true
                        }
                    })
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ScreenController(innerPadding: PaddingValues,navController: NavHostController, topBarTitle: MutableState<String>) {
    NavHost(navController = navController, startDestination = BottomScreen.Home.route) {
        composable(BottomScreen.Home.route) {
            HomeScreen(innerPadding,R.drawable.cat)
            topBarTitle.value = BottomScreen.Home.title
        }

        composable(BottomScreen.Search.route) {
            SearchScreen()
            topBarTitle.value = BottomScreen.Search.title
        }

        composable(BottomScreen.Favourite.route) {
            FavouriteScreen()
            topBarTitle.value = BottomScreen.Favourite.title
        }

        composable(BottomScreen.Setting.route) {
            SettingScreen()
            topBarTitle.value = BottomScreen.Setting.route
        }
        composable(BottomScreen.User.route) {
            UserScreen()
            topBarTitle.value = BottomScreen.User.route
        }
    }
}

