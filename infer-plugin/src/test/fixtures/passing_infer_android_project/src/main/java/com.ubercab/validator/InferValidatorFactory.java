import android.support.annotation.NonNull;

import com.ubercab.rave.BaseValidator;
import com.ubercab.rave.ValidatorFactory;

/**
 * A RAVE validator for music models.
 */
public class InferValidatorFactory implements ValidatorFactory {

    @NonNull
    @Override
    public BaseValidator generateValidator() {
        return new InferValidatorFactory_Generated_Validator();
    }
}
