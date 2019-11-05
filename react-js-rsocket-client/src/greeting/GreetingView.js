import React, {Component} from 'react';
import {PrimaryButton} from 'pivotal-ui/react/buttons';
import {SuccessAlert} from 'pivotal-ui/react/alerts';
import {Form} from 'pivotal-ui/react/forms';
import {FlexCol, Grid} from 'pivotal-ui/react/flex-grids';

import {RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from "rsocket-websocket-client";

import GreetingClient from "./GreetingClient";

export default class GreetingView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            greeting: null,
        };
        this.greetingClient = new GreetingClient({
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
            <Form {...{
                onSubmit: ({initial, current}) => this.greet({name: `${current.firstName} ${current.lastName}`}),
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
        </div>
    }

    componentDidMount() {
        this.greetingClient
            .connect()
            .catch(e => console.error('Failed to connect', e));
    }

    componentWillUnmount() {
        this.greetingClient.disconnect();
    }

    greet(request) {
        this.greetingClient.greet(request.name)
            .then(greeting => this.setState({greeting: greeting}))
            .catch(e => console.error('Failed to greet', e));
    }
}