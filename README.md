
<div align="center">
  <span><img src="src/main/resources/images/diagram/Logo.svg" height=512 width=512 /></span>
</div>



## Register Account

การลงทะเบียนนั้นจำเป็นต้องสร้างช่องการสื่อสารที่มีความปลอดภัยก่อนที่จะส่งข้อมูลที่เป็นความลับ เช่นข้อมูล่วนตัว ข้อความ Chat อื่นๆ
แน่นอนว่ามีการใช้ Cryptography เป็นเครื่องมือสำคัญ ผมจะอธิบายว่าผมใช้อะไรบ้างมีลิงค์ให้กดไปอ่านรายละเอียดต่อได้ พร้อมกับรูปปลากรอบ เห้ยย! รูปประกอบ ที่ผมทำไว้ เอาล่ะมาเริ่มกันเลยยย


### Elliptic Curve
ผมใช้ [Elliptic Curve](https://cryptobook.nakov.com/asymmetric-key-ciphers/elliptic-curve-cryptography-ecc) และเส้นโค้ง [Secp256k1](https://www.secg.org/sec2-v2.pdf) ในการสร้าง **Public Key** เป็นรูปแบบบีบอัดขนาด 33 Bytes 

> _Private Key นั้นสร้างโดยนำ Password มา Hash ด้วย Sha256 ทั้งหมด 7,200 รอบ เพื่อถ่วงเวลาพวกที่จะมา brute force_

### ECDH Share Key
ต่อไปใช้ [ECDH Key Exchange](https://cryptobook.nakov.com/asymmetric-key-ciphers/ecdh-key-exchange), ระหว่าง Client และ Server จะส่ง Public Key ของตัวเองให้ฝั่งตรงข้าม เพื่อสร้าง **Private Key** ดอกใหม่ขึ้นมา และกุญแจดอกนี้ใช้ในการ Encrypted ข้อมูลความลับต่อจากนี้ 

### ChaCha20
สุดท้ายผมใช้ [ChaCha20](https://github.com/nostr-protocol/nips/blob/master/44.md) ในการ Encrypted ข้อมูลต่างๆ โดยใช้ Private Key ที่ได้จากกระบวนการทำ ECDH

วิธีการเหล่านี้ได้แรงบันดาลใจมาจาก Nostr Protocol ในส่วน Encrypted Direct Message

<div align="center">
  <span><img src="src/main/resources/images/diagram/register.svg" height=1024 width=1024 /></span>
</div>


