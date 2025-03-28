package com.example.pokemon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.pokemon.R

@Composable
fun PokemonImage(
    modifier: Modifier = Modifier,
    image: String,
    contentDescription: String? = null
) {
    var isLoading by remember { mutableStateOf(false) }
    Box(contentAlignment = Alignment.Center) {
        AsyncImage(
            modifier = modifier,
            model = image,
            contentDescription = contentDescription,
            error = painterResource(R.drawable.ic_image_not_supported_24),
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },
            onError = { isLoading = false }
        )
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}
