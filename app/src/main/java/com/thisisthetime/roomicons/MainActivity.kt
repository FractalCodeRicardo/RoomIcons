package com.thisisthetime.roomicons


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Room
import com.thisisthetime.roomicons.ui.theme.RoomIconsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database: AppDatabase =
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "colorsdb"
            )
                .build()

        setContent {
            RoomIconsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SaveColors(
                        database, mutableListOf(
                            Icons.Filled.List,
                            Icons.Filled.Edit,
                            Icons.Filled.Add
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun IconPicker(
    selectedIcon: ImageVector? = null,
    iconsToShow: List<ImageVector>,
    onSelectedIcon: (ImageVector) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Button(
        onClick = {
            expanded = true
        }
    ) {
        Text(text = if (selectedIcon == null) "Click to select" else selectedIcon!!.name)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            iconsToShow.forEach {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(it, it.name)
                            Text(text = it.name)
                        }
                    },

                    onClick = {
                        onSelectedIcon(it)
                        expanded = false
                    }
                )
            }
        }
    }

}

@Composable
fun SaveColors(
    db: AppDatabase,
    iconsToShow: List<ImageVector>
) {
    var selectedIcon by remember { mutableStateOf<ImageVector?>(null) }
    var savedColors by remember { mutableStateOf(emptyList<ImageVector>()) }
    val scope = rememberCoroutineScope()

    Column {
        Row() {
            IconPicker(
                selectedIcon = selectedIcon,
                iconsToShow = iconsToShow,
                onSelectedIcon = {
                    selectedIcon = it
                })
            Button(onClick = {
                scope.launch {
                    selectedIcon?.let { saveIcon(db, it) }
                    savedColors = getIcons(db)
                }
            }) {
                Text("Save")
            }
        }
        savedColors.forEach {
            Row() {
                Icon(it, it.name)
                Text(it.name)
            }
        }
    }

}

fun getIconName(icon: ImageVector): String {
    return icon.name.split(".")[1]
}

fun iconByName(name: String): ImageVector {
    val cl = Class.forName("androidx.compose.material.icons.filled.${name}Kt")
    val method = cl.declaredMethods.first()
    return method.invoke(null, Icons.Filled) as ImageVector
}

suspend fun saveIcon(db: AppDatabase, icon: ImageVector) {
    val name = getIconName(icon)
    val dao = db.colorsDao()

    dao.insert(Color(name = name))
}

suspend fun getIcons(db: AppDatabase): List<ImageVector> {
    val records = db.colorsDao().getAll()

    return records.map {
        iconByName(it.name)
    }
}