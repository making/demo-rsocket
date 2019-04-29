import React, {Component} from 'react';
import {PrimaryButton} from 'pivotal-ui/react/buttons';
import {SuccessAlert} from 'pivotal-ui/react/alerts';
import {Form} from 'pivotal-ui/react/forms';
import {FlexCol, Grid} from 'pivotal-ui/react/flex-grids';

import {RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from "rsocket-websocket-client";

import 'pivotal-ui/css/alignment';
import 'pivotal-ui/css/positioning';
import 'pivotal-ui/css/selection';
import 'pivotal-ui/css/typography';
import 'pivotal-ui/css/vertical-alignment';
import 'pivotal-ui/css/whitespace';

import './App.css';

export default class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            message: ''
        };
        this.client = new RSocketClient({
            setup: {
                keepAlive: 60000,
                lifetime: 180000,
                dataMimeType: 'application/json',
                metadataMimeType: 'text/plain'
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
                onDismiss={() => this.setState({message: ''})}
                show={this.state.message.length > 0}>{this.state.message}</SuccessAlert>
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
            data: JSON.stringify(request),
            metadata: 'hello'
        }).subscribe({
            onComplete(payload) {
                let body = JSON.parse(payload.data);
                that.setState({
                    message: body.message
                })
            },
            onError: (e) => {
                console.error('onError', e)
            }
        });
    }
}