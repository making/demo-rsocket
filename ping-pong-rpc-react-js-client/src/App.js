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

    async componentDidMount() {
        const socket = await this.client.connect();
        this.pingPongClient = new PingPongServiceClient(socket);
    }

    async ping() {
        this.setState({
            message: '...'
        });
        const response = await this.pingPongClient.ping(new PingRequest().setMessage('PING'));
        this.setState({message: response.getMessage()});
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
