import EllipticCurve from "../../utils/SecureKey.js";
import AES from "../../utils/AES.js";


const aes = AES()
const ec = EllipticCurve();
// ++++++++++++++++++++++++++++++++++++++++++++++++++++ \\
// `Account 1`
const username = "root"
const pass = "lnw1150"
const privateKey = ec.genPrivateKey(pass)
console.log(privateKey)

const publicKey = ec.generateKeyPair(privateKey)
console.log(publicKey)



const serverPublicKey = Buffer.from("0347d5cb133e59866bd1adf84adc291bf00fb05e03fb5355deda66e91815e320a8", 'hex');

// คำนวณ Shared Key
const sharedKey = ec.calculateSharedKey(
    publicKey,
    serverPublicKey
);


//let sharedKey = Buffer.from("3e11810c67157bf584db16bbd85d9e9b339b4469e27390365195379cb2168a78", 'hex');

let data = {
    "firstName": "สมหมาย",
    "lastName": "ใจหมา",
    "email": "sample1@gmail.com",
    "phoneNumber": "0987654321",
    "userType": "Normal"
}

const jsonString = JSON.stringify(data);
console.log(data);

let dataToSend = aes.encrypt(jsonString, sharedKey);
console.log('Encrypted data:', dataToSend);

let decryptedData = aes.decrypt(dataToSend, sharedKey);
console.log('Decrypted data:', decryptedData);


// ถ้าถึงข้อมูล
let firstName = decryptedData.firstName;
console.log('ชื่อ :', firstName);

let lastName = decryptedData.lastName;
console.log('นามสกุล :', lastName);