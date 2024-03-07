package com.example.calculatorapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import net.objecthunter.exp4j.ExpressionBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var  expression : TextView
    private lateinit var result : TextView
    private lateinit var clearButton : Button
    private lateinit var backSpace : Button
    private lateinit var percentageSign : Button
    private lateinit var division : Button
    private lateinit var multiplication : Button
    private lateinit var subtraction : Button
    private lateinit var equal : Button
    private lateinit var addition : Button

    private lateinit var one : Button
    private lateinit var two : Button
    private lateinit var three : Button
    private lateinit var four : Button
    private lateinit var five : Button
    private lateinit var six : Button
    private lateinit var seven : Button
    private lateinit var eight : Button
    private lateinit var nine: Button
    private lateinit var zero : Button
    private lateinit var point : Button





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clearButton = findViewById(R.id.btnACA)
        expression = findViewById(R.id.expression)
        backSpace = findViewById(R.id.btnBS)
        percentageSign = findViewById(R.id.btnPercent)
        division = findViewById(R.id.btnD)
        multiplication = findViewById(R.id.btnM)
        subtraction = findViewById(R.id.btnS)
        equal = findViewById(R.id.btnE)
        addition = findViewById(R.id.btnA)
        one = findViewById(R.id.btn1)
        two = findViewById(R.id.btn2)
        three = findViewById(R.id.btn3)
        four = findViewById(R.id.btn4)
        five = findViewById(R.id.btn5)
        six = findViewById(R.id.btn6)
        seven = findViewById(R.id.btn7)
        eight = findViewById(R.id.btn8)
        nine = findViewById(R.id.btn9)
        zero = findViewById(R.id.btn0)
        point = findViewById(R.id.btnP)

        expression = findViewById(R.id.expression)
        result = findViewById(R.id.result)

        // Event Listeners
        one.setOnClickListener{appendToExpression("1",true)}
        two.setOnClickListener{ appendToExpression("2",true)}
        three.setOnClickListener{appendToExpression("3",true)}
        four.setOnClickListener{appendToExpression("4",true)}
        five.setOnClickListener{appendToExpression("5",true)}
        six.setOnClickListener{appendToExpression("6",true)}
        seven.setOnClickListener{appendToExpression("7",true)}
        eight.setOnClickListener{appendToExpression("8",true)}
        nine.setOnClickListener{appendToExpression("9",true)}
        zero.setOnClickListener{appendToExpression("0",true)}

        clearButton.setOnClickListener{
            clear()
        }
        backSpace.setOnClickListener{
            val  myExpression = expression.text.toString()
            if (myExpression.isNotEmpty()){
                expression.text =  myExpression.substring(0,myExpression.length-1)
            }
        }
        multiplication.setOnClickListener{appendToExpression("*", false)}
        addition.setOnClickListener{appendToExpression("+", false)}
        subtraction.setOnClickListener{appendToExpression("-", false)}
        point.setOnClickListener{appendToExpression(".", false)}
        percentageSign.setOnClickListener{appendToExpression("%", false)}
        division.setOnClickListener{appendToExpression("/", false)}
        equal.setOnClickListener{
            calculateResult()
        }
    }
    private fun calculateResult(){
        try {
            val expressionInput = ExpressionBuilder(expression.text.toString()).build()
            val expressionOutput = expressionInput.evaluate()
            val expressionLongInput = expressionOutput.toLong()

            if (expressionOutput == expressionLongInput.toDouble()){
                result.text =expressionLongInput.toString()
            }else{
                result.text = result.toString()
            }
        }catch (e:Exception){
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun clear() {
        expression.text = ""
        result.text = ""
    }
    private fun appendToExpression(inputString : String, clearStatus : Boolean){
        if (clearStatus){
            result.text = ""
            expression.append(inputString)
        }else{
            expression.append(result.text)
            expression.append(inputString)
            result.text = ""
        }
    }

}