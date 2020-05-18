package lastmile.meizu.com.lastmile;


import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.meizu.lastmile.service.PingService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
        new PingService().getPingInfo(null);
        Process p = Runtime.getRuntime().exec("ping " + "117.141.138.101");
        int status = p.waitFor();
        if (status == 0) {
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = new String();
            String delay="";
            while ((str = buf.readLine()) != null) {
                if (str.contains("avg")) {
                    int i = str.indexOf("/", 20);
                    int j = str.indexOf(".", i);
                    delay = str.substring(i + 1, j);
                }
            }
//            Log.d("look_fps", delay);
            System.out.println("delay:"+delay);
        }

    }
}