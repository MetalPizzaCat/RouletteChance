package com.metalpizzacat.roulettechance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.metalpizzacat.roulettechance.ui.theme.RouletteChanceTheme
import kotlinx.coroutines.launch
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

enum class ShellState {
    UNKNOWN,
    LIVE,
    BLANK
}

fun getNextShellState(current: ShellState) = when (current) {
    ShellState.LIVE -> ShellState.BLANK
    ShellState.BLANK -> ShellState.UNKNOWN
    ShellState.UNKNOWN -> ShellState.LIVE
}


@Composable
fun ChanceManager() {
    var liveShellCount by remember { mutableIntStateOf(0) }
    var blankShellCount by remember { mutableIntStateOf(0) }
    var isInRound by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        liveShellCount = 0
                        blankShellCount = 0
                        isInRound = false
                    },
                ) {
                    Text("Reset")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    if (liveShellCount > 0 || blankShellCount > 0) {
                        isInRound = true
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(message = "Needs more than one shell")
                        }
                    }
                }, modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text("Start")
                }
            }
        }) {
        Column(modifier = Modifier.padding(it)) {

            if (isInRound) {
                RoundManager(lives = liveShellCount, blanks = blankShellCount, roundEnded = {
                    // idk
                })
            } else {
                Column {
                    Row {
                        Button(onClick = {
                            liveShellCount++
                        }) {
                            Text(stringResource(R.string.add_live))
                        }
                        Button(onClick = {
                            blankShellCount++
                        }) {
                            Text(stringResource(R.string.add_blank))
                        }
                    }
                    Row {
                        Button(onClick = {
                            liveShellCount = max(0, liveShellCount - 1)
                        }) {
                            Text(stringResource(R.string.remove_live))
                        }
                        Button(onClick = {
                            blankShellCount = max(0, blankShellCount - 1)
                        }) {
                            Text(stringResource(R.string.remove_blank))
                        }
                    }
                }

                ShellCounter(
                    blanks = blankShellCount,
                    live = liveShellCount,
                )
            }


        }
    }

}

/**
 * Displays the remaining shells
 * @param blanks Current amount of blanks
 * @param live Current amount of live shells
 */
@Composable
fun ShellCounter(blanks: Int, live: Int) {

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
    }
}

@Preview(showBackground = true)
@Composable
fun ShellStatsPreview() {
    RouletteChanceTheme {
        ShellCounter(blanks = 1, live = 1)
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    RouletteChanceTheme {
        ChanceManager()
    }
}

@Preview(showBackground = true)
@Composable
fun ShellViewerPreview() {
    RouletteChanceTheme {
        ShellViewer(
            shells = mutableListOf(
                ShellState.UNKNOWN,
                ShellState.UNKNOWN,
                ShellState.BLANK,
                ShellState.LIVE
            ),
            current = 2
        ) { _, _ -> run {} }
    }
}