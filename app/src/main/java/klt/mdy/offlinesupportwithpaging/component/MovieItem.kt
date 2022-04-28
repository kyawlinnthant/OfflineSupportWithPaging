package klt.mdy.offlinesupportwithpaging.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import klt.mdy.offlinesupportwithpaging.R
import klt.mdy.offlinesupportwithpaging.common.Endpoints
import klt.mdy.offlinesupportwithpaging.model.MovieEntity


@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MovieEntity
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colors.primary.copy(alpha = 0.1f))
            .padding(all = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            text = movie.movieTitle,
            style = MaterialTheme.typography.h4
        )

        movie.coverUrl?.let {

            AsyncImage(
                model = Endpoints.IMAGE_URL + it,
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                modifier = modifier.aspectRatio(ratio = 2.0f),
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
            )
        }
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            text = movie.overview ?: movie.originalTitle,
            style = MaterialTheme.typography.overline
        )
        Spacer(modifier = modifier.height(8.dp))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "popularity : ${movie.popularity}",
                style = MaterialTheme.typography.overline
            )
            Text(
                text = "vote count : ${movie.totalVote}",
                style = MaterialTheme.typography.overline
            )
            Text(
                text = "vote average : ${movie.averageVote}",
                style = MaterialTheme.typography.overline
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    Surface {
        MovieItem(
            movie = MovieEntity(
                id = 1,
                coverUrl = "",
                posterUrl = "",
                overview = "This is the overview of the selected movie item. " +
                        "How's about the content preview. This gonna be GG. " +
                        "This overview will contain the description of a movie.",
                originalTitle = "Original Title",
                movieTitle = "Title",
                popularity = 1.2,
                releasedDate = "12.12.2022",
                totalVote = 1.2,
                averageVote = 1.2,
                language = "en"
            )
        )
    }
}