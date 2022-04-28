package klt.mdy.offlinesupportwithpaging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import klt.mdy.offlinesupportwithpaging.domain.Repository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {
    val movies = repository.getMovies().cachedIn(viewModelScope)
}