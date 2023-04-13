package io.github.cbuschka.strike4j.instrument;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
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
