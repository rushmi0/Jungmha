import React from 'react'
import { Outlet } from 'react-router-dom'


//components
import Navbar from './Navbar'

function RootLayout() {

  return (
    <>
        <Navbar/>
        <Outlet />
    </>
  )
}

export default RootLayout