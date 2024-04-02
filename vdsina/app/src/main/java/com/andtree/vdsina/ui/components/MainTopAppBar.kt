package com.andtree.vdsina.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.andtree.vdsina.R
import com.andtree.vdsina.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    onExitClick: () -> Unit,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Text(
                text = title,
                maxLines = 1
            )},
        actions = {
            IconButton(onClick = onExitClick) {
                Icon(
                    painter = painterResource(id = R.drawable.exit_icon),
                    contentDescription = "Exit icon"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}