package com.example.domesticviolencealert

object Utils {
    fun loadSuspects(): ArrayList<Suspect> {
        val phones = arrayOf(
            "0000000000",
            "1111111111",
            "2222222222"
        )

        val emails = arrayOf(
            "example@email.com",
            "exampleTest@email.com",
            "exampleTestTest@email.com"
        )

        val names = arrayOf(
            "Suspect 1",
            "Suspect 2",
            "Suspect 3"
        )

        val report1 = ArrayList<Report>()
        report1.add(Report("Suspect 1 -> Report 1"))
        report1.add(Report("Suspect 1 -> Report 2"))
        report1.add(Report("Suspect 1 -> Report 3"))

        val report2 = ArrayList<Report>()
        report2.add(Report("Suspect 2 -> Report 1"))
        report2.add(Report("Suspect 2 -> Report 2"))
        report2.add(Report("Suspect 2 -> Report 3"))

        val report3 = ArrayList<Report>()
        report3.add(Report("Suspect 3 -> Report 1"))
        report3.add(Report("Suspect 3 -> Report 2"))
        report3.add(Report("Suspect 3 -> Report 3"))

        val reports = arrayOf (
            report1,
            report2,
            report3
        )

        val suspects = ArrayList<Suspect>()
        for (i in phones.indices) {
            val suspect = Suspect(phones[i], emails[i], names[i], reports[i])
            suspects.add(suspect)
        }

        return suspects
    }
}