package com.example.wildfire_fixed_imports.networking

import android.widget.Toast
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber

class FirebaseAuthImpl () {

        val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

        val firebaseAnalytics = applicationLevelProvider.mFirebaseAnalytics

        val firebaseAuth =applicationLevelProvider.firebaseAuth


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

        fun registerNewUserAccount(email:String,password:String) :Boolean {
                var result = false
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(applicationLevelProvider.currentActivity) { task ->
                                if (task.isSuccessful) {
                                        // register success, update applicationlevelprovider with the current user
                                        Timber.d( "$TAG, createUserWithEmail:success")
                                        val user = firebaseAuth.currentUser
                                        if (user != null) {
                                                applicationLevelProvider.firebaseUser =user
                                        }
                                        result = true
                                } else {
                                        // If register fails, display a message to the user.
                                        Timber.d( "$TAG, createUserWithEmail:success")
                                        Toast.makeText(applicationLevelProvider.currentActivity, "register failed.",
                                                Toast.LENGTH_SHORT).show()
                                        result = false
                                }

                                // ...
                        }
                return result
        }

        fun signinUser(email:String,password:String)  :Boolean {
                var result = false
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(applicationLevelProvider.currentActivity) { task ->
                                if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Timber.d( "$TAG,  signInWithEmail:success")
                                        val user = firebaseAuth.currentUser
                                        if (user != null) {
                                                applicationLevelProvider.firebaseUser =user
                                        }
                                        result = true
                                } else {
                                        // If sign in fails, display a message to the user.
                                        Timber.d( "$TAG,  signInWithEmail:failure ${task.exception}")
                                        Toast.makeText(applicationLevelProvider.currentActivity, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show()
                                        result = false
                                }

                                // ...
                        }
                return result
        }

        fun signoutUser():Boolean {
                if (applicationLevelProvider.firebaseUser != null) {
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

}