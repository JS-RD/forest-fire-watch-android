package com.example.wildfire_fixed_imports

//File provide static constants usable across the application,

const val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 666 // magic number for fine location request

const val MY_PERMISSIONS_REQUEST_INTERNET = 667 // magic number for internet permission request

const val WEB_BASE_URL = "https://wildfire-watch.herokuapp.com/"

const val DS_BASE_URL = "https://appwildfirewatch.herokuapp.com/"

//this is the string web's backend sends back on auth errors
const val AUTH_ERROR_STRING_WEB_BE = "You shall not pass"

//this is another error string occasionally provided
const val ALT_AUTH_ERROR_STRING_WEB_BE = "provide a token"