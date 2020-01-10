package com.example.wildfire_fixed_imports

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/*
Bundle bundle = new Bundle();
bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/


fun FirebaseAnalytics.sendSelect(id:String,name:String,contentType:String) {
    val bundle =  Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
    this.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

}