package com.example.wildfire_fixed_imports


import android.Manifest
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.example.wildfire_fixed_imports.view.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTests {

    @Rule @JvmField val grantPermissions = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
        /*,Manifest.permission.WRITE_EXTERNAL_STORAGE*/)

    private var device : UiDevice? = null

    @get:Rule
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testFeedbackLocationPermissionDenied() {
        val denyButton = this.device?.findObject(UiSelector().text("DENY"))
        val permissionDeniedMessage = this.device?.findObject(UiSelector().text("Permission denied"))

        denyButton!!.click()

        assert(permissionDeniedMessage!!.exists())
    }

    @Test
    fun testFeedbackLocationPermissionAllowed() {
        val allowButton = this.device?.findObject(UiSelector().text("ALLOW"))
        var permissionAllowedMessage = this.device?.findObject(UiSelector().text("Permission allowed"))
        allowButton!!.click()
        assert(permissionAllowedMessage!!.exists())
    }


}
