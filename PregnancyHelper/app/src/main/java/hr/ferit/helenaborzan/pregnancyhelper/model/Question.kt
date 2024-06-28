package hr.ferit.helenaborzan.pregnancyhelper.model

data class Question (
    var id : String = "",
    val questionText : String = "",
    val answers : MutableList<Answer> = mutableListOf<Answer>()
)