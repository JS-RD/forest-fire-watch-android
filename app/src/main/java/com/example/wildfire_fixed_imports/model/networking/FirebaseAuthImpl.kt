package com.example.wildfire_fixed_imports.model.networking

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber


/*
*
*                       1/30/2020
*       Likely could be slimmed down a bit, support classes probablyly pruned. -JS
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

        private  val firebaseAuth =applicationLevelProvider.firebaseAuth

        private val autheticationRepo = AuthenticationDataRepository(firebaseAuth)

        private val authenticationState = AuthenticationState()
        private val TAG = "firebaseauthimpl"

        val methodName = object : Any() {

        }.javaClass.enclosingMethod?.name


        fun cleanupUser():Boolean? {
                val result =signoutUser()
                if (result) {
                        Timber.i("$TAG $methodName succesfully signed out")
                        Timber.i("$TAG $methodName \n APL: ${applicationLevelProvider.firebaseUser} \n firebaseauth : ${firebaseAuth.currentUser}")
                return true
                }
                else if (applicationLevelProvider.firebaseUser == null && firebaseAuth.currentUser == null) {
                        Timber.i("$TAG $methodName user signed out but not by signoutUser()")
                        Timber.i("$TAG $methodName \n APL: ${applicationLevelProvider.firebaseUser} \n firebaseauth : ${firebaseAuth.currentUser}")
                        return true
                }
                else {
                        Timber.i("$TAG $methodName not signed out")
                        return false
                }
        }

        fun checkUserSignedIn() : FirebaseUser? {
                // Check if user is signed in (non-null) and update UI accordingly.
                val currentUser = firebaseAuth.currentUser
                if (applicationLevelProvider.firebaseUser == null && currentUser == null) {
                        return null
                }
                else if (currentUser != null) {
                        return currentUser
                }

                //TODO()!
                return null
        }


        suspend fun registerCoroutine(email:String,password:String): SuccessFailWrapper<FirebaseUser?> {
                var result:FirebaseUser? = null
                try {
                        autheticationRepo.register(email, password)?.let {

                                authenticationState.postValue(
                                        true
                                )
                             /*   applicationLevelProvider.firebaseUser=it
                                result=it*/
                                result =firebaseAuth.currentUser

                               // firebaseAuth.signInWithCustomToken(cred?.token ?:"sayce")
                               //val results = firebaseAuth.signInWithCredential(cred as AuthCredential).await()
                            //    Timber.i("$TAG ${results.credential} CREDCHECK and \n${results.user}")

                                Timber.i("$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}")
                                return SuccessFailWrapper.Success("Success",result)
                        } ?: run {
                                authenticationState.postValue(
                                        false)
                                return SuccessFailWrapper.Success("Failure",result)
                        }



                } catch (e: FirebaseAuthException) {
                        authenticationState.postValue(
                                false)
                        Timber.i("$TAG.vmscope exception: $e")
                        return SuccessFailWrapper.Exception("Exception -- ${e.localizedMessage}",e)
                }


        }
        suspend fun signinCoroutine(email:String,password:String): SuccessFailWrapper<FirebaseUser?> {
                var result:FirebaseUser? = null
                try {
                autheticationRepo.authenticate(email, password)?.let {
                        authenticationState.postValue(
                                true
                        )

                        result=it
                        Timber.i("$TAG ${it} and $it")
                        Timber.i("$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}")
                        Timber.i("$TAG  ${applicationLevelProvider.firebaseUser?.email} \n ${applicationLevelProvider.firebaseUser?.describeContents().toString()} \n ${applicationLevelProvider.firebaseUser?.displayName}")
                        return SuccessFailWrapper.Success("Success",result)
                }
                        ?: run {
                        authenticationState.postValue(
                                false)
                                return SuccessFailWrapper.Success("Failure",result)
                }

                } catch (e: FirebaseAuthException) {
                        authenticationState.postValue(
                                false)
                        Timber.i("$TAG.vmscope exception: $e")
                        return SuccessFailWrapper.Exception("Exception -- ${e.localizedMessage}",e)
                }


        }

         suspend fun deleteUser(email:String,password:String): Boolean?{

                //returns null if no user logged on and hence no user deleted,
                //returns true if user was successfully deleted
                //returns false if something went wrong

                val currentUser =applicationLevelProvider.firebaseUser
                if (currentUser == null || firebaseAuth.currentUser == null ) {
                        //no user is currently logged in, pop a toast letting the user know
                        Toast.makeText(applicationLevelProvider.applicationContext,
                                "You are not currently logged in, please log in and try again",
                                Toast.LENGTH_SHORT).show()
                        Timber.e("$TAG user not logged out as currentuser = $currentUser and ALP.firebaseuser =${applicationLevelProvider.firebaseUser}")

                        //run clean up method to make sure we gucci going forward
                        if (cleanupUser() == true) {
                        return null
                                }
                        else {
                                Toast.makeText(applicationLevelProvider.applicationContext,
                                        "something is very wrong, please restart application",
                                        Toast.LENGTH_SHORT).show()
                                return false
                        }
                }
                val userToDeleteEmail = firebaseAuth.currentUser?.email
                // run the delete command
                 val sauce = firebaseAuth.currentUser
                firebaseAuth.currentUser?.delete()?.await()
                //check to make sure no user is logged in and hence, the user is deleted
                if (firebaseAuth.currentUser ==null) {
                        Toast.makeText(applicationLevelProvider.applicationContext,
                                "User ($userToDeleteEmail) successfully deleted",
                                Toast.LENGTH_SHORT).show()
                        return true
                }
                else {
                        Toast.makeText(applicationLevelProvider.applicationContext,
                                "something is very wrong, please restart application",
                                Toast.LENGTH_SHORT).show()
                        return null
                }

        }

fun tokenHiJinx() {
        firebaseAuth
}



        fun signoutUser():Boolean {
                if (firebaseAuth.currentUser != null) {
                        Timber.d( "$TAG,  user not null ${firebaseAuth.currentUser}\n running firebaseauth.signOut()")
                firebaseAuth.signOut()
                        Timber.d( "$TAG,  user signed out, current status ${firebaseAuth.currentUser}")
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
                                               Timber.i("$TAG user = $user")
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