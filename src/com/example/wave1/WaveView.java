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
	byte[] fft;

	public WaveView(Context context){
		super(context);
		
		init();
	}
	
    public void init() {
//    	try{
    	MediaPlayer player = MediaPlayer.create(getContext(), Uri.parse("/mnt/sdcard/music/you.mp3"));
//    	}catch(IOException error){
//    		Log.d("wave1", error.getMessage());
//    	}
    	player.start();

    	Visualizer visualizer = new Visualizer(player.getAudioSessionId());
    	visualizer.setEnabled(false);
    	visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
    	visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
					int samplingRate) {
				// 波形の形にしている
				for (int i = 0; i < waveform.length; i++) {
					waveform[i] += 128;
				}
				updateWaveform(waveform);
			}
			
			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft,
					int samplingRate) {
				// fft[0]:			直流成分の値（――、すなわち0Hz）
				// fft[2 * i]:		交流成分の実数部（sinみたいな～～）
				// fft[2 * i + 1]:	交流成分の虚数部（cosみたいな～～）
				// ここでは実数部と虚数部を計算済みの値にしている
	    		for (int i = 1; i < fft.length / 2; i++) {
	    			double amplitude = Math.sqrt(Math.pow(fft[i * 2], 2) + Math.pow(fft[i * 2 + 1], 2));
	    			if(amplitude > Byte.MAX_VALUE){
	    				amplitude = Byte.MAX_VALUE;
	    			}
	    			fft[i * 2] = (byte)amplitude;
	    			fft[i * 2 + 1] = (byte)amplitude;
	    		}
				updateFFT(fft);
			}
		},
		Visualizer.getMaxCaptureRate(),
		true, true);	// waveform, fft
    	visualizer.setEnabled(true);
    	
    	waveform = null;
    	fft = null;
    }

    // ウェーブレット変換
    private byte[] invoke(byte[] input_origin){
    	byte[] input = new byte[input_origin.length];
    	for (int i = 0; i < input_origin.length; i++) {
			input[i] = input_origin[i];
		}
        byte[] output = new byte[input.length];
        
        for(int length = input.length >> 1; ; length >>= 1){
            //length=2^n, WITH DECREASING n
            for(int i = 0; i < length; i++) {
                int sum = input[i*2]+input[i*2+1];
                int difference = input[i*2]-input[i*2+1];
                output[i] = (byte)(sum);
                output[length+i] = (byte)(difference * 10);
            }
            if (length == 1) 
                return output;
            
            //Swap arrays to do next iteration
//            System.arraycopy(output, 0, input, 0, length<<1);
        }
    }

    public void onDraw(Canvas g){
		Paint paint = new Paint();
    	if(waveform != null){
    		int zero_y = (int)(getHeight() * 0.3);
    		int wave_width = getWidth();
	        for (int i = 0; i < waveform.length - 1; i++) {
	        	// 線でつなぐ
//	            int x1 = wave_width * i / waveform.length;
//	            int y1 = zero_y + waveform[i];
//	            int x2 = wave_width * (i + 1) / waveform.length;
//	            int y2 = zero_y + waveform[i + 1];
	        	// 棒を並べる
	        	int x1 = wave_width * i / waveform.length;
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - waveform[i];
		        g.drawLine(x1, y1, x2, y2, paint);
	        }
	        g.drawLine(0, zero_y, getWidth(), zero_y, paint);
    	}
		if(fft != null){
			int zero_y = (int)(getHeight() * 0.6);
    		int wave_width = getWidth();
    		for(int i = 1; i < fft.length / 2; i++){
	        	int x1 = wave_width * i / (fft.length / 2);
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - fft[i];
		        g.drawLine(x1, y1, x2, y2, paint);
		        // 1kHzごとに目盛り
		        int samplingrate = 44100;	// TODO: playerから取得する
		        int capturerate = 1024;	// TODO: visualizerから取得する
		        if(i * 2 % (samplingrate / capturerate / 2) == 0){
		        	g.drawLine(x1, zero_y, x2, zero_y + 5, paint);
		        }
    		}
		}
    
    	// 連続して描画する
    	invalidate();
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
        }
    }
    
    public void updateWaveform(byte[] waveform){
    	this.waveform = waveform;
    }
    
    public void updateFFT(byte[] fft){
    	this.fft = fft;
    }
    
}


