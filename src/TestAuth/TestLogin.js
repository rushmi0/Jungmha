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


const signature = ec.signMessage(url2, privateKey);


console.log("signature: ", signature)


//4f54eb2dd0b2fd4634a2aa3e3fcb955ae5e09120540e55ffefd0b0aa3977dad4
//private: 4f54eb2dd0b2fd4634a2aa3e3fcb955ae5e09120540e55ffefd0b0aa3977dad4 --Login


// 0375b08364614d438ce07652ac22937268b58a9d2006cc9fa84b0998c09c6c85ba
// Public Key:  0375b08364614d438ce07652ac22937268b58a9d2006cc9fa84b0998c09c6c85ba

// a59cdfbd1017eae93e5fbfa38af9f380c7155e0f3c98c772711592a3a706b14e
//Private key a59cdfbd1017eae93e5fbfa38af9f380c7155e0f3c98c772711592a3a706b14e --SignUp
// UserSignUp.jsx:129 Public Key length:  66
// UserSignUp.jsx:130 Public Key:  0375b08364614d438ce07652ac22937268b58a9d2006cc9fa84b0998c09c6c85ba
// UserSignUp.jsx:131 Username HelloWorld99
// UserSignUp.jsx:151 Server PublicKey:  0347d5cb133e59866bd1adf84adc291bf00fb05e03fb5355deda66e91815e320a8
// UserSignUp.jsx:152 Encrypt Data:  9PjMCKaXQEt3SMF69jCQN2EAOT5k5nOnxFo/y0YRcsAMgbIdWqZvIqOyxURDSCzCb8ORs7QXI1HcqtBGpN8TkOK0cfSHKtT9sK3YWB83oy1dc3q/weEWVtwpbPLQyVl7qFnfzIkiBBLqiFp8uCh5rQD4QDrjM9+t5VGIso8eGu9qRzYGvgA=?iv=AAAAAAAAAAAAAAAA
// UserSignUp.jsx:96 share key:  cc56a1d3bac0885244c59e98a2b73a80508ee4f4bc40b51f53b4211393746e34
// UserSignUp.jsx:97 share key length:  32
// UserSignUp.jsx:99 {firstName: 'HelloWorld99', lastName: 'HelloWorld99', email: 'HelloWorld99@gmail.com', phoneNumber: '0642152232', userType: 'Normal'}
// UserSignUp.jsx:102 Encrypted data: 9PjMCKaXQEt3SMF69jCQN2EAOT5k5nOnxFs/y0YRcsAMgbIdWqZvIqOyxURDSCzCb8ORsrQXI1HcqtBGpN8TkOK0cfSHKtT9sK3ZWB83oy1dc3q/weEWVtwpbPLQyVl7qFnfzIkiBBLqiFp8uCh5rQD4QDrjM9+t5VGIso8eGu9qRzYGvgA=?iv=AAAAAAAAAAAAAAAA
// UserSignUp.jsx:112 data to send:  {content: '9PjMCKaXQEt3SMF69jCQN2EAOT5k5nOnxFo/y0YRcsAMgbIdWq…4QDrjM9+t5VGIso8eGu9qRzYGvgA=?iv=AAAAAAAAAAAAAAAA'}
// UserSignUp.jsx:117 User Info:  {serverPublicKey: {…}, token: {…}}

