package klt.mdy.offlinesupportwithpaging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import klt.mdy.offlinesupportwithpaging.domain.Repository
import klt.mdy.offlinesupportwithpaging.ui.udf.MainEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {
    val movies = repository.getMovies().cachedIn(viewModelScope)

    private val _mainEvent = MutableSharedFlow<MainEvent>()
    val mainEvent : SharedFlow<MainEvent> get() = _mainEvent

    fun onAction(id : Int) {
        viewModelScope.launch {
            _mainEvent.emit(MainEvent.ShowSnack(message = id.toString() ))
        }
    }
}