package com.metalpizzacat.roulettechance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

fun calculateLiveChance(
    remainingLive: Int,
    remainingBlanks: Int,
    current: Int,
    state: MutableList<ShellState>
): Float =
    when (state[current]) {
        ShellState.LIVE -> 1f
        ShellState.BLANK -> 0f
        ShellState.UNKNOWN -> {
            val possibleLive: Int =
                remainingLive - state.filterIndexed { id, shell -> (id > current && shell == ShellState.LIVE) }
                    .count()
            val possibleBlank: Int =
                remainingBlanks - state.filterIndexed { id, shell -> (id > current && shell == ShellState.BLANK) }
                    .count()


            possibleLive.toFloat() / (possibleLive + possibleBlank).toFloat()
        }
    }

fun calculateBlankChance(
    remainingLive: Int,
    remainingBlanks: Int,
    current: Int,
    state: MutableList<ShellState>
): Float =
    when (state[current]) {
        ShellState.LIVE -> 0f
        ShellState.BLANK -> 1f
        ShellState.UNKNOWN -> {
            val possibleLive: Int =
                remainingLive - state.filterIndexed { id, shell -> (id > current && shell == ShellState.LIVE) }
                    .count()
            val possibleBlank: Int =
                remainingBlanks - state.filterIndexed { id, shell -> (id > current && shell == ShellState.BLANK) }
                    .count()


            possibleBlank.toFloat() / (possibleLive + possibleBlank).toFloat()
        }
    }

@Composable
fun RoundManager(lives: Int, blanks: Int, roundEnded: () -> Unit) {

    val total = lives + blanks
    val shellStates =
        remember { MutableList(lives + blanks) { ShellState.UNKNOWN }.toMutableStateList() }

    val shotShells = remember { mutableStateListOf<ShellState>() }
    var current by remember { mutableIntStateOf(0) }

    var livesLeft by remember { mutableIntStateOf(lives) }
    var blanksLeft by remember { mutableIntStateOf(blanks) }

    fun setShellState(id: Int, state: ShellState) {
        shellStates.removeAt(id)
        shellStates.add(id, state)
    }
    Column {
        Text("Current ammo state: ")
        ShellViewer(shells = shellStates, current = current, shellUpdated = { id, state ->
            setShellState(id, state)
        })
        Row {
            Button(onClick = {
                if (livesLeft > 0) {
                    livesLeft = max(livesLeft - 1, 0)

                    setShellState(current, ShellState.LIVE)
                    current = min(current + 1, total - 1)
                    shotShells.add(ShellState.LIVE)
                }
            }) {
                Text("Shoot live")
            }
            Button(onClick = {
                if (blanksLeft > 0) {
                    blanksLeft = max(blanksLeft - 1, 0)
                    setShellState(current, ShellState.BLANK)
                    current = min(current + 1, total - 1)
                    shotShells.add(ShellState.BLANK)
                }
            }) {
                Text("Shoot blank")
            }
            Button(onClick = {
                if (!shotShells.isEmpty()) {
                    when (shotShells.last()) {
                        ShellState.BLANK -> blanksLeft++
                        ShellState.LIVE -> livesLeft++
                        else -> {}
                    }
                    shotShells.removeLast()

                    current = max(0, current - 1)
                    setShellState(current, ShellState.UNKNOWN)
                }

            }) {
                Text("Undo")
            }
        }
        Row {
            Text("Chance of live: ")
            Text("${calculateLiveChance(livesLeft, blanksLeft, current, shellStates) * 100f}%")
        }
        Row {
            Text("Chance of blank: ")
            Text("${calculateBlankChance(livesLeft, blanksLeft, current, shellStates) * 100f}%")
        }

        ShellCounter(
            blanks = blanksLeft,
            live = livesLeft,
        )
    }

}

@Composable
fun ShellButton(state: ShellState, clicked: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(
            id = when (state) {
                ShellState.BLANK -> R.drawable.shell_blank
                ShellState.UNKNOWN -> R.drawable.shell_unknown
                ShellState.LIVE -> R.drawable.shell
            }
        ),
        contentDescription = "Shell icon",
        tint = Color.Unspecified,
        modifier = modifier.clickable { clicked() },
    )
}

@Composable
fun ShellViewer(
    shells: MutableList<ShellState>,
    current: Int,
    shellUpdated: (id: Int, currentState: ShellState) -> Unit
) {
    Row {
        shells.mapIndexed { id, shell ->
            if (id == current) {
                ShellButton(
                    state = shell,
                    modifier = Modifier
                        .background(colorResource(id = R.color.purple_200))
                        .padding(6.dp),
                    clicked = { shellUpdated(id, getNextShellState(shell)) }
                )
            } else {
                ShellButton(
                    state = shell,
                    clicked = { shellUpdated(id, getNextShellState(shell)) },
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}