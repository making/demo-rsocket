import React, {Component} from 'react';
import {PrimaryButton} from 'pivotal-ui/react/buttons';
import {SuccessAlert} from 'pivotal-ui/react/alerts';
import {Form} from 'pivotal-ui/react/forms';
import {FlexCol, Grid} from 'pivotal-ui/react/flex-grids';

import GreetingClient from "./GreetingClient";

export default class GreetingView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            greeting: null,
        };
        this.greetingClient = new GreetingClient({});
    }

    render() {
        return <div>
            <Form {...{
                onSubmit: ({initial, current}) => this.greet(current),
                fields: {
                    name: {
                        initialValue: '',
                        validator: currentValue => currentValue.length > 0 ? null : 'Name must not be empty',
                        label: 'Name'
                    }
                }
            }}>
                {({fields, canSubmit, onSubmit}) => {
                    return (
                        <div>
                            <Grid>
                                <FlexCol>{fields.name}</FlexCol>
                                <FlexCol className="mtxxxl" fixed>
                                    <PrimaryButton type="submit"
                                                   disabled={!canSubmit()}>Greet</PrimaryButton>
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