package com.aneonex.bitcoinchecker.tester.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    modifier: Modifier = Modifier,
    selectedIndex: Int = -1,
    itemList: List<String>,
    onValueChange: (Int) -> Unit,
    label: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
//    var selectedIndex by remember { mutableStateOf(initialIndex) }
//    var selectedIndex = initialIndex

    ExposedDropdownMenuBox(
        expanded = expanded,
        modifier = modifier,
        onExpandedChange = {
            expanded = !expanded
        }) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
//                .widthIn(min = 10.dp)
//                .defaultMinSize(minWidth = 1.dp),
//            modifier = modifier,
            singleLine = true,
            maxLines = 1,
            readOnly = true,
            value = if(selectedIndex >= 0) itemList[selectedIndex] else "",
//            color = MaterialTheme.colors.primary,
//            placeholder = {Text("Select market")},
            onValueChange = { },
            label = { if(!label.isNullOrEmpty()) Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
//            colors = ExposedDropdownMenuDefaults.textFieldColors(
//                textColor = MaterialTheme.colors.onSurface
//            )
        )
        ExposedDropdownMenu(
//            modifier = modifier,
//                .fillMaxWidth(),
//                .exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            val listState = rememberLazyListState()
            // Remember a CoroutineScope to be able to launch
            val coroutineScope = rememberCoroutineScope()

            val lazyHeight = (itemList.size).coerceAtMost(10) *  48

//            itemList.forEachIndexed { index, itemText ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .simpleVerticalScrollbar(state = listState)
                    .size(400.dp, lazyHeight.dp) // Required to fix intrinsic issue
            ) {
                itemsIndexed(itemList) { index, itemText ->
//                    val itemText = itemList[index]
                    DropdownMenuItem(
                        modifier = Modifier
//                            .size(400.dp, 200.dp)
//                            .fillMaxWidth()
                            .let {
                                if (selectedIndex == index)
                                    it.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                else it
                            },
                        onClick = {
                            if (selectedIndex != index) {
                                //selectedIndex = index
                                onValueChange(index)
                            }
                            expanded = false
                        },
                        text = {
                            Text(text = itemText)
                        }
                    )
                }
            }

            LaunchedEffect(coroutineScope){
                val scrollIndexShift = 2
                if(selectedIndex > scrollIndexShift) {
                    listState.scrollToItem(selectedIndex - scrollIndexShift)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ComboBoxPreview(sampleCount: Int = 3) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        Row {
            (1..sampleCount).forEach {
                ComboBox(
                    modifier = Modifier
//                        .widthIn(20.dp, 200.dp)
                        .weight(1f)
                        ,

                    itemList = listOf("Item1", "Item2"),
                    label = "Test_$it",
                    onValueChange = {}
                )
            }
        }
        Row {
            ComboBox(
                modifier =
                    Modifier
                        .weight(1f)
//                        .fillMaxWidth()
                        .widthIn(max = 100.dp)
                ,
                itemList = listOf("Item1", "Item2"),
                label = "Test",
                onValueChange = {}
            )
        }

        Row {
            (1..sampleCount).forEach {
//                Column(Modifier.weight(1f)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
//                        .width(50.dp)
//                            .widthIn(10.dp, 200.dp)
//                            .defaultMinSize(minWidth = 1.dp)
                        ,
                        value = "Label $it",
                        singleLine = true,
                        onValueChange = {},
                    )
                }
  //          }
        }

    }
}