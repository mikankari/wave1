package com.example.wave1;

import java.io.BufferedInputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.media.AudioFormat;
import android.view.View;

public class WaveView extends View{

	Insets margin = new Insets(17, 40, 20, 30); // 上, 左, 下, 右
    Boolean bBorder;
    int width, height;          // グラフ描画領域の幅と高さ
    double vMax = 128.0, vMin = -128.0;
    byte[] data;
    AudioFormat af;


	public WaveView(Context context){
		super(context);
		
		init();
	}
	
    int nx(double x){ return (int)Math.round(margin.left + x*width); }
    int ny(double y){ return (int)Math.round(margin.top + (1-y)*height); }
    int yVal(byte v){ return ny((v&0xFF) / (vMax - vMin)); }

    public void init() {        // 最初に実行するメソッドです
        try {
//            bBorder = "yes".equals(getParameter("border"));
            URL url = new URL("/mnt/sdcard/music/[初音ミク]  愛言葉  [オリジナル曲].mp3");
//            double time = Double.parseDouble(getParameter("time"));
            //InputStream in = url.openStream();        // 2013.8.20 下のように変更
            BufferedInputStream in = new BufferedInputStream(url.openStream());
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            af = ais.getFormat();
            int nBytes = (int)(af.getSampleRate() * time);
            data = new byte[nBytes];
            ais.read(data, 0, nBytes);
            ais.close();
            in.close(); // 2013.8.20追加
        } catch (Exception e) {
            System.out.println("PlotWav.init " + e);
        }
    }

    void tick(Graphics g, int nX, int nY) {
        FontMetrics fm = g.getFontMetrics();
        int h = fm.getAscent(); // 文字の高さ
        for (int d = 0; d <= nX; d++) {
            double x = (double)d / nX;
            double time = x * data.length / af.getSampleRate();
            g.drawLine(nx(x), ny(0), nx(x), ny(0)-5);
            String str = String.format("%.0f", time * 1000);
            if (d == nX) str += "mS";
            int w = fm.stringWidth(str);
            g.drawString(str, nx(x)-w/2, ny(0)+h+2);
        }
        for (int n = 0; n <= nY; n++) {
            double y = (double)n / nY;
            g.setColor(Color.lightGray);
            g.drawLine(nx(0), ny(y), nx(1.0), ny(y));
            String str = String.format("%.0f", vMin + (vMax-vMin)*y);
            int w = fm.stringWidth(str);
            g.setColor(Color.black);
            g.drawString(str, nx(0)-w-5, ny(y)+h/2);
        }
    } 

    public void onDraw(Canvas g){
        width  = getWidth();            // アプレット描画領域の幅
        height = getHeight();           // アプレット描画領域の高さ
        if (bBorder) g.drawRect(0, 0, width-1, height-1); // 表示領域境界線描画
        width  -= margin.left + margin.right;
        height -= margin.top  + margin.bottom;
        g.drawRect(margin.left, margin.top, width-1, height-1); // 座標軸描画
        tick(g, 10, 6);
        int xLast = 0, yLast = 0;
        for (int n = 0; n < data.length; n++) {
            double x = (n + 0.5) / data.length;
            if (n > 0) g.drawLine(xLast, yLast, nx(x), yVal(data[n]));
            xLast = nx(x);
            yLast = yVal(data[n]);
        }
    }
    
    public static class Insets(){
    	public int left;
    	public int top;
    	public int right;
    	public int bottom;
    	public Insets(int left, int top, int right, int bottom){
    		this.left = left;
    		this.top = top;
    		this.right = right;
    		this.bottom = bottom;
    	}
    }
}

}
