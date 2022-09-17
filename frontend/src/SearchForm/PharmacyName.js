import React from 'react';
import {FormGroup, InputGroup} from "@blueprintjs/core";
import {INTENT_PRIMARY} from "@blueprintjs/core/lib/esnext/common/classes";

const PharmacyName = () => {
    return (
        <FormGroup label="Pharmacy Name">
            <InputGroup id="pharmacy-name-input"
                        type="search"
                        placeholder="Type Pharmacy Name"
                        disabled={false}
                        intent={INTENT_PRIMARY} />
        </FormGroup>
    );
};

export default PharmacyName;