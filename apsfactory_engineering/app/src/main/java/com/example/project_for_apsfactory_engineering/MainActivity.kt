package com.example.project_for_apsfactory_engineering

import FavoriteViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.project_for_apsfactory_engineering.ui.theme.Project_for_apsfactory_engineeringTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project_for_apsfactory_engineeringTheme {
                AppNavigation()
            }
        }
    }
}
@Composable
fun MenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { navController.navigate("searchScreen") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Search")
        }

        Button(
            onClick = { navController.navigate("favoriteScreen") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Favorite")
        }
    }
}
@Composable
fun FavoriteScreen(navController: NavController, viewModel: FavoriteViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("menuScreen") }) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Favorite Articles", fontSize = 24.sp)

        LazyColumn {
            items(viewModel.favorites) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                        navController.navigate("objectDetails/${item.id}")
                    },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.title, fontSize = 16.sp, modifier = Modifier.weight(2f))
                    Button(onClick = { viewModel.removeFavorite(item) }) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}



@Composable
fun SearchScreen(navController: NavController) {
    var keyword by remember { mutableStateOf("") }
    var objectList by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("menuScreen") }, // Navighează la MenuScreen
            modifier = Modifier.offset(x = -170.dp, y = 1.dp)
                .size(50.dp,50.dp)

        ) {
            Text("<", fontSize = 24.sp) // Textul din buton
        }
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("Enter keyword") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))



        Button(onClick = {
            coroutineScope.launch {
                RetrofitInstance.apiService.searchObjects(keyword).enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                        if (response.isSuccessful) {
                            val ids = response.body()?.objectIDs?.take(20) ?: emptyList()
                            val newList = mutableListOf<Pair<Int, String>>()

                            for (id in ids) {
                                RetrofitInstance.apiService.getObjectDetails(id).enqueue(object : Callback<ObjectResponse> {
                                    override fun onResponse(call: Call<ObjectResponse>, response: Response<ObjectResponse>) {
                                        if (response.isSuccessful) {
                                            val title = response.body()?.title ?: "Unknown Title"
                                            newList.add(id to title)
                                            objectList = newList.toList()
                                        }
                                    }

                                    override fun onFailure(call: Call<ObjectResponse>, t: Throwable) {
                                        newList.add(id to "Failed to load")
                                        objectList = newList.toList()
                                    }
                                })
                            }
                        } else {
                            objectList = emptyList()
                        }
                    }

                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                        objectList = emptyList()
                    }
                })
            }
        }) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(objectList) { (id, title) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("objectDetails/$id") },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "ID: $id", fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Text(text = title, fontSize = 16.sp, modifier = Modifier.weight(2f))
                }
            }
        }
    }
}

@Composable

fun ObjectDetailsScreen(objectId: Int, navController: NavController, viewModel: FavoriteViewModel) {
    var title by remember { mutableStateOf("Loading...") }
    var artist by remember { mutableStateOf("Loading...") }
    var department by remember { mutableStateOf("Loading...") }
    var primaryImage by remember { mutableStateOf<String?>(null) }
    var additionalImages by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(objectId) {
        RetrofitInstance.apiService.getObjectDetails(objectId).enqueue(object : Callback<ObjectResponse> {
            override fun onResponse(call: Call<ObjectResponse>, response: Response<ObjectResponse>) {
                if (response.isSuccessful) {
                    val objectDetails = response.body()
                    title = objectDetails?.title ?: "No title available"
                    artist = objectDetails?.artistDisplayName ?: "Unknown artist"
                    department = objectDetails?.department ?: "Unknown department"
                    primaryImage = objectDetails?.primaryImage
                    additionalImages = objectDetails?.additionalImages ?: emptyList()
                }
            }

            override fun onFailure(call: Call<ObjectResponse>, t: Throwable) {
                title = "Failed to load"
                artist = "Failed to load"
                department = "Failed to load"
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imagine principală
        primaryImage?.let {
            AsyncImage(
                model = it,
                contentDescription = "Primary Image",
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Informații despre obiect
        Text("Title: $title", fontSize = 24.sp)
        Text("Artist: $artist", fontSize = 20.sp)
        Text("Department: $department", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Imagini adiționale
        if (additionalImages.isNotEmpty()) {
            Text("Additional Images:", fontSize = 22.sp)
            Spacer(modifier = Modifier.height(8.dp))

            additionalImages.forEach { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Additional Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Salvare în favorite
        Button(onClick = {
            val favorite = FavoriteItem(objectId, title, artist, department,)
            viewModel.addFavorite(favorite)
        }) {
            Text("Add to Favorites")
        }
    }
}



@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val favoriteViewModel: FavoriteViewModel = viewModel()


    NavHost(navController, startDestination = "menuScreen") {
        composable("menuScreen") { MenuScreen(navController) }
        composable("searchScreen") { SearchScreen(navController) }
        composable("favoriteScreen") { FavoriteScreen(navController, favoriteViewModel) }
        composable("objectDetails/{objectId}") { backStackEntry ->
            val objectId = backStackEntry.arguments?.getString("objectId")?.toIntOrNull()
            objectId?.let { ObjectDetailsScreen(it, navController, favoriteViewModel) }
        }
    }
}
