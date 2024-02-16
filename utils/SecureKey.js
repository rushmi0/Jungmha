import elliptic from 'elliptic';
import sha256 from "./Sha256.js";
import { Buffer } from 'buffer';
import shiftTo from "./ShiftTo.js";

const EllipticCurve = () => {

    // สร้างวัตถุ ec สำหรับใช้กับ elliptic curve ที่กำหนดเอง (secp256k1)
    const ec = new elliptic.ec('secp256k1');
    const random = shiftTo()
    const LIMIT = 7200;

    const genPrivateKey = (pass) => {

        let encodedValue = Buffer.from(pass.toString());

        for (let i = 0; i < LIMIT; i++) {
            const hashResult = sha256.hash(encodedValue);
            encodedValue = Buffer.from(hashResult, 'hex');
        }

        return encodedValue.toString('hex');
    }


    const generateKeyPair = (privateKey) => {
        let keyPair = ec.keyFromPrivate(privateKey);
        return keyPair.getPublic().encodeCompressed("hex");
    }

    const signMessage = (msg, privateKey) => {
        // หา hash ของข้อความ
        const msgHash = sha256.hash(msg);

        const nonce = random.randomBytes(32);

        // เซ็นข้อความและคืนลายเซ็น
        const signature = ec.sign(msgHash, privateKey, 'hex', {
            nonce: nonce,
            canonical: true
        });
        return derEncode(signature);
    };

    const recoverPublicKey = (msgHash, signature) => {
        const hexToDecimal = (x) => ec.keyFromPrivate(x, 'hex').getPrivate().toString(10);
        return ec.recoverPubKey(hexToDecimal(msgHash), signature, signature.recoveryParam, 'hex');
    };

    const verifySignature = (pubKey, msgHash, signature) => {
        return ec.verify(msgHash, signature, pubKey);
    };


    const derEncode = (signature) => {

        const r = signature.r.toArrayLike(Buffer, 'be', 32);
        const s = signature.s.toArrayLike(Buffer, 'be', 32);

        const derLen = 4 + r.length + 4 + s.length;
        const der = Buffer.allocUnsafe(derLen);

        der[0] = 0x30;
        der[1] = derLen - 2;
        der[2] = 0x02;
        der[3] = r.length;

        r.copy(der, 4);

        der[4 + r.length] = 0x02;
        der[5 + r.length] = s.length;

        s.copy(der, 6 + r.length);
        return der.toString('hex');
    };


    //
    const calculateSharedKey = (privateKey, publicKey) => {
        const keyPair = ec.keyFromPrivate(privateKey);
        const otherPublicKey = ec.keyFromPublic(publicKey, 'hex');
        const sharedKey = keyPair.derive(otherPublicKey.getPublic());

        return Buffer.from(sharedKey.toArray('be'), 'hex');
    };


    return {
        calculateSharedKey,
        genPrivateKey,
        generateKeyPair,
        signMessage,
        recoverPublicKey,
        verifySignature,
        derEncode
    };
};

export default EllipticCurve;