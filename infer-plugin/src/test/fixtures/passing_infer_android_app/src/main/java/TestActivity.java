import android.app.Activity;
import com.squareup.leakcanary.LeakCanary;

class TestActivity extends Activity {

    String mayReturnNull(int i) {
        return "Hello, Infer!";
    }

    int cantCauseNPE() {
        String s = mayReturnNull(0);
        return s.length();
    }
}
