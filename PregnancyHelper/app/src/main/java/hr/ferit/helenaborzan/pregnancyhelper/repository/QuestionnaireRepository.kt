package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Question
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestionnaireRepository @Inject constructor(
    private val firestore : FirebaseFirestore
){
    fun getQuestionnaire(questionnaireName: String): Flow<List<Question>> = callbackFlow {
        firestore.collection(questionnaireName)
            .get()
            .addOnSuccessListener { result ->
                val questionnaire = mutableListOf<Question>()
                for (data in result.documents) {
                    val question = data.toObject(Question::class.java)
                    if (question != null) {
                        question.id = data.id
                        questionnaire.add(question)
                    }
                }
                trySend(questionnaire)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                close(exception)
            }
        awaitClose()
    }

    suspend fun incrementSelectedNumber(questionnaireName: String, questionId: String, answerText: String) {
        val questionRef = firestore.collection(questionnaireName).document(questionId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(questionRef)
            val question = snapshot.toObject(Question::class.java)

            question?.let {
                val updatedAnswers = it.answers.map { answer ->
                    if (answer.text == answerText) {
                        answer.copy(selectedNumber = answer.selectedNumber + 1)
                    } else {
                        answer
                    }
                }

                transaction.update(questionRef, "answers", updatedAnswers)
            }
        }.await()
    }

}