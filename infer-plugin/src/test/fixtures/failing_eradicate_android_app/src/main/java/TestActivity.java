import android.app.Activity;
import com.squareup.leakcanary.LeakCanary;

class TestActivity extends Activity {

    String mayReturnNull() {
        return null;
    }

    int cantCauseNPE() {
        String s = mayReturnNull();
        return s.length();
    }
}
