package com.example.locationappdemo

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationappdemo.ui.theme.LocationAppDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel : LocationViewModel = viewModel()
            LocationAppDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                }
                    TheApp(viewModel)
                }
            }
        }
    }

@Composable
fun TheApp(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUnits = LocationUnit(context)
    LocationDisplay(locationUnit = locationUnits, viewModel, context = context)
}

@Composable
fun LocationDisplay(
    locationUnit: LocationUnit,
    viewModel : LocationViewModel,
    context: Context) {

    val location = viewModel.location.value

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // we have access to the location
                locationUnit.requestLocationUpdates(viewModel = viewModel)
            }else{
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired) {
                    Toast.makeText(context,
                        "Location permission is required for this feature to work", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,
                        "Location permission is required. Please enable it in the Android setting", Toast.LENGTH_SHORT).show()
                }
            }
        })

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        if (location != null){
            Text("Address: ${location.latitude} ${location.longitude}")
        }else{
            Text("The Location is not available")
        }
        
        Button(onClick = {
            if (locationUnit.hasLocationPermission(context)) {
                // the permission is already granted
                locationUnit.requestLocationUpdates(viewModel)
            }else{
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        })
        {
            Text("Get Location")
        }
    }

    
}





















