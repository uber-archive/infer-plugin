import android.app.Activity;
import com.squareup.leakcanary.LeakCanary;
import javax.annotation.Generated;

class TestActivity extends Activity {

    String mayReturnNull() {
        return null;
    }

    int cantCauseNPE() {
        String s = mayReturnNull();
        return s.length();
    }
}
