import CryptoJS from 'crypto-js';

const AES = () => {

    const encrypt = (data, sharedKey) => {
        const iv = CryptoJS.lib.WordArray.random(16);
        const encryptedData = CryptoJS.AES.encrypt(data, sharedKey, { iv: iv }).toString();
        const ivBase64 = CryptoJS.enc.Base64.stringify(iv);

        return encryptedData + '?iv=' + ivBase64;
    };

    const decrypt = (encryptedData, sharedKey) => {
        const [encryptedString, ivBase64] = encryptedData.split('?iv=');
        const ivDecoded = CryptoJS.enc.Base64.parse(ivBase64);
        const decrypted = CryptoJS.AES.decrypt(encryptedString, sharedKey, { iv: ivDecoded });
        const decryptedString = decrypted.toString(CryptoJS.enc.Utf8);

        return decryptedString;
    };

    return {
        encrypt,
        decrypt
    };
};

export default AES;