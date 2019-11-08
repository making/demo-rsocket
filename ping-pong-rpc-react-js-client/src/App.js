import React from 'react';
import logo from './logo.svg';
import './App.css';
import {RequestHandlingRSocket, RpcClient} from 'rsocket-rpc-core';
import {PingPongServiceClient} from './proto/pingpong_rsocket_pb';
import {PingRequest} from './proto/pingpong_pb';
import {BufferEncoders} from "rsocket-core";
import RSocketWebSocketClient from "rsocket-websocket-client";

function App() {
    const responder = new RequestHandlingRSocket();
    const keepAlive = 60000 /* 60s in ms */;
    const lifetime = 360000 /* 360s in ms */;
    const transport = new RSocketWebSocketClient({url: 'ws://localhost:9999'}, BufferEncoders);
    const rsocketClient = new RpcClient({setup: {keepAlive, lifetime}, transport, responder});
    const pingRequest = new PingRequest().setMessage('PING');

    let pongServiceClient = null;
    rsocketClient.connect()
        .subscribe({
            onComplete: rsocket => {
                pongServiceClient = new PingPongServiceClient(rsocket);
                pongServiceClient.ping(pingRequest).subscribe({
                    onComplete: data => {
                        console.log(data.getMessage());
                    }
                });
            },
            onError: error => {
                console.error("Failed to connect to RSocket server.", error);
            },
            onSubscribe: cancel => {
            }
        });

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <p>
                    Edit <code>src/App.js</code> and save to reload.
                </p>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
            </header>
        </div>
    );
}

export default App;
