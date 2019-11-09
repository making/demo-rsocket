import React, {Component} from 'react';
import './App.css';
import {PingRequest} from './proto/pingpong_pb';
import {RequestHandlingRSocket, RpcClient} from "rsocket-rpc-core";
import RSocketWebSocketClient from "rsocket-websocket-client";
import {BufferEncoders} from "rsocket-core";
import {PingPongServiceClient} from "./proto/pingpong_rsocket_pb";

class App extends Component {
    constructor(props) {
        super(props);
        const responder = new RequestHandlingRSocket();
        const keepAlive = 60000 /* 60s in ms */;
        const lifetime = 360000 /* 360s in ms */;
        const transport = new RSocketWebSocketClient({url: 'ws://localhost:9999'}, BufferEncoders);
        this.client = new RpcClient({setup: {keepAlive, lifetime}, transport, responder});
        this.state = {
            message: ''
        };
    }

    componentDidMount() {
        this.connect()
            .then(socket => this.pingPongClient = new PingPongServiceClient(socket));
    }

    connect() {
        return new Promise((resolve, reject) => {
            this.client.connect()
                .subscribe({
                    onComplete: socket => {
                        this.socket = socket;
                        resolve(this.socket);
                    },
                    onError: error => {
                        console.error('Failed to connect!', error);
                        reject(error);
                    },
                    onSubscribe: cancel => {
                        this.cancel = cancel
                    }
                });
        });
    }

    ping() {
        this.setState({
            message: '...'
        });
        if (!this.socket._machine._connectionAvailability) {
            alert('Connection is closed.');
            return;
        }
        this.pingPongClient
            .ping(new PingRequest().setMessage('PING'))
            .subscribe({
                    onComplete: data => this.setState({message: data.getMessage()}),
                    onError: error => {
                        console.error('Failed to ping!', error);
                        alert('Failed to ping!');
                    }
                }
            );
    }

    render() {
        return (
            <div>
                <button onClick={this.ping.bind(this)}>Ping</button>
                <p>{this.state.message}</p>
            </div>
        );
    }
}

export default App;
