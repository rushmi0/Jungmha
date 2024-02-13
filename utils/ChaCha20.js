import chacha20 from "chacha20";


const ChaCha20 = () => {

    const encrypt = (data, sharedKey) => {
        const key = Buffer.from(sharedKey, 'hex');
        const nonce = Buffer.alloc(12);

        const ciphertext = chacha20.encrypt(key, nonce, Buffer.from(data));

        const nonceBase64 = Buffer.from(nonce).toString('base64');
        const base64ciphertext = Buffer.from(ciphertext).toString('base64');

        return base64ciphertext + '?iv=' + nonceBase64;
    };


    const decrypt = (data, sharedKey) => {
        const [ciphertextBase64, nonceBase64] = data.split('?iv=');

        const key= Buffer.from(sharedKey, 'hex');

        const nonce= Buffer.from(nonceBase64, 'base64');
        const ciphertext = Buffer.from(ciphertextBase64, 'base64');

        const decrypt = chacha20.decrypt(key, nonce, ciphertext).toString()
        const string_json = Buffer.from(decrypt).toString()
        return JSON.parse(string_json);
    };

    return {
        encrypt,
        decrypt
    };

};

export default ChaCha20;
