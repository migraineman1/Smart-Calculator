package calculator

import java.math.BigInteger

// Calculator storage of variables entered by the user stored in the format variableName:value
var variables = mutableMapOf<String, BigInteger>()

// function to check if a string contains non-Latin characters
fun containsNonLatinCharacters(text: String): Boolean {
    return text.any { !it.isLatin() }
}

// function to check if a character is part of the Latin character set
fun Char.isLatin(): Boolean {
    return this in 'A'..'Z' || this in 'a'..'z'
}

// function to see if a variable has a valid name, position variable selects error message
fun validVariableName(name: String, position: Int): Boolean {
    if (containsNonLatinCharacters(name)) {
        if (position == 1) println("Invalid identifier")
        else println("Invalid assignment")
        return false
    }
    if (name.matches("""[a-zA-Z]+""".toRegex())) return true
    else {
        if (position == 1) println("Invalid identifier")
        else println("Invalid assignment")
        return false
    }
}

// function to see if a variable name already exists, position variable selects error message
fun variableExists(name: String, position: Int): Boolean {
    if (variables.containsKey(name)) {
        return true
    } else {
        if (position == 1) println("Unknown variable")
        else println("Invalid assignment")
        return false
    }
}

//
fun processVariable(variable: String): BigInteger {
    if (validVariableName(variable, 2)) {
        if (variableExists(variable, 2)) {
            return variables[variable]!!
        }

    }
    return -1.toBigInteger()
}

fun parseInput(inval: String): String {
    var input = inval
    val returnString = StringBuilder()
    while (input.isNotEmpty()) {
        when (input[0]) {
            // is variable, retrieve value and put value into return string and remove from input
            in 'a'..'z', in 'A'..'Z' -> {
                val variableName = "[a-zA-Z]+".toRegex().find(input)
                val result = processVariable(variableName!!.value)
                if (returnString.length != 0) returnString.append(" ")
                returnString.append(result.toString())
                input = input.drop(variableName.value.length)
            } // end of is variable

            // is number, put number in return string and remove from input
            in '0'..'9' -> {
                val number = "[0-9]+".toRegex().find(input)
                if (returnString.length != 0) returnString.append(" ")
                returnString.append(number!!.value)
                val len = 1
                input = input.drop(number.value.length)
            } // end of is number

            // is one or more plus signs
            '+' -> {
                val plusSigns = "[+]+".toRegex().find(input)
                if (returnString.length != 0) returnString.append(" ")
                returnString.append('+')
                input = input.drop(plusSigns!!.value.length)
            } // end of is one or more plus signs

            // is one or more minus signs
            '-' -> {
                val minusSigns = "[-]+".toRegex().find(input)
                if (returnString.length != 0) returnString.append(" ")
                if (minusSigns!!.value.length % 2 == 0) returnString.append('+') else returnString.append(
                    '-'
                )
                input = input.drop(minusSigns!!.value.length)
            } // end of is one or more minus signs

            // is ( ) * /
            '(', ')', '*', '/' -> {
                if (returnString.length != 0) returnString.append(" ")
                returnString.append(input[0])
                input = input.drop(1)
            } // end is ( ) * /

            else -> input = input.drop(1)

        } // end of when
    } // end of while
    return returnString.toString()
}

fun appendAndPop(sb: StringBuilder, deque: ArrayDeque<String>) {
    sb.append(" ")
    sb.append(deque.last())
    deque.removeLast()
}

fun convertToPostfix(input: String): String {
    var equation = input
    while (equation.contains('(')) {
        val startIndex = equation.indexOf('(')
        var endIndex = equation.lastIndexOf(')')
        var openParenthesisCount = 1
        var closeParenthesisCount = 0
            for (i in startIndex + 1 until equation.length) {
                if (equation[i] == '(') openParenthesisCount++
                if (equation[i] == ')') closeParenthesisCount++
                if (openParenthesisCount == closeParenthesisCount){
                    endIndex = i
                    break
                }
            }


        val result = solveEquation(equation.substring(startIndex + 1, endIndex))
        val sb = StringBuilder(equation)
        sb.deleteRange(startIndex, endIndex + 1)
        sb.insert(startIndex, " $result ")
        equation = sb.toString()
    }


    var equationComponents = equation.split(" ")

    // using ArrayDeque allows manipulation with add(), removeLast(), and value retrieval with last()
    var deque = ArrayDeque<String>()
    val sb = StringBuilder()
// need to work directly from string so () parts can be excised from string when necessary
    equationComponents = equationComponents.filter { it != "" }

    for (component in equationComponents) {
        if (component.matches(Regex("""-?\d+"""))) {
            if (sb.length != 0) sb.append(' ')
            sb.append(component)
        } else {
            if (deque.size == 0) deque.add(component)
            else {
                when (component) {
                    "+", "-" -> {
                        // if previous operation on stack is * or / append to postfix equation and pop stack
                        if (deque.size > 0) {
                            if ((deque.last() == "*") || (deque.last() == "/")) {
                                appendAndPop(sb, deque)

                                // if deque is empty just add component
                                if (deque.size == 0) deque.add(component)

                                // else if previous operation on stack is + or - append to postfix equation and pop stack
                                else if ((deque.last() == "+") || (deque.last() == "-")) {
                                    appendAndPop(sb, deque)
                                    deque.add(component)
                                }
                            } else if ((deque.last() == "+") || (deque.last() == "-")) {
                                appendAndPop(sb, deque)
                                deque.add(component)
                            }
                        } else
                            // if stack is empty
                            deque.add(component)

                    }

                    "*", "/" -> {
                        // if previous operation on stack is * or / append to postfix equation and pop stack
                        if (deque.size > 0) {
                            if ((deque.last() == "*") || (deque.last() == "/")) {
                                appendAndPop(sb, deque)
                            }
                        }
                        deque.add(component)

                    }

                }
            }

        }
    }
    do {
        appendAndPop(sb, deque)
    } while (deque.isNotEmpty())
    // if empty string was passed in
    // if (sb.isEmpty())  sb.append('0')

    return sb.toString()
}

fun solvePostfix(equation: String): BigInteger {
    val equationComponents = equation.split(" ")

    // using ArrayDeque allows manipulation with add(), removeLast(), and value retrieval with last()
    val deque = ArrayDeque<String>()

    for (component in equationComponents) {
        if (component.contains(Regex("""[0-9]"""))) {
            deque.add(component)
        } else {
            val second = deque.last().toBigInteger()
            deque.removeLast()
            val first = deque.last().toBigInteger()
            deque.removeLast()
            when (component) {
                "+" -> deque.add((first + second).toString())
                "-" -> deque.add((first - second).toString())
                "*" -> deque.add((first * second).toString())
                "/" -> deque.add((first / second).toString())
            }
        }
    }
    return deque.last().toBigInteger()
}

fun solveEquation(equation: String): BigInteger {
    var input = equation
    input = parseInput(input)
    // convert to postfix format equation and solve
    input = convertToPostfix(input)
    return solvePostfix(input)
}

fun main() {
    // allow for a null entry at the console
    val helpMessage = """The program calculates the sum and difference of numbers
                    it accepts variables of 1 or more alphabetical characters
                    Examples:
                    a = 10
                    b = -5
                    sum = a + b
                    sum   (outputs 5)
                    or a straight equation 5 + 3 * 7
                """
    var input: String? = ""

    //main body of loop
    while (input != "/exit") {
        // check for null entry at console
        input = readln() ?: continue

        // check for input consisting only of whitespace
        if (input.isBlank()) continue
        else {
            input = input.trim()
            if (input[0] == '/' && !(input == "/exit" || input == "/help")) {
                println("Unknown command")
                continue
            }

            if (input != "/exit") {

                // if help command print the help message
                if (input == "/help") {
                    println(helpMessage.trimMargin())
                    continue
                }

                // check if equation is valid
                if ((input.count { it.equals('(') } != input.count { it.equals(')') }) || input.count { it.equals('=') } > 1 || input.contains("""(%{2}|/{2}|\*{2})""".toRegex()))  {
                    println("Invalid expression")
                    continue
                }

                // is input an assignment
                if (input.contains('=')) {

                    // check if input contains variables
                    if (input.contains("""[a-zA-Z]+""".toRegex())) {


                        // check if input is a variable assignment
                        if (input.split("=").size == 2) {
                            var (first: String, second: String) = input.split("=")
                            first = first.trim()
                            second = second.trim()

                            // check if variable assignment by equation
                            if (second.contains("""[+\-*/]""".toRegex()) && !second.matches(Regex("""-?\d+"""))) {
                                variables[first] = solveEquation(second)
                                continue
                            } else {

                                // is first parameter a valid variable, if so prepare to assign it a value
                                if (validVariableName(first, 1)) {

                                    // if second parameter is an integer assign to variable
                                    if (second.all { it in ('0'..'9').toSet() || it == '+' || it == '-' }) {
                                        variables[first] = second.toBigInteger()
                                    } else {

                                        // is second variable valid and does it exist
                                        if (validVariableName(second, 2)) {
                                            if (variableExists(second, 2)) {

                                                // if so does first variable exist, if so assign it the value of the second variable
                                                // if not, create it
                                                variables[first] = variables[second]!!
                                            }
                                        } else continue
                                    }

                                }

                            }
                        } // close of variable assignment
                    }

                } else {
                    // if input is just a variable print its value
                    if (input.matches("""[a-zA-Z]+""".toRegex())) {
                        if (variableExists(input, 1)) println(variables[input])
                        continue
                    }
                    // input is a straight equation so solve
                    println(solveEquation(input))
                }

            }
        }
    }
    println("Bye!")
}

