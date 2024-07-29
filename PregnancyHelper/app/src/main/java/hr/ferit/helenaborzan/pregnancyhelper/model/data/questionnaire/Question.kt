package hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire

data class Question (
    var id : String = "",
    val questionText : String = "",
    val answers : MutableList<Answer> = mutableListOf<Answer>()
)