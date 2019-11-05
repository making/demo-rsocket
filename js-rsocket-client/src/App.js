import React, {Component} from 'react';
import {DefaultButton, PrimaryButton} from 'pivotal-ui/react/buttons';
import {SuccessAlert} from 'pivotal-ui/react/alerts';
import {Form} from 'pivotal-ui/react/forms';
import {FlexCol, Grid} from 'pivotal-ui/react/flex-grids';
import {DraggableList, DraggableListItem} from 'pivotal-ui/react/draggable-list';

import {RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from "rsocket-websocket-client";

import './Greeting';

import 'pivotal-ui/css/alignment';
import 'pivotal-ui/css/positioning';
import 'pivotal-ui/css/selection';
import 'pivotal-ui/css/typography';
import 'pivotal-ui/css/vertical-alignment';
import 'pivotal-ui/css/whitespace';

import './App.css';
import Greeting from "./Greeting";

export default class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            greeting: null,
            items: []
        };
        this.client = new RSocketClient({
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
            <Form {...{
                onSubmit: ({initial, current}) => this.hello({name: current.firstName + ' ' + current.lastName}),
                fields: {
                    firstName: {
                        initialValue: '',
                        validator: currentValue => currentValue.length > 0 ? null : 'First Name must not be empty',
                        label: 'First Name'
                    },
                    lastName: {
                        initialValue: '',
                        validator: currentValue => currentValue.length > 0 ? null : 'Last Name must not be empty',
                        label: 'Last Name'
                    }
                }
            }}>
                {({fields, canSubmit, onSubmit}) => {
                    return (
                        <div>
                            <Grid>
                                <FlexCol>{fields.firstName}</FlexCol>
                                <FlexCol>{fields.lastName}</FlexCol>
                                <FlexCol className="mtxxxl" fixed>
                                    <PrimaryButton type="submit"
                                                   disabled={!canSubmit()}>Hello</PrimaryButton>
                                </FlexCol>
                            </Grid>
                        </div>
                    );
                }}
            </Form>
            <br/>
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
                {this.state.items.map(i => <DraggableListItem key={i}>{i}</DraggableListItem>)}
            </DraggableList>
        </div>
    }

    componentDidMount() {
        this.client.connect()
            .subscribe({
                onComplete: socket => {
                    this.socket = socket;
                },
                onError: error => console.error(error),
                onSubscribe: cancel => {/* call cancel() to abort */
                }
            });
    }

    hello(request) {
        let that = this;
        this.socket && this.socket.requestResponse({
            data: JSON.stringify({}),
            metadata: routingMetadata(`greeting/${request.name}`)
        }).subscribe({
            onComplete(payload) {
                let body = JSON.parse(payload.data);
                that.setState({
                    greeting: new Greeting(body)
                })
            },
            onError: (e) => {
                console.error('onError', e)
            }
        });
    }

    names() {
        let that = this;
        let maxInFlight = 20;
        let current = maxInFlight;
        let subscription;
        this.socket && this.socket.requestStream({
            data: JSON.stringify({}),
            metadata: routingMetadata('name')
        }).subscribe({
            onSubscribe: sub => {
                subscription = sub;
                console.log('request', maxInFlight);
                subscription.request(maxInFlight);
            },
            onNext: (payload) => {
                let body = JSON.parse(payload.data);
                let items = that.state.items;
                items.unshift(`${body.lastName} ${body.firstName}`);
                while (items.length > 20) {
                    items.pop();
                }
                that.setState({
                    items: items
                });
                current--;
                if (current === 0) {
                    current = maxInFlight;
                    console.log('request', maxInFlight);
                    setTimeout(() => {
                        subscription.request(maxInFlight);
                    }, 1000);
                }
            },
            onError: (e) => {
                console.error('onError', e)
            }
        });
    }

}

function routingMetadata(route) {
    return String.fromCharCode(route.length) + route;
}