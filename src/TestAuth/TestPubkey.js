
import EllipticCurve from "../SecureKey.js";

const ec = EllipticCurve();
// ++++++++++++++++++++++++++++++++++++++++++++++++++++ \\
// `Account 1`
const username = "root"
const pass = "lnw1150"
const privateKey = ec.genPrivateKey(pass)
console.log(privateKey)

const publicKey = ec.generateKeyPair(privateKey)
console.log(publicKey)


