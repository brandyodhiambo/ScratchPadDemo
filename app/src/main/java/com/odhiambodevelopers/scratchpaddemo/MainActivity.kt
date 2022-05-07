package com.odhiambodevelopers.scratchpaddemo

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.odhiambodevelopers.scratchpaddemo.ui.theme.ScratchPadDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScratchPadDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ScratchPad()
                }
            }
        }
    }
}


@Composable
fun ScratchPad() {
    val path = remember {mutableStateOf(mutableListOf<PathState>())}
    Scaffold(
        topBar = {
            ComposePaintAppBar{
                path.value = mutableListOf()
            }
        }
    ) {
        PaintBody(path)
    }
}

@Composable
fun ComposePaintAppBar(
    onDelete:()-> Unit
) {
    TopAppBar(
        title = {
            Text(text = "ScratchPad")
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription ="Delete"
                )
            }
        }
    ) 
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PaintBody(path:MutableState<MutableList<PathState>>) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val drawColor = remember{ mutableStateOf(Color.Black)}
        val drawBrush = remember{ mutableStateOf(5f)}
        val usedColor = remember { mutableStateOf(mutableSetOf(Color.Black,Color.White,Color.Gray))}

        path.value.add(PathState(Path(),drawColor.value,drawBrush.value))

        DrawingCanvas(
                drawColor,
                drawBrush,
                usedColor,
                path.value
        )

        DrawingTools(
            drawColor = drawColor,
            drawBrush = drawBrush,
            usedColors = usedColor.value
        )

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingCanvas(
    drawColor: MutableState<Color>,
    drawBrush: MutableState<Float>,
    usedColor: MutableState<MutableSet<Color>>,
    path: MutableList<PathState>
) {
    val currentPath = path.last().path
    val movePath = remember{ mutableStateOf<Offset?>(null)}

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
            .pointerInteropFilter {
                    when(it.action){
                        MotionEvent.ACTION_DOWN ->{
                            currentPath.moveTo(it.x,it.y)
                            usedColor.value.add(drawColor.value)
                        }
                        MotionEvent.ACTION_MOVE ->{
                            movePath.value = Offset(it.x,it.y)
                        }
                        else ->{
                            movePath.value =null
                        }
                    }
                true
            }
    ){
        movePath.value?.let {
            currentPath.lineTo(it.x,it.y)
            drawPath(
                path = currentPath,
                color = drawColor.value,
                style = Stroke(drawBrush.value)
            )
        }
        path.forEach {
            drawPath(
                path = it.path,
                color = it.color,
                style  = Stroke(it.stroke)
            )
        }
    }
}
