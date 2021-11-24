package com.example.foodvillage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivitySignupBinding
import com.example.foodvillage.login.ui.LoginActivity

class SignUpActivity : AppCompatActivity() {
    val TAG: String = "Register"

    var isExistBlank = false
    var isPwdSame = false
    var isIDExist = false

    private var mBinding: ActivitySignupBinding? = null
    private val binding get() = mBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
//
//        // 숨기기
//        binding.tvActivitySignupIdUnavailable.visibility = View.INVISIBLE
//        binding.tvActivitySignupNicknameUnavailable.visibility = View.INVISIBLE

        // 바인딩
        mBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 누르면 종료
        binding.btnActivitySignupBack.setOnClickListener{
            this.finish()
        }

        // 회원가입 버튼 클릭시
        binding.btnActivitySignup.setOnClickListener{
            val id = binding.etActivitySignupId.text.toString()
            val pwd = binding.etActivitySignupPwd.text.toString()
            val pwd_re = binding.etActivitySignupPwdcheck.text.toString()
            val nickname = binding.etActivitySignupNickname.text.toString()
            val phone = binding.etActivitySignupPhone.text.toString()

            // 빈 항목이 있을 경우
            if(id.isEmpty() || pwd.isEmpty() || pwd_re.isEmpty() || nickname.isEmpty() || phone.isEmpty()){
                Log.d(TAG, "빈 항목이 있음")
                isExistBlank = true
            }
            else{
                if(pwd == pwd_re){ // 비밀번호 같은지 확인
                    isPwdSame = true
                }
            }

            // 회원가입 성공시 로그인 액티비티로 이동
            // DB에 저장 필요!!!
            if(!isExistBlank && isPwdSame){
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            // 회원가입 실패시 상태에 따른 메시지 띄우기
            else{
                // 작성 안한 항목 있을 경우
                if(isExistBlank){
                    Toast.makeText(this, "압력란을 모두 작성해주세요.", Toast.LENGTH_SHORT).show()
                }
                // 입력 비밀번호가 다른 경우
                else if (!isPwdSame){
                    Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                }

//                // id가 이미 DB에 존재할 경우
//                else if(){
//                    binding.tvActivitySignupIdUnavailable.visibility = View.VISIBLE
//                }
//
//                // 닉네임이 이미 DB에 존재할 경우
//                else if(){
//
//                }
            }
        }
    }
}