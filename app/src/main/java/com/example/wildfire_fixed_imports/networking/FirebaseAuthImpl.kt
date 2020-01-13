package com.example.wildfire_fixed_imports.networking

import android.hardware.biometrics.BiometricPrompt
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.await
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/*
*
* FirebaseAuthImpl provides some relatively simple methods to allow registering a new user with firebase, logging in, signing out and
* updating user information
*
* class AuthenticationDataRepository
*                &
* class AuthenticationState
*
* provide support structures for firebaseauthimpl
*
* */

class FirebaseAuthImpl () {

        private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

        private val firebaseAnalytics = applicationLevelProvider.mFirebaseAnalytics

        private  val firebaseAuth =applicationLevelProvider.firebaseAuth

        private val autheticationRepo = AuthenticationDataRepository(firebaseAuth)

        private val authenticationState = AuthenticationState()
        private val TAG = "firebaseauthimpl"



        fun checkUserSignedIn() : FirebaseUser? {
                // Check if user is signed in (non-null) and update UI accordingly.
                val currentUser = firebaseAuth.currentUser
                if (applicationLevelProvider.firebaseUser == null && currentUser == null) {
                        return null
                }
                else if (currentUser != null) {
                        applicationLevelProvider.firebaseUser = currentUser
                        return currentUser
                }

                //TODO()!
                return null
        }


        suspend fun registerCoroutine(email:String,password:String): FirebaseUser? {
                var result:FirebaseUser? = null
                try {
                        autheticationRepo.register(email, password)?.let {
                                authenticationState.postValue(
                                        true
                                )
                             /*   applicationLevelProvider.firebaseUser=it
                                result=it*/
                                applicationLevelProvider.firebaseUser= firebaseAuth.currentUser
                                result =firebaseAuth.currentUser
                                Timber.i("$TAG ${it.toString()} and $it")
                                Timber.i("$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}")
                                Timber.i("$TAG  ${applicationLevelProvider.firebaseUser?.email}")
                        } ?: run {
                                authenticationState.postValue(
                                        false)
                        }



                } catch (e: FirebaseAuthException) {
                        authenticationState.postValue(
                                false)
                        Timber.i("$TAG.vmscope exception: $e")
                }

                return result
        }
        suspend fun signinCoroutine(email:String,password:String): FirebaseUser? {
                var result:FirebaseUser? = null
                try {
                autheticationRepo.authenticate(email, password)?.let {
                        authenticationState.postValue(
                                true
                        )
                        applicationLevelProvider.firebaseUser=it
                        result=it
                        Timber.i("$TAG ${it.toString()} and $it")
                        Timber.i("$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}")
                        Timber.i("$TAG  ${applicationLevelProvider.firebaseUser?.email} \n ${applicationLevelProvider.firebaseUser?.describeContents().toString()} \n ${applicationLevelProvider.firebaseUser?.displayName}")
                } ?: run {
                        authenticationState.postValue(
                                false)
                }



                } catch (e: FirebaseAuthException) {
                        authenticationState.postValue(
                                false)
                        Timber.i("$TAG.vmscope exception: $e")
                }

                return result
        }




        fun signoutUser():Boolean {
                if (firebaseAuth.currentUser != null) {
                        Timber.d( "$TAG,  user not null ${firebaseAuth.currentUser}\n running firebaseauth.signOut()")
                firebaseAuth.signOut()
                        Timber.d( "$TAG,  user signed out, current status ${firebaseAuth.currentUser}")
                        applicationLevelProvider.firebaseUser = null
                }
                if (firebaseAuth.currentUser != null) {
                        //failed to sign out somehow
                        Timber.d( "$TAG,  sign out failed ${firebaseAuth.currentUser}")
                        return false
                }
                else {
                        //sign out successful
                        Timber.d( "$TAG,  exiting sign out method returning true")
                        return true
                }
        }




        @Deprecated("these are the traditional call back style implementations, deprecated")
        fun registerNewUserAccount(email:String,password:String) {

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(applicationLevelProvider.currentActivity) { task ->
                                if (task.isSuccessful) {
                                        // register success, update applicationlevelprovider with the current user
                                        Timber.d( "$TAG, createUserWithEmail:success")
                                        val user = firebaseAuth.currentUser
                                        if (user != null) {
                                                Timber.d( "$TAG, ${user.toString()}")
                                                applicationLevelProvider.firebaseUser =user
                                        }

                                } else {
                                        // If register fails, display a message to the user.
                                        Timber.d( "$TAG, createUserWithEmail:failed")
                                        Toast.makeText(applicationLevelProvider.currentActivity, "register failed.",
                                                Toast.LENGTH_SHORT).show()

                                }

                                // ...
                        }

        }

        @Deprecated("these are the traditional call back style implementations, deprecated")
        fun signinUser(email:String,password:String)  {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(applicationLevelProvider.currentActivity) { task ->
                                if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Timber.d( "$TAG,  signInWithEmail:success")
                                        val user = firebaseAuth.currentUser
                                        if (user != null) {
                                                applicationLevelProvider.firebaseUser =user
                                        }

                                } else {
                                        // If sign in fails, display a message to the user.
                                        Timber.d( "$TAG,  signInWithEmail:failure ${task.exception}")
                                        Toast.makeText(applicationLevelProvider.currentActivity, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show()

                                }

                                // ...
                        }

        }

}


class AuthenticationDataRepository constructor(
        private val firebaseAuth: FirebaseAuth
) {
        suspend fun register(
                email: String,
                password: String
        ): AuthResult? {
                val sauce: AuthResult? =firebaseAuth.createUserWithEmailAndPassword(
                        email, password).await()

              /*  if () {
                        // register success, update applicationlevelprovider with the current user
                        Timber.d( "$TAG, createUserWithEmail:success")
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                                Timber.d( "$TAG, ${user.toString()}")
                                applicationLevelProvider.firebaseUser =user
                        }

                } else {
                        // If register fails, display a message to the user.
                        Timber.d( "$TAG, createUserWithEmail:failed")
                        Toast.makeText(applicationLevelProvider.currentActivity, "register failed.",
                                Toast.LENGTH_SHORT).show()

                }*/
                Timber.i("firebase auth $sauce and ${sauce.toString()}")
                return sauce
                //throw FirebaseAuthException("", "")
        }
        suspend fun authenticate(
                email: String,
                password: String
        ): FirebaseUser? {
                val sauce =firebaseAuth.signInWithEmailAndPassword(
                        email, password).await()
                Timber.i("firebase auth $sauce and ${sauce.toString()}")
                return firebaseAuth.currentUser ?:
                throw FirebaseAuthException("", "")
        }
}

class AuthenticationState(){
        private val _authenticated = MutableLiveData<Boolean>().apply {
                value = false
        }
        val authenticated: LiveData<Boolean> = _authenticated

        fun postValue(boolean: Boolean) {
                _authenticated.postValue(boolean)
        }

}