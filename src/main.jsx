import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import DemoChaCha20 from "./TestAuth/DemoChaCha20.jsx";
import Chat from "./ws/ChatApp.jsx";

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/*<App />*/}
    {/*  <DemoChaCha20/>*/}
      <Chat/>
  </React.StrictMode>,
)
