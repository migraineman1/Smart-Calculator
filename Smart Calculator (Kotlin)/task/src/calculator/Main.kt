package calculator

fun main() {
    var input = ""
    var integerList = mutableListOf<Int>()
    while (input != "/exit") {
         input = readln() ?: continue
            if (input != "/exit") {
                if (input == "/help") {
                    println("The program calculates the sum of numbers")
                } else {
                try {
                    integerList.clear()
                    integerList.addAll(input.split(" ").map { it.toInt() })
                } catch (e: NumberFormatException) {
                    continue
                }
                println(integerList.sum())
            }
        }
    }
    println("Bye!")
}
