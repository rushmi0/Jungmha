import React, { useState, useEffect } from 'react';
import { useWebSocket } from 'react-use-websocket';

const Chat = () => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const {
        sendMessage,
        lastMessage,
        readyState,
    } = useWebSocket('ws://localhost:8080/ws/chat/all/milko');

    useEffect(() => {
        if (lastMessage !== null) {
            const newMessages = [...messages, JSON.parse(lastMessage.data)];
            setMessages(newMessages);
        }
    }, [lastMessage]);

    const handleInputChange = (e) => {
        setMessage(e.target.value);
    };

    const handleSendMessage = () => {
        const messageObject = {
            topic: 'all',
            username: 'milko',
            message: message,
        };
        sendMessage(JSON.stringify(messageObject));
        setMessage('');
    };

    return (
        <div>
            <div style={{ float: 'left', width: '50%' }}>
                {messages.map((msg, index) => (
                    <div key={index} style={{ textAlign: 'left', color: 'red' }}>
                        {msg.username}: {msg.message}
                    </div>
                ))}
            </div>
            <div style={{ float: 'right', width: '50%' }}>
                <input type="text" value={message} onChange={handleInputChange} />
                <button onClick={handleSendMessage}>Send</button>
            </div>
        </div>
    );
};

export default Chat;
