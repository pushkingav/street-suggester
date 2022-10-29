import React from 'react';
import {FormGroup, InputGroup} from "@blueprintjs/core";
import {INTENT_PRIMARY} from "@blueprintjs/core/lib/esnext/common/classes";

const SearchField = ({label, type, placeholder, disabled, id, onChangeHandler, onKeyDownHandler}) => {
    const changeHandler = (event) => {
        onChangeHandler(event.target.value);
    }
    return (
        <FormGroup label={label}>
            <InputGroup id={id}
                        type={type}
                        placeholder={placeholder != null ? placeholder : null}
                        disabled={disabled != null ? disabled : false}
                        onChange={changeHandler}
                        onKeyDown={onKeyDownHandler}
                        intent={INTENT_PRIMARY} />
        </FormGroup>
    );
};

export default SearchField;