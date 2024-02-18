package org.jungmha

import kotlin.random.Random

data class ImageData(val fileName: String)

fun main() {


    for (i in 0 .. 20) {
        val randomImageData = getRandomImageData()
        println(randomImageData)
    }
}

fun getRandomImageData(): String {

    val imageFileNames = listOf(
        "101609001.png",
        "103289198.png",
        "103378116.png",
        "11540109.png",
        "125073706.png",
        "127224899.png",
        "129670421.png",
        "138510874.png",
        "140230018.png",
        "2028222.png",
        "29009428.png",
        "32309979.png",
        "38359198.png",
        "48699240.png",
        "52341427.png",
        "58748765.png",
        "62823080.png",
        "7670721.png",
        "7713860.png",
        "78198120.png",
        "79818344.png",
        "8560251.png",
        "9362787.png",
        "94222565.png",
        "96358978.png"
    )

    val randomIndex = Random.nextInt(imageFileNames.size)
    val randomFileName = imageFileNames[randomIndex]
    return ImageData(randomFileName).fileName
}
