import org.jetbrains.annotations.Nullable;
import javax.annotation.Generated;

class PassingEradicateExample {

    @Nullable
    String mayReturnNull() {
        return null;
    }

    int cantCauseNPE() {
        String s = mayReturnNull();
        return s == null ? 0 : s.length();
    }
}
