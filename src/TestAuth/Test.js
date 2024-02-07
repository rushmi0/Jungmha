import {Buffer} from "buffer";
import AES from "../AES.js";


const aes = AES()

let sharedKey = Buffer.from("3e11810c67157bf584db16bbd85d9e9b339b4469e27390365195379cb2168a78", 'hex');

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

