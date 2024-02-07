import React, { useState } from "react";
import axios from "axios";
import EllipticCurve from "../SecureKey.js"; //ecc/SecureKey.js

function Test() {
  const [ username, setUsername ] = useState("");
  const [ pass, setPass ] = useState("");

  const ec = EllipticCurve();
  const url = "localhost:8080/api/v1/auth/sign-up";

  const onUsernameEnter = (e) => {
    setUsername(e.target.value);
  };

  const onPassEnter = (e) => {
    setPass(e.target.value);
  };
  
  const onSubmit = (e) => {
    e.preventDefault();
    const privateKey = ec.genPrivateKey(pass);
    console.log(privateKey);

    const publicKey = ec.generateKeyPair(privateKey);
    
    const payload = {
      userName: username,
      authKey: authKey,
    };

    axios.post(url, payload)
    .then(console.log("Data posted!")).catch((err) => console.log(err));
  };

  return (
    <>
      <form onSubmit={onSubmit} method="PUT">
        <p>Username</p>
        <input
          type="text"
          name="username"
          className="text-white"
          onChange={onUsernameEnter}
        />
        <p>{username}</p>
        <p>Password</p>
        <input
          type="password"
          name="pass"
          className="text-white"
          onChange={onPassEnter}
        />
        <p></p>
        <button onSubmit={onSubmit} type="submit" className="btn btn-success text-white">
          Submit
        </button>
      </form>
    </>
  );
}

export default Test;