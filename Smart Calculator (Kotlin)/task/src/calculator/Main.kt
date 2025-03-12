package calculator

var variables = mutableMapOf<String, Int>()

fun validVariableName(name: String): Boolean = name.matches("""[a-zA-Z]+""".toRegex())

fun variableExists(name: String) = variables.containsKey(name)

fun saveVariable(name: String, value: Int) = variables.put(name, value)

fun updateVariable(name: String, value: Int) = value.also { variables[name] = it }

fun assignVariable(input: String, value: Int) {
    if (variableExists(input)) {
        updateVariable(input, value)
    } else {
        saveVariable(input, value)
    }
}

//perform calculation
fun performCalculation(operatorList: List<String>, numberList: List<String>): Int {
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

fun main() {
    // allow for a null entry at the console
    var input: String? = ""
    var result = 0

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
                println("The program calculates the sum and difference of numbers")
                continue
            }

            // check input for variable assignment
            var regex = """(?<name>[a-zA-Z]+)\s*=\s*(?<value>\d+)""".toRegex()
            if (input.matches(regex)) {
                val match = regex.find(input)
                if (match != null) match.groups.get("name")
                    ?.let { assignVariable(it.value, match.groups.get("value")!!.value.toInt()) }
            }

            // check input for variable to variable assignment
            //TODO("CHECK FOR VARIABLE TO VARIABLE ASSIGNMENT")

            //TODO("CHECK FOR VARIABLE CALCULATIONS")

 //           try {
                // split equation string into operands and integers
                var (operatorList: List<String>, numberList: List<String>) = input.split(" ")
                    .partition { """[\+|\-]+""".toRegex().matches(it) }

                val mutableOperatorList = operatorList.toMutableList()

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

//            } catch (e: Exception) {
//                println("Invalid expression")
//                continue
//            }
            println(performCalculation(operatorList, numberList))
        }
    }

    println("Bye!")
}
