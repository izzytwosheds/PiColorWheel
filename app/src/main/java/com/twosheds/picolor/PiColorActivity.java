package com.twosheds.picolor;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class PiColorActivity extends Activity implements PiColorView.ColorPickedListener {
    private final double PHI = (1 + Math.sqrt(5)) / 2;

    private ViewGroup rootView;
    private TextView piView;
    private TextView piColorView;
    private TextView eView;
    private TextView eColorView;
    private TextView phiView;
    private TextView phiColorView;
    private TextView colorView;
    private PiColorView piColorWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pi_color);

        rootView = (ViewGroup) findViewById(R.id.root_view);

        piView = (TextView) findViewById(R.id.pi);
        piColorView = (TextView) findViewById(R.id.pi_color);
        eView = (TextView) findViewById(R.id.e);
        eColorView = (TextView) findViewById(R.id.e_color);
        phiView = (TextView) findViewById(R.id.phi);
        phiColorView = (TextView) findViewById(R.id.phi_color);

        colorView = (TextView) findViewById(R.id.color);
        piColorWheelView = (PiColorView) findViewById(R.id.pi_color_wheel);
        piColorWheelView.setColorPickerListener(this);
    }

    @Override
    public void onColorPicked(int color) {
        colorView.setText(String.format("#%06X", (color & 0x00FFFFFF)));
        piColorView.setText(String.format("#%06X", getPiColor(color)));
        eColorView.setText(String.format("#%06X", getEColor(color)));
        phiColorView.setText(String.format("#%06X", getPhiColor(color)));

        piView.setTextColor(getPiColor(color) | (0xFF << 24));
        eView.setTextColor(getEColor(color) | (0xFF << 24));
        phiView.setTextColor(getPhiColor(color) | (0xFF << 24));

        rootView.invalidate();
    }

    private int getPiColor(int color) {
        return (int) ((double) (color & 0xFFFFFF) / Math.PI);
    }

    private int getEColor(int color) {
        return (int) ((double) (color & 0xFFFFFF) / Math.E);
    }

    private int getPhiColor(int color) {
        return (int) ((double) (color & 0xFFFFFF) / PHI);
    }
}
