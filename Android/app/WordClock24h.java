package de.tcpix.wordclock24h.app;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class WordClock24h extends Activity {

    public static final int WP_COUNT = 79;
    public static final int WC_COLUMNS = 18;
    public static final int WC_ROWS  = 16;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    static final String[][] display;
    static int[][] GlyphIDs;
    static {
        display = new String[][]{new String[]{ // dummy-dimension for screen-saver-variant with capital ß
                "ES#IST#VIERTELEINS",
                "DREINERSECHSIEBEN#",
                "ELFÜNFNEUNVIERACHT",
                "NULLZWEI#ZWÖLFZEHN",
                "UND#ZWANZIGVIERZIG",
                "DREISSIGFÜNFZIGUHR",
                "MINUTEN#VORBISNACH",
                "UNDHALBDREIVIERTEL",
                "SIEBENEUNULLZWEINE",
                "FÜNFSECHSACHTVIER#",
                "DREINSUND#ELF#ZEHN",
                "ZWANZIG###DREISSIG",
                "VIERZIGZWÖLFÜNFZIG",
                "MINUTENUHR#FRÜHVOR",
                "ABENDSMITTERNACHTS",
                "MORGENS....MITTAGS"
        }};
        GlyphIDs = new int[WC_ROWS][WC_COLUMNS];
    }
    /**
     * Generate a value suitable for use in {@link_ #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateMyViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                // Log.i("WC24h","ViewId: "+ result);
                return result;
            }
        }
    }
    WC24h WC24hData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_clock24h);

        WC24hData = new WC24h();
        WC24hData.loadJSON(getString(R.string.init_display));

        TableLayout tl = (TableLayout) findViewById(R.id.MainTable);
        for (int row = 0; row < WC_ROWS; row++) {
            TableRow tr = new TableRow(this);
            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            for (int col = 0; col < WC_COLUMNS; col++) {
                TextView tv = new TextView(this);
                tv.setEms(1);
                tv.setTextSize(30);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setText(display[0][row].charAt(col) + "");
                int newID;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    newID = generateMyViewId();
                } else {
                    newID = View.generateViewId();
                }
                tv.setId(newID);
                GlyphIDs[row][col] = newID;
                tv.setTextColor(Color.parseColor("#111111"));
                tr.addView(tv);
            }
        }
        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayData(); // this action have to be in UI thread
                            }
                        });
                    } catch (InterruptedException e) {
                        // ooops
                    }
                }
            }
        })).start(); // the while thread will start in BG thread
    }

    protected int _LastSecond = 0;
    protected int _TestRow = 0;
    protected int _TestCol = 0;
    protected TextView AnimatedText;
    protected ValueAnimator colorAnimation;
    protected void displayData() {
        Calendar c = Calendar.getInstance();
        int s = c.get(Calendar.SECOND);
        if (s != this._LastSecond) {
            this._LastSecond = s;
            AnimatedText = (TextView) findViewById(GlyphIDs[_TestRow][_TestCol]);
            // tv.setTextColor(Color.parseColor("#F5DC49"));
            Integer colorFrom = Color.parseColor("#111111");
            Integer colorTo = Color.parseColor("#FFFF00");
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    AnimatedText.setTextColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.setDuration(800); // length of the animation, in milliseconds
            colorAnimation.start();
            if (++_TestCol >= WC_COLUMNS) {
                _TestCol = 0;
                if (++_TestRow >= WC_ROWS) _TestRow = 0;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.word_clock24h, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
