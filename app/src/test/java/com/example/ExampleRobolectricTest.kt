package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Yesmin AI", appName)
  }

  @Test
  fun `verify bottomNavScreens are non-null and have valid routes`() {
    val screens = com.example.ui.navigation.YesminScreen.bottomNavScreens
    assertEquals(5, screens.size)
    screens.forEach { screen ->
      org.junit.Assert.assertNotNull(screen)
      org.junit.Assert.assertNotNull(screen.route)
      org.junit.Assert.assertTrue(screen.route.isNotBlank())
    }
  }
}
