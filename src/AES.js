import crypto from 'crypto';
import {Buffer} from 'buffer';

const AES = () => {

    const encrypt = (data, sharedKey) => {

        let iv = crypto.randomFillSync(new Uint8Array(16));

        const cipher = crypto.createCipheriv(
            'aes-256-cbc',
            Buffer.from(sharedKey, 'hex'),
            iv
        );

        let encryptedData = cipher.update(data, 'utf8', 'base64');
        encryptedData += cipher.final('base64');

        let ivBase64 = Buffer.from(iv.buffer).toString('base64');

        return encryptedData + '?iv=' + ivBase64;
    };

    const decrypt = (encryptedData, sharedKey) => {

        let [encryptedString, ivBase64] = encryptedData.split('?iv=');

        let ivDecoded = Buffer.from(ivBase64, 'base64');
        const decipher = crypto.createDecipheriv('aes-256-cbc', sharedKey, ivDecoded);

        let decryptedString = decipher.update(encryptedString, 'base64', 'utf8');
        decryptedString += decipher.final('utf8');

        return JSON.parse(decryptedString);
    };

    return {
        encrypt,
        decrypt
    };
};

export default AES;