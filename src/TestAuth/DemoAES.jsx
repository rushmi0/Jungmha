import React, { useState } from 'react';
import {Buffer} from "buffer";
import AES from "../../utils/AES.js";


const DemoAES = () => {
    const [privateKey, setPrivateKey] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [encryptedData, setEncryptedData] = useState('');

    const handleEncrypt = () => {
        const data = {
            firstName: firstName,
            lastName: lastName,
            email: email,
            phoneNumber: phoneNumber,
        };

        const jsonString = JSON.stringify(data);
        const aes = AES();

        // Buffer.from(privateKey, 'hex');
        if (privateKey && jsonString) {
            const encryptedResult = aes.encrypt(
                jsonString,
                Buffer.from(privateKey, 'hex')
            );
            setEncryptedData(encryptedResult);
        } else {
            // กรณีไม่ได้กรอก privateKey หรือ jsonString
            alert('กรุณากรอกข้อมูลให้ครบถ้วน');
        }
    };

    return (
        <div>
            <h1>AES Encryption Demo</h1>
            <label>
                Private Key:
                <input
                    type="text"
                    value={privateKey}
                    onChange={(e) => setPrivateKey(e.target.value)}
                />
            </label>
            <br />
            <label>
                First Name:
                <input
                    type="text"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                />
            </label>
            <br />
            <label>
                Last Name:
                <input
                    type="text"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                />
            </label>
            <br />
            <label>
                Email:
                <input
                    type="text"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
            </label>
            <br />
            <label>
                Phone Number:
                <input
                    type="text"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                />
            </label>
            <br />
            <button onClick={handleEncrypt} style={{ backgroundColor: 'green' }}>
                Encrypt Data
            </button>
            <br />
            <h3>Encrypted Data:</h3>
            <p>{encryptedData}</p>
        </div>
    );
};

export default DemoAES;
