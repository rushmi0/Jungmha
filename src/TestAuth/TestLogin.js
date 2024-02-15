import EllipticCurve from "../../utils/SecureKey.js";
import sha256 from "../../utils/Sha256.js";
import axios from "axios";


const ec = EllipticCurve();
// ++++++++++++++++++++++++++++++++++++++++++++++++++++ \\'2c36303cd590b30acf42e01f857973c4b81ee336ca2bd19b73b1e39ee4800d6b'
// `Account 1`
const username = "HelloWorld99"
const pass = "#AR2545ssaa"

const privateKey = ec.genPrivateKey(pass)
console.log(privateKey)

const publicKey = ec.generateKeyPair(privateKey)
console.log(publicKey)
// ++++++++++++++++++++++++++++++++++++++++++++++++++++ \\ 037678a280c054e2371c23ba16b4a9bba6b0194f3a405f0743ba45cce91732a8cb

const url = `http://localhost:8080/api/v1/auth/sign-in/${username}`;

const url2 = `/auth/sign-in/${username}`;
console.log("Unix Time:", typeof Date.now());
const signature = ec.signMessage(url2, privateKey);


console.log("signature: ", signature)


//sign1 30460220ad318419e25b91b374130805d26f710809c4066e9f681c47221721464c2d068e02202fb3692a1d3d4b2fa056816cffb80ad2e09f7598976ca38952a447d00cfb82690000
//sign2
