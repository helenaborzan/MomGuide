package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.helenaborzan.pregnancyhelper.model.Question
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestionnaireRepository @Inject constructor(
    private val firestore : FirebaseFirestore
){
    fun getQuestionnaire(): Flow<List<Question>> = callbackFlow {
        firestore.collection("postPartumDepressionScale")
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
}