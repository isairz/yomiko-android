package kr.isair.yomiko.adapter

interface NavigationListener {
    fun select(position: Int)
    fun goPrevChapter()
    fun goNextChapter()
    fun size() : Int

    fun canPrevCahtper() : Boolean
    fun canNextCahtper() : Boolean
}