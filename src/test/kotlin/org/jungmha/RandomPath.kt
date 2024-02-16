package org.jungmha

fun main() {

    val img = listOf(
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
    );

    // สร้างรายการชื่อ
    val names = listOf("sakura_lover123", "yumi_chan22", "misaki_kawaii88", "aiko_garden99", "emiko_starlight77")

    for (i in 0 .. 10) {

        // สุ่มค่าดัชนี
        val randomIndex = img.indices.random()

        // ดึงชื่อจากรายการโดยใช้ดัชนีที่สุ่ม
        val randomName = img[randomIndex]

        // พิมพ์ชื่อที่สุ่ม
        println("ชื่อที่สุ่ม: $randomName")

    }


}