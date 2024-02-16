package org.jungmha

fun main() {
    /*
    -rw-r--r--. 1 milko milko 1569 Feb 16 16:42 101609001.png
-rw-r--r--. 1 milko milko 1552 Feb 16 16:42 103289198.png
-rw-r--r--. 1 milko milko 1567 Feb 16 16:38 103378116.png
-rw-r--r--. 1 milko milko 1539 Feb 16 16:41 11540109.png
-rw-r--r--. 1 milko milko 1562 Feb 16 16:39 125073706.png
-rw-r--r--. 1 milko milko 1555 Feb 16 16:38 127224899.png
-rw-r--r--. 1 milko milko 1532 Feb 16 16:38 129670421.png
-rw-r--r--. 1 milko milko 1521 Feb 16 16:40 138510874.png
-rw-r--r--. 1 milko milko 1566 Feb 16 16:39 140230018.png
-rw-r--r--. 1 milko milko 1537 Feb 16 16:38 2028222.png
-rw-r--r--. 1 milko milko 1537 Feb 16 16:37 29009428.png
-rw-r--r--. 1 milko milko 1587 Feb 16 16:41 32309979.png
-rw-r--r--. 1 milko milko 1554 Feb 16 16:39 38359198.png
-rw-r--r--. 1 milko milko 1549 Feb 16 16:42 48699240.png
-rw-r--r--. 1 milko milko 1547 Feb 16 16:42 52341427.png
-rw-r--r--. 1 milko milko 1554 Feb 16 16:42 58748765.png
-rw-r--r--. 1 milko milko 1544 Feb 16 16:41 62823080.png
-rw-r--r--. 1 milko milko 1551 Feb 16 16:37 7670721.png
-rw-r--r--. 1 milko milko 1573 Feb 16 16:39 7713860.png
-rw-r--r--. 1 milko milko 1533 Feb 16 16:39 78198120.png
-rw-r--r--. 1 milko milko 1522 Feb 16 16:39 79818344.png
-rw-r--r--. 1 milko milko 1557 Feb 16 16:38 8560251.png
-rw-r--r--. 1 milko milko 1556 Feb 16 16:39 9362787.png
-rw-r--r--. 1 milko milko 1573 Feb 16 16:39 94222565.png
-rw-r--r--. 1 milko milko 1516 Feb 16 16:39 96358978.png

     */

    // สร้างรายการชื่อ
    val names = listOf("sakura_lover123", "yumi_chan22", "misaki_kawaii88", "aiko_garden99", "emiko_starlight77")

    for (i in 0 .. 10) {

        // สุ่มค่าดัชนี
        val randomIndex = names.indices.random()

        // ดึงชื่อจากรายการโดยใช้ดัชนีที่สุ่ม
        val randomName = names[randomIndex]

        // พิมพ์ชื่อที่สุ่ม
        println("ชื่อที่สุ่ม: $randomName")

    }


}