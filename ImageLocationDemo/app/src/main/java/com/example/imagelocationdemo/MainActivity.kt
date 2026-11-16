package com.example.imagelocationdemo

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.imagelocationdemo.R
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.imagelocationdemo.ui.theme.ImageLocationDemoTheme
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.location.Location
import androidx.compose.foundation.layout.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageLocationDemoTheme {
                ScreenWithCard()
            }
        }
    }
}
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun LocationPermissionExample(onPermissionResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(hasPermission) { onPermissionResult(hasPermission) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        Toast.makeText(
            context,
            context.getString(
                if (granted) R.string.location_granted else R.string.location_denied
            ),
            Toast.LENGTH_LONG
        ).show()
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                if (hasPermission) R.string.location_granted else R.string.location_denied
            )
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }) {
            Text("Enable location")
        }
    }
}

@Composable
fun UserLocationMapScreen(
    permissionGranted: Boolean
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val current = LatLng(it.latitude, it.longitude)
                    userLocation = current
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(current, 15f)
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        Text(text = "Your current location")

        Spacer(modifier = Modifier.height(12.dp))

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
            cameraPositionState = cameraPositionState
        ) {
            userLocation?.let { position ->
                Marker(
                    state = MarkerState(position = position),
                    title = "You are here"
                )
            }
        }
    }
}

@Composable
fun ScreenWithCard() {
    var hasPermission by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.geolocation),
            contentDescription = "Map preview"
        )

        Text(text = stringResource(R.string.app_name))

        PermissionCard()

        // LocationPermissionExample()
        if (hasPermission) {
            UserLocationMapScreen(permissionGranted = true)
        } else {
            LocationPermissionExample(
                onPermissionResult = { granted -> hasPermission = granted }
            )
        }
    }
}
@Composable
fun PermissionCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(R.string.welcome_title))
            Text(text = stringResource(R.string.welcome_description))
        }
    }
}
@Composable
fun ResourceExample() {
    Image(
        painter = painterResource(id = R.drawable.geolocation),
        contentDescription = "Geolocation map",
    )
}
