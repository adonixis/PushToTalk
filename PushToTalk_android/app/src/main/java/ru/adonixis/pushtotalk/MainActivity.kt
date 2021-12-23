package ru.adonixis.pushtotalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.adonixis.pushtotalk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Constants.isIntiatedNow = true
        Constants.isCallEnded = true
        binding.btnRegister.setOnClickListener {
            if (binding.inputSubscriberNumber.text.toString().trim().isNullOrEmpty() ||
                binding.inputSubscriberNumber.text.toString().trim().length < 4 ||
                binding.inputSubscriberNumber.text.toString().trim().toIntOrNull() == null)
                binding.inputSubscriberNumber.error = "Введите 4-значный номер"
            else {
                db.collection("calls")
                    .document(binding.inputSubscriberNumber.text.toString())
                    .get()
                    .addOnSuccessListener {
                        if (it["type"] == "OFFER" || it["type"] == "ANSWER" || it["type"] == "END_CALL") {
                            binding.layoutSubscriberNumber.error = "Этот номер уже занят"
                        } else {
                            val intent = Intent(this@MainActivity, RTCActivity::class.java)
                            intent.putExtra("meetingID", binding.inputSubscriberNumber.text.toString())
                            intent.putExtra("isJoin", false)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                        binding.layoutSubscriberNumber.error = "Ошибка соединения"
                    }
            }
        }

        binding.btnCall.setOnClickListener {
            if (binding.inputSubscriberNumber.text.toString().trim().isNullOrEmpty() ||
                binding.inputSubscriberNumber.text.toString().trim().length < 4 ||
                binding.inputSubscriberNumber.text.toString().trim().toIntOrNull() == null)
                binding.layoutSubscriberNumber.error = "Введите 4-значный номер"
            else {
                db.collection("calls")
                    .document(binding.inputSubscriberNumber.text.toString())
                    .get()
                    .addOnSuccessListener {
                        if (!it.exists()) {
                            binding.layoutSubscriberNumber.error = "Абонента с таким номером не существует"
                        } else if (it["type"] == "ANSWER" || it["type"] == "END_CALL") {
                            binding.layoutSubscriberNumber.error = "Абонент занят"
                        } else {
                            val intent = Intent(this@MainActivity, RTCActivity::class.java)
                            intent.putExtra("meetingID", binding.inputSubscriberNumber.text.toString())
                            intent.putExtra("isJoin", true)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                        binding.layoutSubscriberNumber.error = "Ошибка соединения"
                    }
            }
        }
    }
}