package com.resonance.resonanceandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.resonance.resonanceandroid.ui.theme.ResonanceAndroidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResonanceAndroidTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PlaylistScreen()
                }
            }
        }
    }
}

data class Song(
    val songName: String,
    val artists: List<String>
)

data class Playlist(
    val playlistId: String,
    val playlistName: String,
    val songs: List<Song>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen() {
    var playlists by remember { mutableStateOf<List<Playlist>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val fetchPlaylists: suspend () -> Unit = {
        try {
            isLoading = true
            error = null
            val response = withContext(Dispatchers.IO) {
                URL("https://resonance-backend.onrender.com/playlists").readText()
            }
            playlists = parsePlaylists(response)
        } catch (e: Exception) {
            error = "Failed to load playlists: ${e.message}"
            playlists = emptyList()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchPlaylists()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resonance!") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        fetchPlaylists()
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                isLoading -> {
                    LoadingView()
                }
                error != null -> {
                    ErrorView(
                        error = error!!,
                        onRetry = {
                            scope.launch {
                                fetchPlaylists()
                            }
                        }
                    )
                }
                playlists.isEmpty() -> {
                    EmptyView()
                }
                else -> {
                    PlaylistsList(playlists = playlists)
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading playlists...")
    }
}

@Composable
private fun ErrorView(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(error, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No playlists found")
    }
}

@Composable
private fun PlaylistsList(playlists: List<Playlist>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(playlists) { playlist ->
            PlaylistItem(playlist = playlist)
        }
    }
}

@Composable
fun PlaylistItem(playlist: Playlist) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = playlist.playlistName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Songs:",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Column {
                playlist.songs.forEach { song ->
                    Text(
                        text = "${song.songName} - ${song.artists.joinToString(", ")}",
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun parsePlaylists(json: String): List<Playlist> {
    val jsonArray = JSONArray(json)
    val playlists = mutableListOf<Playlist>()

    for (i in 0 until jsonArray.length()) {
        val playlistObj = jsonArray.getJSONObject(i)
        val songsArray = playlistObj.getJSONArray("songs")
        val songs = mutableListOf<Song>()

        for (j in 0 until songsArray.length()) {
            val songObj = songsArray.getJSONObject(j)
            val artistsArray = songObj.getJSONArray("artists")
            val artists = mutableListOf<String>()

            for (k in 0 until artistsArray.length()) {
                artists.add(artistsArray.getString(k))
            }

            songs.add(Song(
                songName = songObj.getString("songName"),
                artists = artists
            ))
        }

        playlists.add(Playlist(
            playlistId = playlistObj.getString("playlist_id"),
            playlistName = playlistObj.getString("playlist_name"),
            songs = songs
        ))
    }

    return playlists
}

@Preview(showBackground = true)
@Composable
fun PlaylistScreenPreview() {
    ResonanceAndroidTheme {
        PlaylistScreen()
    }
}