package com.example.leon.kotlinapplication

/**
 * Created by Leon on 14.06.17.
 */
class moneyParser(money: Int) {
    var money: Int = money

    fun parse(): String {
        var shortMoney: Int = 0
        if (money > 1000000) {
            shortMoney = money / 1000000
            return "$shortMoney MIL."
        }
        if (money > 1000) {
            shortMoney = money / 1000
            return "$shortMoney MIL."
        }
        if (money == 0) {
            return "-"
        }
        return "-"
    }

}