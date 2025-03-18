package com.example.project_for_apsfactory_engineering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_for_apsfactory_engineering.ui.theme.Project_for_apsfactory_engineeringTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable




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

// 📌 FUNCȚIE PENTRU DETALIILE UNUI OBIECT
@Composable

fun SearchScreen(navController: NavController) {
    var keyword by remember { mutableStateOf("") }
    var objectIDs by remember { mutableStateOf<List<Int>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔎 Câmp de căutare
        TextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("Enter keyword") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔍 Buton pentru declanșarea căutării
        Button(onClick = {
            coroutineScope.launch {
                RetrofitInstance.apiService.searchObjects(keyword).enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                        if (response.isSuccessful) {
                            objectIDs = response.body()?.objectIDs ?: emptyList()
                        } else {
                            objectIDs = emptyList()
                        }
                    }

                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                        objectIDs = emptyList()
                    }
                })
            }
        }) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📋 Afișarea listei de rezultate
        LazyColumn {
            items(objectIDs) { id ->
                Text(
                    text = "Object ID: $id",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("objectDetails/$id")
                        }
                )
            }
        }
    }
}

@Composable
fun ObjectDetailsScreen(objectId: Int, navController: NavController) {
    var title by remember { mutableStateOf("Loading...") }
    var artist by remember { mutableStateOf("Loading...") }

    LaunchedEffect(objectId) {
        RetrofitInstance.apiService.getObjectDetails(objectId).enqueue(object : Callback<ObjectResponse> {
            override fun onResponse(call: Call<ObjectResponse>, response: Response<ObjectResponse>) {
                if (response.isSuccessful) {
                    val objectDetails = response.body()
                    title = objectDetails?.title ?: "No title available"
                    artist = objectDetails?.artistDisplayName ?: "Unknown artist"
                } else {
                    title = "Error"
                    artist = "Error"
                }
            }

            override fun onFailure(call: Call<ObjectResponse>, t: Throwable) {
                title = "Failed to load"
                artist = "Failed to load"
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Title: $title", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Artist: $artist", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Buton pentru a naviga înapoi la pagina de căutare
        Button(
            onClick = { navController.navigate("searchScreen") }
        ) {
            Text("Back to Search")
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "searchScreen") {
        composable("searchScreen") { SearchScreen(navController) }
        composable("objectDetails/{objectId}") { backStackEntry ->
            val objectId = backStackEntry.arguments?.getString("objectId")?.toIntOrNull()
            objectId?.let {
                // Trece navController ca parametru în ObjectDetailsScreen
                ObjectDetailsScreen(it, navController)
            }
        }
    }
}