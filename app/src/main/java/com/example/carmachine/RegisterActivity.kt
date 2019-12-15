package com.example.carmachine

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//MainActivity -> Registration
class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    companion object {
        //logcat
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        already_have_account_text_View.setOnClickListener {
            Log.d(TAG, "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectPhoto_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        register_button_register.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            performRegister()
            //saveUserToFirebaseDatabase(it.toString())
        }
    }

    var selectedPhotoUri: Uri? = null
    //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //proceed and chec what the selected image was...
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            //Photo location
            //val selectedPhotoUri = data.data
            selectedPhotoUri = data.data

            //Bitmap of photo
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            val bitmapDrawable = BitmapDrawable(bitmap)
            selectPhoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        baseContext, "Authentication ok.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // ...
            }
    }


    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

//    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
//        val uid = FirebaseAuth.getInstance().uid ?: ""
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//
//        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
//
//        ref.setValue(user)
//            .addOnSuccessListener {
//                Log.d(TAG, "Finally we saved the user to Firebase Database")
//            }
//            .addOnFailureListener {
//                Log.d(TAG, "Failed to set value to database: ${it.message}")
//            }
//    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")

        val user = User(
            username_edittext_register.text.toString(),
            email_edittext_register.text.toString(),
            profileImageUrl,
            password_edittext_register.text.toString()
        )

        myRef.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
        Toast.makeText(
            baseContext, "User add to database.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        database.child("users").child(userId).setValue(user)
    }
}


//class User(val username: String, val profileImageUrl: String)

@IgnoreExtraProperties
data class User(
    var username: String? = "",
    var email: String? = "",
    val profileImageUrl: String? = "",
    val password: String? = ""
)