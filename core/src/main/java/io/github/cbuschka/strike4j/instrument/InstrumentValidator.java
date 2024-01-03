package io.github.cbuschka.strike4j.instrument;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class InstrumentValidator {

    private final ValidatorFactory factory;

    public InstrumentValidator() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    public Set<ConstraintViolation<Instrument>> validate(Instrument instrument) {
        Validator validator = factory.getValidator();
        return validator.validate(instrument);
    }
}
