package com.example.archive_sfc.utils.hash

fun generateUniqueHashes(numHashes: Int, length: Int): List<String> {
    val hashes = HashSet<String>()
    val generatedHashes = mutableListOf<String>()

    while (generatedHashes.size < numHashes) {
        val randomHash = generateRandomHash(length)
        if (hashes.add(randomHash)) {
            generatedHashes.add(randomHash)
        }
    }

    return generatedHashes
}

fun generateRandomHash(length: Int): String {
    val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}