import React, {Component} from 'react';
import {DefaultButton} from 'pivotal-ui/react/buttons';
import {SuccessAlert} from 'pivotal-ui/react/alerts';
import {DraggableList, DraggableListItem} from 'pivotal-ui/react/draggable-list';

import {RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from "rsocket-websocket-client";

import NameClient from "./NameClient";

export default class NameView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            items: []
        };
        this.nameClient = new NameClient({
            client: this.createRSocketClient()
        });
    }

    createRSocketClient() {
        return new RSocketClient({
            setup: {
                // ms btw sending keepalive to server
                keepAlive: 10000,
                // ms timeout if no keepalive response
                lifetime: 20000,
                dataMimeType: 'application/json',
                metadataMimeType: 'message/x.rsocket.routing.v0'
            },
            transport: new RSocketWebSocketClient({url: 'ws://localhost:7000/rsocket'}),
        });
    }

    render() {
        return <div>
            <SuccessAlert
                withIcon
                dismissable
                onDismiss={() => this.setState({greeting: null})}
                show={this.state.greeting != null}>{this.state.greeting && this.state.greeting.toString()}</SuccessAlert>
            <br/>
            <DefaultButton onClick={() => this.names()}>Generate names</DefaultButton>
            <br/>
            <br/>
            <DraggableList>
                {this.state.items.map(i => <DraggableListItem key={i.toString()}>{i.toString()}</DraggableListItem>)}
            </DraggableList>
        </div>
    }

    componentDidMount() {
        this.nameClient
            .connect()
            .catch(e => console.error('Failed to connect', e));
    }

    componentWillUnmount() {
        this.nameClient.disconnect();
    }

    names() {
        this.nameClient.names({
            doOnNext: name => {
                let items = this.state.items;
                items.unshift(name);
                while (items.length > 20) {
                    items.pop();
                }
                this.setState({
                    items: items
                });
            },
            doOnError: e => console.error('Failed to retrieve names', e),
            doOnLastOfBatch: (subscription, maxInFlight) => {
                setTimeout(() => {
                    console.log('request', maxInFlight);
                    subscription.request(maxInFlight);
                }, 1000);
                return maxInFlight;
            }
        });
    }

}