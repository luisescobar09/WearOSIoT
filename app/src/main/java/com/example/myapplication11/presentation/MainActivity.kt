/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.myapplication11.presentation

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import coil.ComponentRegistry
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.myapplication11.R
import com.example.myapplication11.presentation.theme.MyApplication11Theme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
        setContent {
            WearApp()
        }
    }
}

object NavRoute {
    const val SCREEN_1 = "screen1"
    const val SCREEN_2 = "screenLED"
    const val SCREEN_3 = "screenTemperature"
}

@Composable
fun WearApp() {
    MyApplication11Theme {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(navController = navController,   startDestination = NavRoute.SCREEN_1) {
            composable(NavRoute.SCREEN_1) {
                MainScreen(navController = navController)
            }
            composable(NavRoute.SCREEN_2) {
                LEDScreen()
            }
            composable(NavRoute.SCREEN_3) {
                TemperatureScreen()
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context= context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Scaffold() {
        val listState = rememberScalingLazyListState()
        Scaffold(timeText = {
            if (!listState.isScrollInProgress) {
                TimeText()
            }
        }, vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        }, positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                state = listState
            ) {
                item {
                    Text("Bienvenido", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Button(onClick = {
                        // Navigate to the second screen
                        navController.navigate(NavRoute.SCREEN_2)
                    },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.width(150.dp),
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter= painterResource(id = R.drawable.lighticon),
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Box(
                                modifier = Modifier.weight(1F),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Control LED", color = Color.Black)
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Button(onClick = {
                        // Navigate to the second screen
                        navController.navigate(NavRoute.SCREEN_3)
                    },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.width(150.dp),
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter= painterResource(id = R.drawable.temperatura),
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Box(
                                modifier = Modifier.weight(1F),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Temperatura", color = Color.Black)
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun LEDScreen() {
    val context = LocalContext.current
    var switch by remember {
        mutableStateOf(false)
    }
    Scaffold() {
        val listState = rememberScalingLazyListState()
        Scaffold(timeText = {
            if (!listState.isScrollInProgress) {
                TimeText()
            }
        }, vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        }, positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                state = listState
            ) {
                item {
                    Text("Control de LED", textAlign = TextAlign.Center)
                }
                //icons8.com
                item { Spacer(modifier = Modifier.height(15.dp)) }
                item {
                    Image(painter = painterResource(id = if (switch) R.drawable.encendido else R.drawable.apagado)  , contentDescription = "Image",
                    modifier = Modifier.clickable {
                        switch = !switch
                        val database = Firebase.database
                        val ref = database.reference.child("esp").child("led_state")
                        if (switch) {
                            ref.setValue(1).addOnCompleteListener {
                                Toast.makeText(context, "LED encendido", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                switch = !switch
                                Toast.makeText(
                                    context,
                                    "Error al encender el LED",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            ref.setValue(0).addOnCompleteListener {
                                Toast.makeText(context, "LED apagado", Toast.LENGTH_SHORT).show()
                            }
                                .addOnFailureListener {
                                    switch = !switch
                                    Toast.makeText(
                                        context,
                                        "Error al apagar el LED",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    )
                }
            }

        }
    }
}

@Composable
fun TemperatureScreen() {
    var temperature by remember {
        mutableStateOf(0.0F)
    }
    var humidity by remember {
        mutableStateOf(0.0F)
    }
    val database = Firebase.database
    val refTemperature = database.reference.child("esp").child("temperature")
    val refHumidity = database.reference.child("esp").child("humidity")
    refTemperature.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            temperature = snapshot.getValue(Float::class.java)!!
        }
        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
        }
    })
    refHumidity.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            humidity = snapshot.getValue(Float::class.java)!!
        }
        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
        }
    })
    Scaffold() {
        val listState = rememberScalingLazyListState()
        Scaffold(timeText = {
            if (!listState.isScrollInProgress) {
                TimeText()
            }
        }, vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        }, positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                state = listState
            ) {
                item {
                    Image(painter = painterResource(id = if(temperature < 12) R.drawable.temperatura_baja else if (temperature >= 12 && temperature <25) R.drawable.temperatura_normal else R.drawable.temperatura_alta), contentDescription = "Temperature",
                    )
                }
                item {
                    Text("Temperatura: $temperature°C", textAlign = TextAlign.Center)
                }
                item {
                    Image(painter = painterResource(id = if(humidity < 20.00) R.drawable.humedad_baja else if (humidity >= 20.00 && humidity < 80.00) R.drawable.humedad_normal else R.drawable.humedad_alta), contentDescription = "Humedad")
                }
                item {
                    Text("Humedad: $humidity%", textAlign = TextAlign.Center)
                }
            }

        }
    }
}