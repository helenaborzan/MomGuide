package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Answer
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QandA
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.Question
import hr.ferit.helenaborzan.pregnancyhelper.model.data.questionnaire.QuestionnaireResult
import hr.ferit.helenaborzan.pregnancyhelper.model.service.AccountService
import kotlinx.coroutines.tasks.await

abstract class BaseInfoRepository(
    protected val accountService: AccountService,
    protected val firestore: FirebaseFirestore
) {
    protected abstract val collectionName: String
    protected val collection by lazy { firestore.collection(collectionName) }


    suspend fun ensureInfoDocument() {
        val userId = accountService.currentUserId
        val infoDocRef = collection.whereEqualTo("userId", userId).get().await()

        if (infoDocRef.isEmpty) {
            createInfoDocument(userId)
        }
    }

    protected abstract suspend fun createInfoDocument(userId: String)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addQuestionnaireResults(score: Int, resultMessage: String, selectedAnswers: Map<String, Answer?>, fieldName : String) {
        val userId = accountService.currentUserId
        val querySnapshot = collection.whereEqualTo("userId", userId).get().await()

        if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents[0]
            val documentId = document.id

            val qAndAList = selectedAnswers.map { (questionId, answer) ->
                QandA(questionId = questionId, selectedAnswer = answer )
            }

            val newResult = QuestionnaireResult(
                date = Timestamp.now(),
                score = score,
                resultMessage = resultMessage,
                results = qAndAList
            )

            val documentReference = collection.document(documentId)
            documentReference.update(fieldName, FieldValue.arrayUnion(newResult)).await()
        }
    }
}