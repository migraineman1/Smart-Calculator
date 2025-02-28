package calculator

fun main() {
    var input = ""
    var result = 0
    while (input != "/exit") {
        input = readln() ?: continue
        if (input != "/exit" && !(input.isNullOrBlank())) {
            if (input == "/help") {
                println("The program calculates the sum and difference of numbers")
            } else {
                try {
                    var (operatorList: List<String>, numberList: List<String>) = input.split(" ")
                        .partition { """[\+|\-]+""".toRegex().matches(it) }

                    val mutableOperatorList = operatorList.toMutableList()
                    // check for multiple minus or plus signs
                    for (index in operatorList.indices) {
                        if (operatorList[index].contains('-'))
                            if (operatorList[index].length % 2 == 0) mutableOperatorList[index] = "+"
                            else mutableOperatorList[index] = "-"
                        if (operatorList[index].contains('+')) mutableOperatorList[index] = "+"
                    }
                    if (operatorList.size != numberList.size) {
                        mutableOperatorList.add(0, "+")
                        operatorList = mutableOperatorList.toList()
                    }
                    val integerList = numberList.map { it.toInt() }
                    result = integerList.reduceIndexed { index, acc, i ->
                        when (operatorList[index]) {
                            "+" -> acc + i
                            "-" -> acc - i
                            else -> acc
                        }
                    }
                } catch (e: NumberFormatException) {
                    continue
                }
                println(result)
            }
        }
    }
    println("Bye!")
}
