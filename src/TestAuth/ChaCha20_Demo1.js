import ChaCha20 from "../../utils/ChaCha20.js";
import EllipticCurve from "../../utils/SecureKey.js";


const chacha = ChaCha20()

const ec = EllipticCurve();
// ++++++++++++++++++++++++++++++++++++++++++++++++++++ \\
// `Account 1`
const username = "root"
const pass = "lnw1150"

const privateKey = ec.genPrivateKey(pass)
console.log(privateKey)

const publicKey = ec.generateKeyPair(privateKey)
console.log(publicKey)

// b4c1cf8b26771a30d8373bc5b114407f1c7fdd19d2dc2721a725578c7e623de8
// 03009b6e1b28d281e69a4cd6bfafe61bcfeb4bb707dec05d39f2eda07ce0d97125
/*

### User root
# b4c1cf8b26771a30d8373bc5b114407f1c7fdd19d2dc2721a725578c7e623de8
# 03009b6e1b28d281e69a4cd6bfafe61bcfeb4bb707dec05d39f2eda07ce0d97125
POST http://localhost:8080/api/v1/auth/open-channel
Content-Type: application/json

{
  "userName" : "root",
  "authenKey" : "03009b6e1b28d281e69a4cd6bfafe61bcfeb4bb707dec05d39f2eda07ce0d97125"
}

*/

/*

PUT http://localhost:8080/api/v1/auth/sign-up
Content-Type: application/json
UserName: Aura

{
  "content" : ""
}
 */


let sharedKey = "3e11810c67157bf584db16bbd85d9e9b339b4469e27390365195379cb2168a78";

let data = {
    "firstName": "สมหมาย",
    "lastName": "ใจหมา",
    "email": "sample1@gmail.com",
    "phoneNumber": "0987654321",
    "userType": "Normal"
}

const jsonString = JSON.stringify(data);
console.log(data);

let dataToSend = chacha.encrypt(jsonString, sharedKey)
console.log('Encrypted data:', dataToSend);

let decryptedData = chacha.decrypt(dataToSend, sharedKey);
console.log('Decrypted data:', decryptedData);