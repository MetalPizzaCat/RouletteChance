package com.metalpizzacat.roulettechance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.metalpizzacat.roulettechance.ui.theme.RouletteChanceTheme
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RouletteChanceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChanceManager()
                }
            }
        }
    }
}


@Composable
fun ChanceManager() {
    var liveShellCount by remember { mutableIntStateOf(0) }
    var blankShellCount by remember { mutableIntStateOf(0) }
    var current by remember { mutableIntStateOf(0) }

    val known = remember { mutableStateListOf<Int>() }

    Column {
        Button(onClick = {
            liveShellCount = 0
            blankShellCount = 0
            current = 0
            known.clear()
        }) {
            Text("Reset")
        }

        Row {
            Button(onClick = {
                liveShellCount++
            }) {
                Text("Add live")
            }
            Button(onClick = {
                blankShellCount++
            }) {
                Text("Add blank")
            }
        }

        if (blankShellCount <= 0 && liveShellCount <= 0) {
            Text("No ammo in the clip")
        } else {
            ChanceViewer(
                blanks = blankShellCount,
                live = liveShellCount,
            )
            Row {
                Button(onClick = { liveShellCount = max(liveShellCount - 1, 0) }) {
                    Text("Shoot live")
                }
                Button(onClick = { blankShellCount = max(blankShellCount - 1, 0) }) {
                    Text("Shoot blank")
                }
            }
        }
    }
}

/**
 * @param blanks Current amount of blanks
 * @param live Current amount of live shells
 */
@Composable
fun ChanceViewer(blanks: Int, live: Int) {

    Column {
        Row {
            for (i in 0..<live) {
                Icon(
                    painter = painterResource(R.drawable.shell),
                    contentDescription = "Live shell",
                    tint = Color.Unspecified,
                    modifier = Modifier.padding(2.dp)
                )
            }
            for (i in 0..<blanks) {
                Icon(
                    painter = painterResource(R.drawable.shell_blank),
                    contentDescription = "Blank shell",
                    tint = Color.Unspecified,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
        Text("Chances: ")
        Text("Next shell is live: ${((live.toFloat() / ((live + blanks).toFloat())) * 100f)}%")
        Text("Next shell is live: ${((blanks.toFloat() / ((live + blanks).toFloat())) * 100f)}%")
    }
}

@Preview(showBackground = true)
@Composable
fun InterfacePreview() {
    RouletteChanceTheme {
        ChanceViewer(blanks = 1, live = 1)
    }
}