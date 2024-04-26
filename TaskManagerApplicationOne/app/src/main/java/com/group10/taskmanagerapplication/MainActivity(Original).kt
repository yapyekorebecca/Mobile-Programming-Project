package com.group10.taskmanagerapplication
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.group10.taskmanagerapplication.databinding.ActivityMainBinding
//import com.group10.taskmanagerapplication.ui.TaskActivity
//
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    private lateinit var emailEditText: EditText
//    private lateinit var passwordEditText: EditText
//    private lateinit var loginButton: Button
//    private lateinit var registerText: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        emailEditText = findViewById(R.id.emailEditText)
//        passwordEditText = findViewById(R.id.passwordEditText)
//        loginButton = findViewById(R.id.loginButton)
//        registerText = findViewById(R.id.registerTextView)
//
//        // Set click listener for login button
//        loginButton.setOnClickListener {
//            // Get email and password entered by the user
//            val email = emailEditText.text.toString()
//            val password = passwordEditText.text.toString()
//
//            // Display email and password in a Toast
//            val message = "Email: $email\nPassword: $password"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//
//
//            val intent = Intent(this, TaskActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        // Set click listener for register text
//        registerText.setOnClickListener {
//            // Launch the RegisterActivity
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//
//
//
//    }
//}
