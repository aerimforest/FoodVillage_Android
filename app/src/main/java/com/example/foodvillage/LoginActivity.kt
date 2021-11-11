package com.example.foodvillage

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.foodvillage.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
    private val GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        
        // 맵 사용 등록을 위한 해시 키 생성 함수
//        getHashKey()

        auth = FirebaseAuth.getInstance()

        binding.btnLoginGoogle.setOnClickListener {
            googleLogin()
        }

        binding.btnLoginEmail.setOnClickListener {
            logInWithEmail()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun getHashKey(){
        var packageInfo : PackageInfo = PackageInfo()
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException){
            e.printStackTrace()
        }

        for (signature: Signature in packageInfo.signatures){
            try{
                var md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch(e: NoSuchAlgorithmException){
                Log.e("KEY_HASH", "Unable to get MessageDigest. signature = " + signature, e)
            }
        }
    }



    // 로그인 한 적이 있는지 확인
    public override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        // 이미 로그인 한 사용자인 경우
        if (auth.currentUser != null) {
            moveMainPage(auth.currentUser)
        } else if (account != null) {
            moveMainPage(auth.currentUser)
        }
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            // 구글에서 넘겨주는 로그인 결과 값 받아오기
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = result.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {

            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //moveMainPage(task.result?.user)
                    moveUserNamePage(auth.currentUser)
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        // user가 있는 경우(로그인 한 적이 있는 경우)
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun moveUserNamePage(user: FirebaseUser?) {
        // user가 있는 경우(로그인 한 경우)
        if (user != null) {
            //val intent = Intent(this, UserNameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createAndLoginEmail() {
        auth.createUserWithEmailAndPassword(
            binding.etLoginId.text.toString(),
            binding.etLoginPwd.text.toString()
        )
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        // moveUserNamePage(auth.currentUser)
                    }
                    task.exception?.message.isNullOrEmpty() -> {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        signInEmail()
                    }
                }
            }
    }

    private fun logInWithEmail() {
        if (binding.etLoginId.text.toString()
                .isEmpty() || binding.etLoginPwd.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            createAndLoginEmail()
        }
    }

    private fun signInEmail() {
        auth.signInWithEmailAndPassword(
            binding.etLoginId.text.toString(),
            binding.etLoginPwd.text.toString()
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    moveMainPage(auth.currentUser)
                } else {
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}