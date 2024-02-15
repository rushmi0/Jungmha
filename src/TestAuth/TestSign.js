import EllipticCurve from "../../utils/SecureKey.js";


const ec = EllipticCurve();
// ++++++++++++++++++++++++++++++++++++++++++++++++++++ \\
// `Account 1`
const username = "Aura"
// # Private Key : e143924567f4a20128f4b3457d97145a7e1922864e9a1cf47d7efff8b7d85979
// # Public Key : 02f06f2580404d439896f002a6b77cbbc518ace934345c69b831a234a6dfbe54c

const privateKey = "e143924567f4a20128f4b3457d97145a7e1922864e9a1cf47d7efff8b7d85979"

const publicKey = ec.generateKeyPair(privateKey)
console.log(publicKey)

const url2 = `/auth/sign-in/${username}`;

const signature = ec.signMessage(url2, privateKey);
console.log(signature)
