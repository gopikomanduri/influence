//package com.example.facebookintegrationapp
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.facebookintegrationapp.ui.theme.FacebookIntegrationAppTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            FacebookIntegrationAppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    FacebookIntegrationAppTheme {
//        Greeting("Android")
//    }
//}
package com.example.facebookintegrationapp

import android.content.Intent
import android.media.FaceDetector.Face
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private val TAG  = "MainActivity"
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginButton: LoginButton
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // FacebookSdk.setClientToken("1222842719088477|281906ccb4868c6342f7a2777083d1a4");
      //  FacebookSdk.setClientToken("281906ccb4868c6342f7a2777083d1a4");
      //  FacebookSdk.sdkInitialize(applicationContext);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        setContentView(R.layout.activity_main)

        callbackManager = CallbackManager.Factory.create()
        loginButton = findViewById(R.id.login_button)
        statusTextView = findViewById(R.id.status_text_view)

        loginButton.setPermissions("email", "user_friends", "user_likes", "user_birthday",
            "posts", "user_gender", "user_age_range");
        Log.d("GOPII", "before registering call back.. GOPI");
        val id= FacebookSdk.getApplicationId();
        val name = FacebookSdk.getApplicationName();
        val clientid = FacebookSdk.getClientToken();
        Log.d("GOPII", "name is "+name+"   id is "+id+"   clientid "+clientid);

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {

                getUserDetails(result.accessToken);
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                statusTextView.text = "Login canceled"
            }

            override fun onError(error: FacebookException) {
                statusTextView.text = "Login error: ${error.message}"
            }
        })
    }

    private fun getUserDetails(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { `object`: JSONObject?, response: GraphResponse? ->
            try {
                val name = `object`!!.getString("name")
                val email = `object`!!.getString("email")
                val friends =
                    `object`!!.getJSONObject("friends").getString("summary")
                val likes = `object`!!.getJSONObject("likes").getString("summary")
                var user_posts: String? = null;
                val user_posts_details = `object`!!.getJSONObject("user_posts")
                if(user_posts_details!= null) {
                     user_posts = user_posts_details.getString("summary")
                }
                val user_gender = `object`!!.getString("user_gender")
                val user_age_range = `object`!!.getString("user_age_range")
               // val user_posts_details = `object`!!.getJSONObject("user_posts")


                Log.d(TAG, "Name: $name")
                Log.d(TAG, "Email: $email")
                Log.d(TAG, "Friends: $friends")
                Log.d(TAG, "Likes: $likes")
                Log.d(TAG, "user_posts: $user_posts")
                Log.d(TAG, "user_gender: $user_gender")
                Log.d(TAG, "user_age_range: $user_age_range")
                Log.d(TAG, "user_posts_details: $user_posts_details")

                Toast.makeText(this@MainActivity, "User: $name", Toast.LENGTH_LONG)
                    .show()
            } catch (e: JSONException) {
                Log.e(TAG, "JSON error: " + e.message)
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,friends.summary(true),likes.summary(true)")
        request.parameters = parameters
        request.executeAsync()
    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        // Use the access token to make API calls

        CoroutineScope(Dispatchers.IO).launch {
            val response = fetchFacebookData(token.token)
            withContext(Dispatchers.Main) {
                statusTextView.text = response
            }
        }
    }

    private suspend fun fetchFacebookData(accessToken: String): String {
        // Example API call to fetch user data (requires proper implementation)
        return "Facebook data fetched with token: $accessToken"
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
