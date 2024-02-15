import EllipticCurve from "../../utils/SecureKey.js";

const ec = EllipticCurve();

// 491a613f1026d8552e3daea8928300af4d2868ecd8e8c6172de7c522e8edb7c1
// c70d1d5b8b000e2f525a618ec0c603e86465321c5edee3463469ff3a353e047
const priKey = "936b3cb61a1a936e2b96449d2415f3b984f8ea2bf9bb83f7c03ccf998aecfc57"; //pubkey
const srvKey = "0347d5cb133e59866bd1adf84adc291bf00fb05e03fb5355deda66e91815e320a8"; //Server
const sharedKey = ec.calculateSharedKey(
    priKey,
    srvKey
);

console.log("sharedKey: ", sharedKey.toString("hex"));

//shared key: 491a613f1026d8552e3daea8928300af4d2868ecd8e8c6172de7c522e8edb7c1