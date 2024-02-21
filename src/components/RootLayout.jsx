import React, {useEffect, useState} from 'react'
import { Outlet } from 'react-router-dom'


//components
import Navbar from './Navbar'
import LoginNavbar from "./LoginNavbar.jsx";
import LoginNavbarDogwalkers from "./LoginNavbarDogwalkers.jsx";

function RootLayout() {
    const [isLogin, setLogin] = useState(false);
    const [token, setToken] = useState("");
    const getToken = JSON.parse(localStorage.getItem("user-token"));
    const type = localStorage.getItem("type");
    const [user, setUser] = useState(false);
    const [dog, setDog] = useState(false);

    useEffect(() => {
        if(!getToken) {
            setLogin(false);
        } else {
            setToken(getToken.token.view);
            setLogin(true);
        }
        checkType(type)
    }, [])

    console.log("isLogin: ",isLogin);
    console.log("Type: ",type);

    const checkType = (type) => {
        if(type == "Normal") {
            setUser(true);
            setDog(false);
        } else if (type == "DogWalkers"){
            setUser(false);
            setDog(true);

        }
    }

    // const checkToken = () => {
    //
    // }



  return (
    <>
        {isLogin && user  && (
            <LoginNavbar/>
        )}
        {isLogin && dog && (
            <LoginNavbarDogwalkers/>
        )}
        {!isLogin && (
            <Navbar/>
        )}
        <Outlet />
    </>
  )
}

export default RootLayout