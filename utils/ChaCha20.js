import nacl from "tweetnacl";


const ChaCha20 = () => {
    const encrypt = (data, sharedKey) => {
        const key = Uint8Array.from(Buffer.from(sharedKey, 'hex'));

        const nonce = nacl.randomBytes(nacl.secretbox.nonceLength);
        const ciphertext = nacl.secretbox(Buffer.from(data), nonce, key);

        const nonceBase64 = Buffer.from(nonce).toString('base64');
        const base64ciphertext = Buffer.from(ciphertext).toString('base64');

        return base64ciphertext + '?iv=' + nonceBase64;
    };

    const decrypt = (data, sharedKey) => {
        const [ciphertextBase64, nonceBase64] = data.split('?iv=');

        const key = Uint8Array.from(Buffer.from(sharedKey, 'hex'));

        const nonce= Buffer.from(nonceBase64, 'base64');
        const ciphertext = Buffer.from(ciphertextBase64, 'base64');

        const decryptedText = nacl.secretbox.open(ciphertext, nonce, key);
        return Buffer.from(decryptedText).toString()
    };

    return {
        encrypt,
        decrypt
    };
};

export default ChaCha20;

const chacha20 = ChaCha20();
const keyHex = '3e11810c67157bf584db16bbd85d9e9b339b4469e27390365195379cb2168a78';

const encryptedData = chacha20.encrypt("Mai", keyHex);
console.log(encryptedData);

const decryptedData = chacha20.decrypt(
    encryptedData,
    keyHex
)

console.log(decryptedData)




