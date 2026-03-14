package com.zionhuang.music.ui.hitori.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.ArtistItem
import com.zionhuang.innertube.models.PlaylistItem
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.YTItem
import com.zionhuang.music.ui.hitori.JakartaFont
import com.zionhuang.music.ui.hitori.hitoriColors

@Composable
fun HitoriSearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(hitoriColors.InputBg)
            .border(1.dp, hitoriColors.Border, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = hitoriColors.Text3, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(color = hitoriColors.Text, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = JakartaFont),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox = { inner ->
                if (query.isEmpty()) Text("Artists, songs, podcasts…", color = hitoriColors.Text3, fontSize = 14.sp, fontFamily = JakartaFont)
                inner()
            },
            modifier = Modifier.weight(1f)
        )
        if (query.isNotEmpty()) {
            Icon(Icons.Default.Close, null, tint = hitoriColors.Text3, modifier = Modifier.size(16.dp).clickable { onQueryChange("") })
        }
    }
}

@Composable
fun SearchResultRow(item: YTItem, onClick: () -> Unit) {
    val subtitle = when (item) {
        is SongItem -> item.artists.joinToString { it.name }
        is AlbumItem -> item.artists?.joinToString { it.name }
        is PlaylistItem -> item.author?.name
        else -> null
    }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MusicThumbnail(
            url = item.thumbnail,
            modifier = Modifier.size(46.dp),
            shape = if (item is ArtistItem) CircleShape else RoundedCornerShape(10.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = hitoriColors.Text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = hitoriColors.Text2,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(Icons.Default.MoreVert, null, tint = hitoriColors.Text3, modifier = Modifier.size(18.dp))
    }
}
