package klt.mdy.offlinesupportwithpaging.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import dagger.hilt.android.AndroidEntryPoint
import klt.mdy.offlinesupportwithpaging.component.*
import klt.mdy.offlinesupportwithpaging.model.MovieEntity
import klt.mdy.offlinesupportwithpaging.theme.OfflineSupportWithPagingTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OfflineSupportWithPagingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val vm: MainViewModel = hiltViewModel()
                    val movies = vm.movies.collectAsLazyPagingItems()
                    MoviesContent(movies = movies)

                }
            }
        }
    }
}

@Composable
fun MoviesContent(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<MovieEntity>,
) {
    Surface {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(all = 8.dp),
            content = {
                itemsIndexed(items = movies) { index, value ->
                    MovieItem(movie = value ?: MovieEntity())
                    if (index < movies.itemCount - 1) {

                        Spacer(modifier = modifier.height(8.dp))

                    }
                }
                movies.apply {

                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                ShimmerView()
                            }
                        }
                        loadState.append is LoadState.Loading -> {
                            item {
                                LoadingView()
                            }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val e = movies.loadState.refresh as LoadState.Error
                            item {
                                RetryView(
                                    message = e.error.localizedMessage ?: "Error",
                                    onClickRetry = { retry() }
                                )
                            }
                        }
                        loadState.append is LoadState.Error -> {
                            val e = movies.loadState.append as LoadState.Error
                            item {
                                RetryView(
                                    message = e.error.localizedMessage ?: "Error",
                                    onClickRetry = { retry() }
                                )
                            }
                        }
                        loadState.append.endOfPaginationReached -> {
                            if (movies.itemCount == 0) {
                                item {
                                    EmptyView()
                                }
                            } else {
                                item {
                                    EndOfPaginationView()
                                }
                            }
                        }
                    }

                }
            }
        )
    }
}
