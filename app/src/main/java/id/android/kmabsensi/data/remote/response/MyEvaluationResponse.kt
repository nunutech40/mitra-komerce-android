package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class MyEvaluationResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val evaluations: List<Evaluation> = listOf()
)

data class Evaluation(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_evaluator_id")
    val userEvaluatorId: Int = 0,
    @SerializedName("user_target_id")
    val userTargetId: Int = 0,
    @SerializedName("evaluation_period")
    val evaluationPeriod: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: Any? = null,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("user_target")
    val userTarget: UserTarget = UserTarget(),
    @SerializedName("user_evaluator")
    val userEvaluator: UserEvaluator = UserEvaluator(),
    @SerializedName("form_evaluation_answer")
    val formEvaluationAnswer: List<FormEvaluationAnswer> = listOf()
)

data class UserTarget(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("username")
    val username: String = "",
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = ""
)

data class UserEvaluator(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("username")
    val username: String = "",
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = ""
)

data class FormEvaluationAnswer(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("form_evaluations_id")
    val formEvaluationsId: Int = 0,
    @SerializedName("evaluation_question_id")
    val evaluationQuestionId: Int = 0,
    @SerializedName("question_name")
    val questionName: String = "",
    @SerializedName("question_type")
    val questionType: Int = 0,
    @SerializedName("answer_value")
    val answerValue: String = "",
    @SerializedName("notes")
    val notes: String = ""
)