package com.example.wave1;

import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

public class WaveView extends View{

	byte[] waveform;

	public WaveView(Context context){
		super(context);
		
		init();
	}
	
    public void init() {        // �ŏ��Ɏ��s���郁�\�b�h�ł�
//        try {
////            bBorder = "yes".equals(getParameter("border"));
//            URL url = new URL("/mnt/sdcard/music/[�����~�N]  �����t  [�I���W�i����].mp3");
////            double time = Double.parseDouble(getParameter("time"));
//            //InputStream in = url.openStream();        // 2013.8.20 ���̂悤�ɕύX
//            BufferedInputStream in = new BufferedInputStream(url.openStream());
//            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
//            af = ais.getFormat();
//            int nBytes = (int)(af.getSampleRate() * time);
//            data = new byte[nBytes];
//            ais.read(data, 0, nBytes);
//            ais.close();
//            in.close(); // 2013.8.20�ǉ�
//        } catch (Exception e) {
//            System.out.println("PlotWav.init " + e);
//        }
    	
//    	try{
    	MediaPlayer player = MediaPlayer.create(getContext(), Uri.parse("/mnt/sdcard/music/�u�Ԃ��ʂ��I�Q�O�O�W�I�v�@�I���W�i���ȁ@vo.�����~�N.mp3"));
//    	}catch(IOException error){
//    		Log.d("wave1", error.getMessage());
//    	}
    	player.start();
    	Visualizer visualizer = new Visualizer(player.getAudioSessionId());	// cannot initialize Visualizer engine error -1
    	visualizer.setEnabled(false);
    	visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
    	visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
			
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
					int samplingRate) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				updateWaveform(waveform);
			}
			
			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft,
					int samplingRate) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				for (int i = 0; i < fft.length; i++) {
					fft[i] *= 10;
				}
				updateWaveform(fft);				
			}
		},
		Visualizer.getMaxCaptureRate(),
		true, false);	// waveform or fft
    	visualizer.setEnabled(true);
    	
    	waveform = null;
    }

//    void tick(Graphics g, int nX, int nY) {
//        FontMetrics fm = g.getFontMetrics();
//        int h = fm.getAscent(); // �����̍���
//        for (int d = 0; d <= nX; d++) {
//            double x = (double)d / nX;
//            double time = x * data.length / af.getSampleRate();
//            g.drawLine(nx(x), ny(0), nx(x), ny(0)-5);
//            String str = String.format("%.0f", time * 1000);
//            if (d == nX) str += "mS";
//            int w = fm.stringWidth(str);
//            g.drawString(str, nx(x)-w/2, ny(0)+h+2);
//        }
//        for (int n = 0; n <= nY; n++) {
//            double y = (double)n / nY;
//            g.setColor(Color.lightGray);
//            g.drawLine(nx(0), ny(y), nx(1.0), ny(y));
//            String str = String.format("%.0f", vMin + (vMax-vMin)*y);
//            int w = fm.stringWidth(str);
//            g.setColor(Color.black);
//            g.drawString(str, nx(0)-w-5, ny(y)+h/2);
//        }
//    } 

    public void onDraw(Canvas g){
//        width  = getWidth();            // �A�v���b�g�`��̈�̕�
//        height = getHeight();           // �A�v���b�g�`��̈�̍���
//        if (bBorder) g.drawRect(0, 0, width-1, height-1); // �\���̈拫�E���`��
//        width  -= margin.left + margin.right;
//        height -= margin.top  + margin.bottom;
//        g.drawRect(margin.left, margin.top, width-1, height-1); // ���W���`��
//        tick(g, 10, 6);
//        int xLast = 0, yLast = 0;
//        for (int n = 0; n < data.length; n++) {
//            double x = (n + 0.5) / data.length;
//            if (n > 0) g.drawLine(xLast, yLast, nx(x), yVal(data[n]));
//            xLast = nx(x);
//            yLast = yVal(data[n]);
//        }
    	
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		int start_y = 800 - 256;
    	if(waveform != null){
        	for (int i = 0; i < waveform.length; i++) {
    			g.drawLine(i, start_y - waveform[i], i, start_y, paint);
    		}
        	g.drawText(waveform.length + "", 0, 120, paint);
    	}
    	paint.setColor(Color.RED);
    	g.drawLine(0, start_y, 480, start_y, paint);
    
    	invalidate();
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
        }
    }
    
    public void updateWaveform(byte[] waveform){
    	this.waveform = waveform;
    }
    
}


