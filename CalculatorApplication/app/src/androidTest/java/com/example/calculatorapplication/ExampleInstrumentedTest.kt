<<<<<<<< HEAD:CalculatorApplication/app/src/androidTest/java/com/example/calculatorapplication/ExampleInstrumentedTest.kt
package com.example.calculatorapplication
========
package com.example.greetingapp
>>>>>>>> 75864eede69b56cfe6bec5af2c4b33871f68bc51:GreetingApp/app/src/androidTest/java/com/example/greetingapp/ExampleInstrumentedTest.kt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
<<<<<<<< HEAD:CalculatorApplication/app/src/androidTest/java/com/example/calculatorapplication/ExampleInstrumentedTest.kt
        assertEquals("com.example.calculatorapplication", appContext.packageName)
========
        assertEquals("com.example.greetingapp", appContext.packageName)
>>>>>>>> 75864eede69b56cfe6bec5af2c4b33871f68bc51:GreetingApp/app/src/androidTest/java/com/example/greetingapp/ExampleInstrumentedTest.kt
    }
}