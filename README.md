<div align="center">
  <span><img src="src/main/resources/images/diagram/Logo.svg" height=200 width=512 /></span>
</div>

## ขั้นตอนการติดตั้งและใช้งาน

เริ่มต้นด้วยการคัดลอกโปรเจ็กต์จาก GitHub และเข้าไปยังไดเร็กทอรีของโปรเจ็กต์ที่คุณได้คัดลอก
```shell
git clone https://github.com/rushmi0/Jungmha.git
cd Jungmha
```

## ติดตั้ง GraalVM Community Edition สำหรับ JDK17
1. ดาวน์โหลด GraalVM จากลิงก์นี้: [GraalVM CE Builds](https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-17.0.9)
2. ติดตั้ง GraalVM ตามขั้นตอนการติดตั้งที่เหมาะสมกับระบบปฏิบัติการที่ใช้


### กำหนดตัวแปรสภาพแวดล้อม
กำหนดตัวแปร JAVA_HOME และปรับ PATH เพื่อให้ระบบรู้ถึงที่ติดตั้งของ GraalVM

```shell
export JAVA_HOME=/home/$(whoami)/to/path/graalvm-ce-17.0.9
export PATH=$JAVA_HOME/bin:$PATH
source ~/.zshrc  # หรือใช้ .bashrc หากใช้ bash
```

### คำสั่งในการใช้ GraalVM ในการคอมไพล์ไปเป็น native binaries
การใช้ GraalVM เพื่อคอมไพล์โปรแกรมเป็นไฟล์ native binaries ทำให้โปรแกรมสามารถทำงานได้โดยไม่ต้องใช้ Java Virtual Machine (JVM) อีกต่อไป นี่คือคำสั่งใน Gradle เพื่อใช้งาน GraalVM ในการคอมไพล์โปรแกรม

คำสั่งนี้จะใช้ GraalVM เพื่อคอมไพล์โปรแกรมไปเป็น native binaries โดยมีการคอมไพล์เบื้องต้น
```shell
./gradlew nativeCompile 
```
คำสั่งนี้จะใช้ GraalVM เพื่อคอมไพล์โปรแกรมไปเป็น native binaries โดยมีการปรับปรุงเพิ่มเติมในกระบวนการคอมไพล์เพื่อให้ได้ผลที่เร็วและมีประสิทธิภาพมากที่สุดที่เป็นไปได้
```shell
./gradlew nativeOptimizedCompile 
```

<br>

## การใช้งานสำหรับ Docker

### 🔧 ⚙️ Just-in-time (JIT) compilation
ใช้วิธีการแปลงโค้ดไปเป็น bytes code ก่อน แล้วนำไปรันบน Java Virtual Machine (JVM) ดังนี้
```shell
docker compose up -d jungmhaDB jungmha-jvm-app
```

### 🔧 ⚙️  Ahead-of-time (AOT) compilation
ใช้วิธีการแปลงโค้ดไปเป็น native binaries ของแพลตฟอร์มนั้นๆ แล้วรันได้โดยตรง ดังนี้
```shell
docker compose up -d jungmhaDB jungmha-native-image
```

<br>

### เอกสารการใช้งาน API
- https://jungmha.rushmi0.win/swagger-ui
- https://jungmha.rushmi0.win/redoc

___
<br>

# Backend ผมออกแบบ โดยแบ่งออกเป็นส่วนการทำงาน 4ชั้น

#### ชั้นที่ 1 REST API
- ✨  ส่วนของการจัดส่งข้อมูล ที่จะให้บริการต่างๆแก่ Frontend

#### ชั้นที่ 2 Business Logic
- ✨  สำหรับการทำงานของ Method หรือ ส่วนการคำนวณต่างๆ

#### ชั้นที่ 3 Connect and Execute SQL statement
- ✨  ในส่วนนี้จะเป็นการเชื่อมกับฐานข้อมูลและดำเนินการ SQL และ จัดเตรียมรูปแบบข้อมูล ที่จะส่งให้ ขั้นที่ 2 ไปคำนวณต่อ

#### ชั้นที่ 4 Database
- ✨  ฐานข้อมูลนี้คือก้อนของข้อมูล ที่มีการเก็บข้อมูลต่างๆไว้ และในส่วนนนี้ยังมีการ เขียนโปรแกรมทำอะไรบ้างอย่างกับข้อมูล เพื่อให้ฐานข้อมูลฉลาดขึ้น จะได้ลดภาระ หรือลดโค้ดในชั้นที่ 2 และ 3

## Tech Stack

### Backend
<div>
	<table>
		<tr>
			<td><img width="64" src="src/main/resources/images/diagram/icons/idea.svg" alt="IntelliJ" title="IntelliJ"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/184146221-671413cb-b1ae-47db-a232-b37c99281516.png" alt="SonarQube" title="SonarQube"/></td>
			<td><img width="64" src="https://raw.githubusercontent.com/rushmi0/Jungmha/backend/src/main/resources/images/diagram/icons/qodana.jpeg" alt="SonarQube" title="SonarQube"/></td>
			<td><img width="80" src="https://raw.githubusercontent.com/rushmi0/Jungmha/backend/src/main/resources/images/diagram/icons/micronaut.jpg" alt="Micronaut" title="Micronaut"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/183017085-067f30b6-1032-4f89-adc4-ba917d6d0f3a.png" alt="GraalVM" title="GraalVM"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/185062810-7ee0c3d2-17f2-4a98-9d8a-a9576947692b.png" alt="Kotlin" title="Kotlin"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/117208740-bfb78400-adf5-11eb-97bb-09072b6bedfc.png" alt="PostgreSQL" title="PostgreSQL"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/117207330-263ba280-adf4-11eb-9b97-0ac5b40bc3be.png" alt="Docker" title="Docker"/></td>
			<td><img width="64" src="https://raw.githubusercontent.com/rushmi0/Jungmha/backend/src/main/resources/images/diagram/icons/cloudflare.png" alt="fedora" title="cloudflare"/></td>
        </tr>
	</table>
</div>

### Frontend
<div>
	<table>
		<tr>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/192108893-b1eed3c7-b2c4-4e1c-9e9f-c7e83637b33d.png" alt="WebStorm" title="WebStorm"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/192108891-d86b6220-e232-423a-bf5f-90903e6887c3.png" alt="Visual Studio Code" title="Visual Studio Code"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/202896760-337261ed-ee92-4979-84c4-d4b829c7355d.png" alt="Tailwind CSS" title="Tailwind CSS"/></td>
			<td><img width="64" src="https://daisyui.com/images/daisyui-logo/daisyui-logomark.svg" alt="Tailwind CSS" title="Daisy UI"/></td>
            <td><img width="64" src="https://user-images.githubusercontent.com/25181517/117447155-6a868a00-af3d-11eb-9cfe-245df15c9f3f.png" alt="JavaScript" title="JavaScript"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/183897015-94a058a6-b86e-4e42-a37f-bf92061753e5.png" alt="React" title="React"/></td>
			<td><img width="64" src="https://user-images.githubusercontent.com/25181517/183568594-85e280a7-0d7e-4d1a-9028-c8c2209e073c.png" alt="Node.js" title="Node.js"/></td>
			<td><img width="64" src="https://github-production-user-asset-6210df.s3.amazonaws.com/62091613/261395532-b40892ef-efb8-4b0e-a6b5-d1cfc2f3fc35.png" alt="Vite" title="Vite"/></td>
        </tr>
	</table>
</div>

<br/>


## Sequence Diagram

ระบบลงทะเบียนและเข้าสู่ระบบ

### Account Register

เพื่อความปลอดภัยของข้อมูลที่ส่งไปยังเซิร์ฟเวอร์ การลงทะเบียนจำเป็นต้องมีการสร้างช่องทางสื่อสารที่ปลอดภัย โดยเฉพาะเมื่อข้อมูลที่ส่งมีความลับ เช่น ข้อมูลส่วนตัว หรือข้อความแชท ดังนั้น การใช้ Cryptography เป็นสิ่งสำคัญอย่างยิ่ง


#### Elliptic Curve
ใช้ [Elliptic Curve](https://cryptobook.nakov.com/asymmetric-key-ciphers/elliptic-curve-cryptography-ecc) และเส้นโค้ง [Secp256k1](https://www.secg.org/sec2-v2.pdf) ในการสร้าง **Public Key** เป็นรูปแบบบีบอัดขนาด 33 Bytes

> _Private Key นั้นสร้างโดยนำ Password มา Hash ด้วย Sha256 ทั้งหมด 7,200 รอบ เพื่อถ่วงเวลาพวกที่จะมา brute force_

#### ECDH Share Key
ต่อไปใช้ [ECDH Key Exchange](https://cryptobook.nakov.com/asymmetric-key-ciphers/ecdh-key-exchange) เพื่อสร้าง Private Key ใหม่ โดยในกระบวนการนี้ Client และ Server จะแลกเปลี่ยน Public Key เพื่อสร้างกุญแจที่ใช้ในการเข้ารหัสข้อมูล

#### ChaCha20
สุดท้ายใช้ [ChaCha20](https://cryptobook.nakov.com/symmetric-key-ciphers/chacha20-poly1305) เพื่อเข้ารหัสข้อมูล โดยใช้ Private Key ที่ได้จากขั้นตอน ECDH

<div align="center">
  <span><img src="src/main/resources/images/diagram/register.svg" height=512 width=1024 /></span>
</div>

### Log in

การเข้าสู่ระบบเพื่อเข้าใช้งานนั้น Client ต้องสร้างลายเซ็นดิจิตอล และส่งไปให้ Server เมื่อทาง Server ตรวจสอบเลยเซ็นแล้วพบว่าถูกต้อง จากนั้นจะส่ง Access token กลับไปให้ Client


##### วิธีการทำงาน

#### ECDSA
นำข้อความที่กำหนดมา Hash ด้วย Sha256 เพียงครั้งเดียว แล้วนำ Private Key มาใช้สร้างลายเซ็นแบบ ECDSA แล้วส่งไปให้ Server เพื่อรับ Access token

> ลายเซ็นที่สร้างขึ้นใช้งานได้ครั่งเดียวเท่านั้น ไม่สามารถใช้ซ้ำได้


<div align="center">
  <span><img src="src/main/resources/images/diagram/login.svg" height=512 width=1024 /></span>
</div>


