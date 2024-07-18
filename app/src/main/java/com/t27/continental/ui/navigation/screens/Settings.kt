package com.t27.continental.ui.navigation.screens

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val localContext = LocalContext.current
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SettingsItem(
                icon = Icons.Outlined.Info,
                primaryName = "App Version",
                navigateTo = {},
                description = getAppVersion(localContext)
            )
        }
    }
}

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    primaryName: String,
    description: String? = null,
    navigateTo: () -> Unit
) {
    ListItem(
        leadingContent = {
            if (icon != null) Icon(
                icon,
                contentDescription = icon.name,
                modifier = Modifier
                    .width(36.dp)
                    .height(36.dp)
            )
        },
        headlineContent = {
            Text(
                text = primaryName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )
        },
        supportingContent = {
            Text(
                text = description ?: "",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 12.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { navigateTo() },
        tonalElevation = 2.dp,
    )
}

fun getAppVersion(
    context: Context,
): String {
    return try {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName
    } catch (e: Exception) {
        "unable to get version name"
    }
}
