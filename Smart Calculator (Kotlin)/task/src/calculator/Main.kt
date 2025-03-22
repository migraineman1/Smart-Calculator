package calculator

var variables = mutableMapOf<String, Int>()

fun containsNonLatinCharacters(text: String): Boolean {
    return text.any { !it.isLatin() }
}

fun Char.isLatin(): Boolean {
    return this in 'A'..'Z' || this in 'a'..'z'
}

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

fun variableExists(name: String, position: Int): Boolean {
    if (variables.containsKey(name)) {
        return true
    } else {
        if (position == 1) println("Unknown variable")
        else println("Invalid assignment")
        return false
    }
}

fun saveVariable(name: String, value: Int) = variables.put(name, value)

fun updateVariable(name: String, value: Int) = value.also { variables[name] = it }

fun assignVariable(input: String, value: Int) {
    if (variableExists(input, 1)) {
        updateVariable(input, value)
    } else {
        saveVariable(input, value)
    }
}

//perform calculation
fun solve(operatorList: List<String>, numberList: List<Int>): Int {
    val integerList = numberList.map { it.toInt() }
    val result = integerList.reduceIndexed { index, acc, i ->
        when (operatorList[index]) {
            "+" -> acc + i
            "-" -> acc - i
            else -> acc
        }
    }
    return result
}

// split equation string into operands and integers
fun parseEquation(equation: String): Pair<List<String>, MutableList<String>> {
    var (operatorList: List<String>, numberList: List<String>) = equation.split(" ")
        .partition { """[\+|\-]+""".toRegex().matches(it) }

    val mutableOperatorList = operatorList.toMutableList()
    val mutableNumberList = numberList.toMutableList()

    // simplify string of multiple minus or plus signs
    for (index in operatorList.indices) {
        if (operatorList[index].contains('-'))
            if (operatorList[index].length % 2 == 0) mutableOperatorList[index] = "+"
            else mutableOperatorList[index] = "-"
        if (operatorList[index].contains('+')) mutableOperatorList[index] = "+"
    }

    // equalize size of operator list and number list
    if (operatorList.size != numberList.size) {
        mutableOperatorList.add(0, "+")
        operatorList = mutableOperatorList.toList()
    }

    return Pair(operatorList, mutableNumberList)
}

fun convertToNumbers(numbers: MutableList<String>): Pair<List<Int>, Boolean> {
    var success = true
    val mutableNumberList = mutableListOf<Int>()
    for (number in numbers) {
        if (number.matches(Regex("[a-zA-Z]+"))) {
            if (validVariableName(number, 2)) {
                if (variableExists(number, 2)) {
                    mutableNumberList.add(variables[number]!!)
                } else {
                    success = false
                    break
                }
            } else {
                success = false
                break
            }
        } else {
            if (number.matches(Regex("[0-9]+"))) {
                mutableNumberList.add(number.toInt())
            }
        }
    }
    return Pair(mutableNumberList.toList(), success)
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
                    or a straight equation 5 + 3 - 7
                """
    var input: String? = ""

    //main body of loop
    while (input != "/exit") {
        // check for null entry at console
        input = readln() ?: continue

        // check for input consisting only of whitespace
        if (input.isNotBlank()) {
            if (input[0] == '/' && !(input == "/exit" || input == "/help")) {
                println("Unknown command")
                continue
            }

            // if help command print the help message
            if (input == "/help") {
                println(helpMessage.trimMargin())
                continue
            }

            if (input != "/exit") {

                // check if numeric equation
                if (input.all { it in ('0'..'9').toSet() || it == '+' || it == '-' || it == ' ' }) {
                    val (operatorList, mutableNumberList) = parseEquation(input)
                    val (numberList, success) = convertToNumbers(mutableNumberList)
                    if (success) println(solve(operatorList, numberList))
                    continue
                }

                // strip out numbers and math symbols and check if input contains non latin characters
                if (containsNonLatinCharacters(input.filter { it !in setOf('+', '-', '=', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')  })) {
                    println("Invalid identifier")
                    continue
                }


                // check if input contains variables
                if (input.contains("""[a-zA-Z]+""".toRegex())) {

                    // does input contain only a single variable, if so print value of variable
                    if (input.split(" ", "=").size == 1) {
                        input = input.trim()
                        if (validVariableName(input, 1)) {
                            if (variableExists(input, 1)) {
                                println(variables[input])
                            } else continue
                        } else continue
                    }

                    // check if input is a variable assignment
                    if (input.split("=", "=").size == 2) {
                        var (first: String, second: String) = input.split("=")
                        first = first.trim()
                        second = second.trim()

                        // is first parameter a valid variable, if so prepare to assign it a value
                        if (validVariableName(first, 1)) {

                            // if second is an integer and assign to variable
                            if (second.all { it in ('0'..'9').toSet() || it == '+' || it == '-' }) {
                                variables[first] = second.toInt()
                            } else {

                                // is second variable valid and does it exist
                                if (validVariableName(second, 2)) {
                                    if (variableExists(second, 2)) {

                                        // if so does first variable exist, if so assign it the value of the second variable
                                        // if not, create it
                                        variables[first] = variables[second]!!
                                    } else continue
                                } else continue
                            }

                        }

                        // check if variable assignment by equation
                        if (second.contains("""("+"|"-")""".toRegex())) {
                            val (operatorList, mutableNumberList) = parseEquation(second)
                            val (numberList, success) = convertToNumbers(mutableNumberList)
                            if (success) println(solve(operatorList, numberList))
                            else continue
                        }

                    } // close of variable assignment
                }

                // was an equation  entered, if so solve
                if (input.contains("""[+\-]""".toRegex())) {
                    val (operatorList, mutableNumberList) = parseEquation(input)
                    val (numberList, success) = convertToNumbers(mutableNumberList)
                    if (success) println(solve(operatorList, numberList))
                    else continue
                }
            }

        }


    }

    println("Bye!")
}
