import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.ubercab.rave.Validated;

@Validated(factory = InferValidatorFactory.class)
public abstract class Infer implements Parcelable {

    @Nullable
    public abstract String getName();

    public abstract Infer setName(String name);
}
