package de.pbauerochse.worklogviewer.datasource.dummy

import de.pbauerochse.worklogviewer.timereport.Tag

object DummyNames {

    val projects = listOf(
        "CNP", "STUFF", "NON_WORK", "APP", "KFI", "MEET", "VID",
        "TWT", "STNDUP", "CRWLR", "JNR", "CNFR", "RFCTR", "RNDM"
    )

    val issues = listOf(
        "That new cool feature", "Implementing a thing into the other thing", "A very strange bug",
        "Investigating", "Fixing that thing", "Meetings", "Migrating data from excel sheet",
        "Video conference with that client", "After work beer", "Slacking off", "Finding Nemo",
        "Get him to the greek", "IMPORTANT LAST MINUTE FEATURE!!", "Counting money", "Making profit",
        "Calming customer", "Working really hard", "Working really hard faster", "Being awesome",
        "Saving the planet", "Putting together hardware", "Onboarding new co-worker", "Merge conflicts",
        "Build problems", "Rage quitting", "Planning company party", "Telephone conference", "Cleaning up",
        "Writing stuff on post-its", "Talking to the new hot co-worker", "Watering plants", "Calculating last digit of Pi",
        "Looking busy", "Evaluate that thing", "Refactor the whole darn thing"
    )

    val firstNames = listOf(
        "Patrick", "Peter", "Markus", "Martin", "Angela", "Penelope", "Dagobert", "Juan", "José",
        "Alfredo", "Dennis", "Daniel", "Ali", "Ahmed", "Ibrahim", "Sara", "Sarah", "Isabel", "María",
        "Mariam", "Nicolas", "Noah", "Liam", "Felix", "Luis", "Mateo", "David", "Richard", "Francisco",
        "Oliver", "Sebastian", "Diego", "Mats", "Leo", "Lucía", "Martina", "Catalina", "Emilia", "Zoe",
        "Chloe", "Emma", "Charlotte", "Alice", "Bob", "Elizabeth", "Linda", "Elsa", "Victoria", "Valeria",
        "George", "Jack", "Robert", "Elias", "Lina", "Mia", "Lara", "Jana", "Susanne", "Suzana", "Nora",
        "Ella"
    )

    val lastNames = listOf(
        "Watson", "Holmes", "Jolie", "Pitt", "da Vito", "di Caprio", "Duck", "Rabbit", "Parker",
        "Wayne", "Travis", "Vermeer", "Fowler", "Keppler", "Norton", "Rickman", "De Niro", "Bale",
        "Waltz", "Freeman", "Williams", "Pacino", "Hernandez", "Svensson", "Murray", "Stewart",
        "Newman", "Franco", "Dominguez", "Chao", "Pesci", "Malkovich"
    )

    val fields = listOf(
        ProjectField("Type", listOf("Issue", "Bug", "Feature Request", "Nonsense", "Question", "Investigation", "Evaluation", "Meeting")),
        ProjectField("Customer", listOf(
            "Evil Corp", "The Umbrella Corp", "Pearson Specter", "Pawtucket Brewery", "Multi National United", "Lunar Industries", "Acme Corporation", "Initech",
            "Dunder Mifflin", "Nakatomi Trading Co.", "Stark Industries", "Macmillan Toys", "Cyberdyne Systems", "Wonka's", "Monsters Inc.", "Wayne Enterprises"
        )),
        ProjectField("Component", listOf("Migration", "Import", "Export", "Interface", "App", "Communication")),
        ProjectField("Difficulty", listOf("Easy peasy", "Easy", "Quite alright", "Medium", "Not too easy", "Hard", "Tough", "Oh my gosh!", "Please help me!")),
        ProjectField("Costs", listOf("Thrift shop", "Cheap", "Affordable", "Moderate", "Tad expensive", "Rip off", "Muhahaha", "2nd mortgage")),
        ProjectField("Deadline", listOf("yesterday", "ASAP", "Now!", "Take your time", "Better work overtime"))
    )

    val tags = listOf(
        Tag("Important", backgroundColor = "ff0000"),
        Tag("Hotfix", backgroundColor = "ff0000"),
        Tag("Feeback required", backgroundColor = "ddddff", foregroundColor = "ffffff"),
        Tag("Easy", backgroundColor = "ddffdd", foregroundColor = "000000"),
        Tag("Needs more details", backgroundColor = "ddffff", foregroundColor = "ff0000"),
        Tag("Ugly UI", backgroundColor = "00FF00", foregroundColor = "0000FF"),
        Tag("Refactoring", backgroundColor = "0000ff"),
        Tag("A very very long tag name", backgroundColor = "dd00dd"),
        Tag("DB Migration", backgroundColor = "bbbbbb", foregroundColor = "ffffff")
    )

    data class ProjectField(
        val name : String,
        val possibleValues : List<String>
    )

}