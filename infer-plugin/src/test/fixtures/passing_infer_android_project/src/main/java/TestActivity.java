import android.app.Activity;
import com.squareup.leakcanary.LeakCanary;
import javax.annotation.Generated;

class TestActivity extends Activity {

    String mayReturnNull(int i) {
        return "Hello, Infer!";
    }

    int cantCauseNPE() {
        String s = mayReturnNull(0);
        return s.length();
    }
}
