package klt.mdy.offlinesupportwithpaging.ui.udf

sealed class MainEvent {
    data class ShowSnack(val message: String) : MainEvent()
}
