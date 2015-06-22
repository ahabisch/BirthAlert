package ca.birthalert.aronne.birthalertrev0;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanManager;


/**
 * Created by Aronne on 21/06/2015.
 */
public class Connect extends ActionBarActivity {


    BeanDiscoveryListener listener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int i) {

        }

        @Override
        public void onDiscoveryComplete() {

        }
    };
}
