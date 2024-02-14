import React, { useState } from 'react';
import { Buffer } from "buffer";
import ChaCha20 from "../../utils/ChaCha20.js";




const DemoChaCha20 = () => {
    const [privateKey, setPrivateKey] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [type, setType] = useState('');
    const [encryptedData, setEncryptedData] = useState('');

    const handleEncrypt = async () => {
        const data = {
            firstName: firstName,
            lastName: lastName,
            email: email,
            phoneNumber: phoneNumber,
            userType: type
        };

        const jsonString = JSON.stringify(data);
        const chacha = ChaCha20()

        if (privateKey && jsonString) {
            try {
                const encryptedResult = chacha.encrypt(
                    jsonString,
                    privateKey
                );
                setEncryptedData(encryptedResult);
            } catch (error) {
                console.error('Encryption failed:', error);
            }
        } else {
            alert('กรุณากรอกข้อมูลให้ครบถ้วน');
        }
    };


    return (
        <>
            <div style={{maxWidth: '400px', margin: '0 auto', fontFamily: 'Arial, sans-serif'}}>

                <div style={{marginBottom: '40px', textAlign: 'center', marginTop: '20px'}}>
                    <h1 style={{fontSize: '24px', fontWeight: 'bold'}}>ChaCha20 Encryption Demo</h1>
                </div>


                <div style={{marginBottom: '15px'}}>
                    <label style={{display: 'block', marginBottom: '5px'}}>Private Key:</label>
                    <input
                        type="text"
                        value={privateKey}
                        onChange={(e) => setPrivateKey(e.target.value)}
                        style={{width: '100%', padding: '5px'}}
                    />
                </div>
                <div style={{marginBottom: '15px'}}>
                    <label style={{display: 'block', marginBottom: '5px'}}>First Name:</label>
                    <input
                        type="text"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        style={{width: '100%', padding: '5px'}}
                    />
                </div>
                <div style={{marginBottom: '15px'}}>
                    <label style={{display: 'block', marginBottom: '5px'}}>Last Name:</label>
                    <input
                        type="text"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        style={{width: '100%', padding: '5px'}}
                    />
                </div>

                <div style={{marginBottom: '15px'}}>
                    <label style={{display: 'block', marginBottom: '5px'}}>Email:</label>
                    <input
                        type="text"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        style={{width: '100%', padding: '5px'}}
                    />
                </div>

                <div style={{marginBottom: '15px'}}>
                    <label style={{display: 'block', marginBottom: '5px'}}>Phone Number:</label>
                    <input
                        type="text"
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        style={{width: '100%', padding: '5px'}}
                    />
                </div>

                <div style={{marginBottom: '15px'}}>
                    <label style={{display: 'block', marginBottom: '5px'}}>User Type:</label>
                    <input
                        type="text"
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                        style={{width: '100%', padding: '5px'}}
                    />
                </div>

                <button onClick={handleEncrypt} style={{
                    backgroundColor: 'green',
                    color: '#fff',
                    border: 'none',
                    padding: '10px',
                    borderRadius: '5px',
                    cursor: 'pointer',
                    width: '100%'
                }}>Encrypt Data
                </button>

                <div style={{marginTop: '15px'}}>
                    <h3 style={{marginBottom: '5px'}}>Encrypted Data:</h3>
                    <div style={{
                        border: '1px solid #ccc',
                        borderRadius: '5px',
                        backgroundColor: '#f9f9f9',
                        padding: '10px'
                    }}>
                        <p style={{margin: '0', wordBreak: 'break-all'}}>{encryptedData}</p>
                    </div>
                </div>

            </div>
        </>
    );
};

export default DemoChaCha20;
