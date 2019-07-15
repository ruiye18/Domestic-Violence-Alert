package com.example.domesticviolencealert

object Utils {
    fun loadSuspects() : ArrayList<Suspect> {
        val phones = arrayOf (
            "0000000000",
            "1111111111",
            "2222222222"
        )

        val emails = arrayOf (
            "example@email.com",
            "exampleTest@email.com",
            "exampleTestTest@email.com"
        )

        val names = arrayOf (
            "Suspect 1",
            "Suspect 2",
            "Suspect 3"
        )

        val suspects = ArrayList<Suspect>()
        for (i in phones.indices) {
            val suspect = Suspect(phones[i], emails[i], names[i])
            suspects.add(suspect)
        }

        return suspects
    }
}