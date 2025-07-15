package com.fit3163.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class AuthViewModel : ViewModel(){
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState


    init {
        checkAuthStatus()
    }
    fun checkAuthStatus(){
        if(auth.currentUser == null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated

                }else{
//                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                    _authState.value = AuthState.Error("Wrong account/password")

                }
            }
    }

    fun signup(name: String, email: String, password: String) {
        if (name.isEmpty()) {
            _authState.value = AuthState.Error("Name can't be empty")
            return
        }

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email / Password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                _authState.value = AuthState.SignupSuccess
                            } else {
                                _authState.value = AuthState.Error(updateTask.exception?.message ?: "Failed to update profile")
                            }
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }




    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object SignupSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}