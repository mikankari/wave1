package com.example.wave1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Visualizer;
import android.view.View;

public class WaveView extends View{

	MediaPlayer player;
	Visualizer visualizer;
	byte[] waveform;
	byte[] fft;

	public WaveView(Context context, MediaPlayer player){
		super(context);
		
		this.player = player;
		init();
	}
	
    public void init() {
    	visualizer = new Visualizer(player.getAudioSessionId());
    	visualizer.setEnabled(false);
    	visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
    	visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
					int samplingRate) {
				// �g�`�̌`�ɂ��Ă���
				for (int i = 0; i < waveform.length; i++) {
					waveform[i] += 128;
				}
				updateWaveform(waveform);
			}
			
			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft,
					int samplingRate) {
				// fft[0]:			���������̒l�i�\�\�A���Ȃ킿0Hz�j
				// fft[1]:			�T���v�����O���g���̔����̎���
				// fft[2 * i]:		�𗬐����̎����isin�݂����ȁ`�`�j
				// fft[2 * i + 1]:	�𗬐����̋����icos�݂����ȁ`�`�j
				// �����ł͎����Ƌ������v�Z�ς݂̒l�ɂ��Ă���
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

    // �E�F�[�u���b�g�ϊ�    
    private byte[] invoke(byte[] input){
    	byte[] output = new byte[input.length];
    	for (int i = input.length / 2; i >= 1; i -= 2) {
			for (int j = 0; j < i; j++) {
				int average = (input[j * 2] + input[j * 2 + 1]) / 2;
				int difference = input[j * 2] - input[j * 2 + 1];
				output[j] = (byte)average;
				output[i + j] = (byte)difference;
			}
		}
    	return output;
    }

    public void onDraw(Canvas g){
		Paint paint = new Paint();
    	if(waveform != null){
    		int zero_y = (int)(getHeight() * 0.25);
    		int wave_width = getWidth();
	        g.drawText("waveform", 0, zero_y - 50, paint);
	        for (int i = 0; i < waveform.length - 1; i++) {
	        	// ���łȂ�
//	            int x1 = wave_width * i / waveform.length;
//	            int y1 = zero_y + waveform[i];
//	            int x2 = wave_width * (i + 1) / waveform.length;
//	            int y2 = zero_y + waveform[i + 1];
	        	// �c�_����ׂ�
	        	int x1 = wave_width * i / waveform.length;
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - waveform[i];
		        g.drawLine(x1, y1, x2, y2, paint);
	        }
	        g.drawLine(0, zero_y, wave_width, zero_y, paint);
	        
	        zero_y = (int)(getHeight() * 0.5);
	        wave_width = getWidth();
	        g.drawText("wavelet", 0, zero_y - 50, paint);
	        byte[] wavelet = invoke(waveform);
	        for (int i = 0; i < wavelet.length / 2; i++) {
				byte b = wavelet[i];
	        	int x1 = wave_width * i / (wavelet.length / 2);
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - wavelet[i];
		        g.drawLine(x1, y1, x2, y2, paint);				
	        }
	        g.drawLine(0, zero_y, wave_width, zero_y, paint);
    	}
		if(fft != null){
			int zero_y = (int)(getHeight() * 0.75);
    		int wave_width = getWidth();
	        g.drawText("FFT", 0, zero_y - 50, paint);
    		for(int i = 1; i < fft.length / 2; i++){
    			int x1 = wave_width * i / (fft.length / 2);
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - fft[i];
		        g.drawLine(x1, y1, x2, y2, paint);
		        // 1kHz���Ƃɖڐ���
		        int samplingrate = visualizer.getSamplingRate();
		        int capturerate = visualizer.getCaptureSize();
		        if(i * 2 % (samplingrate / capturerate / 2) == 0){
		        	g.drawLine(x1, zero_y, x2, zero_y + 5, paint);
		        }
    		}
	        g.drawLine(0, zero_y, wave_width, zero_y, paint);
    		// ���v�l
	        g.drawText("FFT sum", 0, zero_y + 50, paint);
    		int sum = 0;
    		for (int i = 0; i < fft.length / 2; i++) {
    			sum += fft[i * 2];
    		}
    		g.drawLine(0, zero_y + 50, sum / 2, zero_y + 50, paint);
		}
    
    	// �A�����ĕ`�悷��
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


