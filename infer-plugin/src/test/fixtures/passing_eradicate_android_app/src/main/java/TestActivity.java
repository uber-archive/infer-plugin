import android.app.Activity;
import com.squareup.leakcanary.LeakCanary;
import org.jetbrains.annotations.Nullable;
import javax.annotation.Generated;

class TestActivity extends Activity {

    @Nullable
    String mayReturnNull() {
        return null;
    }

    int cantCauseNPE() {
        String s = mayReturnNull();
        return s == null ? 0 : s.length();
    }
}
