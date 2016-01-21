import android.app.Activity;
import com.squareup.leakcanary.LeakCanary;

class TestActivity extends Activity {

    String mayReturnNull(int i) {
        if (i > 0) {
            return "Hello, Infer!";
        }
        return null;
    }

    int mayCauseNPE() {
        String s = mayReturnNull(0);
        return s.length();
    }
}
