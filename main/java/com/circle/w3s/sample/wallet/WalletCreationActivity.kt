package com.circle.w3s.sample.wallet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.circle.w3s.sample.wallet.databinding.WalletcreationpageBinding
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import android.content.Intent
import android.view.View

import android.widget.Button
import android.widget.Toast
import com.circle.w3s.sample.wallet.ui.main.LoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

data class UserInfoData(
    val data: UserDataDetail
)

data class UserDataDetail(
    val user: User
)

data class User(
    val id: String,
    val status: String,
    val createDate: String,
    val pinStatus: String,
    val pinDetails: PinDetails,
    val securityQuestionStatus: String,
    val securityQuestionDetails: SecurityQuestionDetails
)

data class PinDetails(
    val failedAttempts: Int
)

data class SecurityQuestionDetails(
    val failedAttempts: Int
)


class WalletCreationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(this, "Email login successful", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Email login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google Sign-In button click listener
        val google_sign_in_button = findViewById(R.id.google_sign_in_button)
        google_sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Email login button click listener
        val btnEmailLogin = findViewById(R.id.email_login_button)
        btnEmailLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmail(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
        val RC_SIGN_IN = 123456

        // Google Sign-In result handler
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)



            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign-In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    // Google Sign-In failed
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
            // Authenticate with Firebase using Google Sign-In credentials
            private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
                val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        val binding = WalletcreationpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //define all buttons, textViews and input fields
        val createWalletButton = binding.createWallet
        val backButton = binding.backbutton
        val loginButton = binding.loginBtn
        val submitButton = binding.submitbutton
        val proceedButton = binding.loginproceedbutton
        val progressBar = binding.progressBar

        val apiKeyEditText = binding.apiKeyEditText
        val userIdEditText = binding.userIdEditText
        val createUserTitle = binding.createUserTitle
        val listUserTitle = binding.listUserTitle
        val apiResponseTextView = binding.apiResponseTextView

        //Step 1 - REPLACE PLACEHOLDER WITH YOUR APP ID
        val appId = "53d5b151-ebbd-5d4b-9671-0df4a74ee22b"

        // Initially, hide the EditText fields and TextView
        apiKeyEditText.visibility = View.INVISIBLE
        userIdEditText.visibility = View.INVISIBLE
        createUserTitle.visibility = View.INVISIBLE
        listUserTitle.visibility = View.INVISIBLE
        submitButton.visibility =  View.INVISIBLE
        proceedButton.visibility = View.INVISIBLE
        backButton.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        apiResponseTextView.visibility = View.INVISIBLE

        createWalletButton.setOnClickListener {
            // This code will run when the "Create Wallet" button is clicked
            Log.d("WalletCreationActivity", "Create Wallet button clicked")

            // Show the EditText fields and TextView
            apiKeyEditText.visibility = View.VISIBLE
            userIdEditText.visibility = View.VISIBLE
            createUserTitle.visibility = View.VISIBLE
            submitButton.visibility =  View.VISIBLE
            backButton.visibility = View.VISIBLE

            // Hide the other component
            loginButton.visibility = View.INVISIBLE
            createWalletButton.visibility = View.INVISIBLE
        }

        backButton.setOnClickListener {
         // Show the EditText fields and TextView
            apiKeyEditText.visibility = View.INVISIBLE
            userIdEditText.visibility = View.INVISIBLE
            createUserTitle.visibility = View.INVISIBLE
            submitButton.visibility =  View.INVISIBLE
            proceedButton.visibility = View.INVISIBLE
            backButton.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            apiResponseTextView.visibility = View.INVISIBLE
            listUserTitle.visibility = View.INVISIBLE

            // Hide the other component
            loginButton.visibility = View.VISIBLE
            createWalletButton.visibility = View.VISIBLE

            // Clear the text in both userIdEditText and apiKeyEditText
            userIdEditText.text.clear()
            apiKeyEditText.text.clear()

            // Clear any existing error messages
            userIdEditText.error = null
            apiKeyEditText.error = null
        }

        submitButton.setOnClickListener {
            Log.d("WalletCreationActivity", "submit Wallet button clicked")
            // Get the text from the userIdEditText
            val userId = userIdEditText.text.toString().trim()
            if (userId.isEmpty()) {
                // userId is empty, display a warning message
                userIdEditText.error = "User ID is required"
            } else if (userId.length < 5 || userId.length > 50) {
                // userId length is not within the required range, display a warning message
                userIdEditText.error = "User ID must be between 5 and 50 characters"
            } else {
                // userId is valid, you can proceed with further actions here
                // Clear any previous error message
                userIdEditText.error = null
            }
            val apiKey = apiKeyEditText.text.toString().trim()
            if (apiKey.isEmpty()) {
                // userId is empty, display a warning message
                apiKeyEditText.error = "API Key is required"
            } else {
                // userId is valid, you can proceed with further actions here
                // Clear any previous error message
                apiKeyEditText.error = null
            }

            if (apiKeyEditText.error == null && userIdEditText.error == null) {
                // If there are no errors, proceed with the API call here
                progressBar.visibility = View.VISIBLE

                //call Circle API to create new user
                // Create OkHttp Client
                GlobalScope.launch(Dispatchers.IO) {
                      //Step 2 - PASTE CODE HERE FOR "CREATE USER" API
                    val client = OkHttpClient()
                    val mediaType = "application/json".toMediaTypeOrNull()
                    val body = "{\"userId\":\"$userId\"}".toRequestBody(mediaType)
                    val request = Request.Builder()
                        .url("https://api.circle.com/v1/w3s/users")
                        .post(body)
                        .addHeader("accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("authorization", "Bearer $apiKey")
                        .build()
                    // Inside your try-catch block for making the API call
                    try {
                        val response = client.newCall(request).execute()

                        runOnUiThread {
                            if (response.isSuccessful) {
                                val responseBody = response.body?.string()
                                // Process the response data
                                Log.d("WalletCreationActivity", "Response: $responseBody")

                                //If the request is successful, you will receive an empty response body. -> {}
                                //redirect page with APIkey and userId -> Acquire a Session Token
                                // Create an Intent to start the AcquireSessionActivity
                                val intent = Intent(this@WalletCreationActivity, AcquireSessionTokenActivity::class.java)

                                // Pass the apiKey and userId as extras to the new activity
                                intent.putExtra("apiKey", apiKey)
                                intent.putExtra("userId", userId)
                                intent.putExtra("appId", appId)

                                // Start the new activity
                                startActivity(intent)

                                // Finish the current activity if needed
                                finish()


                            } else {
                                // Handle error response
                                Log.e("WalletCreationActivity", "Error: ${response}")
                                progressBar.visibility = View.INVISIBLE
                                apiResponseTextView.visibility = View.VISIBLE
                                val errorCode = response.code // Assuming you have the error code from the API response
                                val errorMessage: String = when (errorCode) {
                                    401 -> "Invalid credentials"
                                    409 -> "Existing user already created with the provided userId."
                                    else -> "Unknown error"
                                }

                                apiResponseTextView.text = "Error ${errorCode}: ${errorMessage}. Please try again. "
                            }
                        }
                    } catch (e: IOException) {
                        // Handle network exception
                        Log.e("WalletCreationActivityError", "Error: ${e.message}", e)

                        runOnUiThread {
                            progressBar.visibility = View.INVISIBLE
                            apiResponseTextView.visibility = View.VISIBLE
                            apiResponseTextView.text = "Error: ${e.message}"
                        }
                    }

                }
            }
        }

        loginButton.setOnClickListener{
            Log.d("WalletCreationActivity", "Login button clicked")
            // Hide the other component
            loginButton.visibility = View.INVISIBLE
            createWalletButton.visibility = View.INVISIBLE

            // Show the EditText fields and TextView
            listUserTitle.visibility = View.VISIBLE
            apiKeyEditText.visibility = View.VISIBLE
            userIdEditText.visibility = View.VISIBLE
            proceedButton.visibility = View.VISIBLE
            backButton.visibility = View.VISIBLE
        }

        proceedButton.setOnClickListener {
            val userId = userIdEditText.text.toString().trim()
            if (userId.isEmpty()) {
                // userId is empty, display a warning message
                userIdEditText.error = "User ID is required"
            } else if (userId.length < 5 || userId.length > 50) {
                // userId length is not within the required range, display a warning message
                userIdEditText.error = "User ID must be between 5 and 50 characters"
            } else {
                // userId is valid, you can proceed with further actions here
                // Clear any previous error message
                userIdEditText.error = null
            }
            //validate API Key value
            val apiKey = apiKeyEditText.text.toString().trim()
            if (apiKey.isEmpty()) {
                // userId is empty, display a warning message
                apiKeyEditText.error = "API Key is required"
            } else {
                // userId is valid, you can proceed with further actions here
                // Clear any previous error message
                apiKeyEditText.error = null
            }
            if (apiKeyEditText.error == null && userIdEditText.error == null) {
                val loadingDialog = LoadingDialog(this, "Loading fetching your user data...") // Specify the loading text here
                loadingDialog.show()

                //call Circle API to list the user based on userId inputted
                GlobalScope.launch(Dispatchers.IO) {
                    val client = OkHttpClient()

                    val request = Request.Builder()
                        .url("https://api.circle.com/v1/w3s/users/$userId")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("authorization", "Bearer $apiKey")
                        .build()

                    try {
                        val response = client.newCall(request).execute()

                        if(response.isSuccessful){
                            val responseBody = response.body?.string()
                            // Use Gson to parse the JSON response into your data class
                            val gson = Gson()
                            val responseObject = gson.fromJson(responseBody, UserInfoData::class.java)
                            Log.d("WalletCreationActivity", "Data: $responseObject")
                            val userStatus = responseObject.data.user.status
                            val securityQuestionStatus = responseObject.data.user.securityQuestionStatus
                            val pinStatus = responseObject.data.user.pinStatus

                            if( userStatus != "ENABLED" || securityQuestionStatus != "ENABLED" || pinStatus != "ENABLED"){
                                loadingDialog.dismiss()
                                runOnUiThread {
                                    progressBar.visibility = View.INVISIBLE
                                    apiResponseTextView.visibility = View.VISIBLE

                                    apiResponseTextView.text = "User has not answered security questions or setup a PIN yet. Please go back and create a new wallet instead."
                                }
                            } else {
                                runOnUiThread {
                                    //redirect to acquire session token for userId
                                    val userIdResponse = responseObject.data.user.id
                                    Log.d("WalletCreationActivity", "Successfully obtain user data.")
                                    loadingDialog.dismiss()

                                    val intent = Intent(this@WalletCreationActivity, AcquireSessionTokenExistingUser::class.java)
                                    //pass data to next page
                                    intent.putExtra("apiKey", apiKey)
                                    intent.putExtra("userId", userId)
                                    intent.putExtra("appId", appId)
                                    // Start the new activity
                                    startActivity(intent)

                                    // Finish the current activity if needed
                                    finish()

                                }
                            }

                        } else {
                            // Update UI components
                            runOnUiThread {
                                Log.e("WalletCreationActivityError", "Error: ${response.code}")
                                progressBar.visibility = View.INVISIBLE
                                apiResponseTextView.visibility = View.VISIBLE
                                val errorCode = response.code // Assuming you have the error code from the API response
                                loadingDialog.dismiss()
//                            }

                                apiResponseTextView.text = "Error ${errorCode}:. Please try again. "
                            }
                        }

                    } catch (e: IOException) {
                        Log.e("WalletCreationActivityError", "Error: ${e.message}", e)

                        runOnUiThread {
                            progressBar.visibility = View.INVISIBLE
                            apiResponseTextView.visibility = View.VISIBLE
                            apiResponseTextView.text = "Error: ${e.message}"
                        }
                    }

                }

            }

        }


    }
}
