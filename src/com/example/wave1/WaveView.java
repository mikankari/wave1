package com.example.wave1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class WaveView extends View{

	MediaPlayer player;
	Visualizer visualizer;
	byte[] waveform;
	byte[] waveform1000ms;
	int waveform1000ms_index;
	byte[] wavelet;
	int bpm;
	byte[] fft;
	boolean isupdate;

	public WaveView(Context context, MediaPlayer player){
		super(context);
		
		this.player = player;
    	visualizer = new Visualizer(player.getAudioSessionId());
    	visualizer.setEnabled(false);
    	visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
    	visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
				// 波形の形にしている
				for (int i = 0; i < waveform.length; i++) {
					waveform[i] += 128;
				}
				updateWaveform(waveform);

				// ウェーブレット解析結果生成
				if(waveform1000ms_index >= waveform1000ms.length){
					int repetition = 6;
					byte[] wavelet = invoke(waveform1000ms);
					for (int i = 1; i < repetition; i++) {
						wavelet = invoke(wavelet);
					}
					int duration_max = 0;
					int iszeroindex_old = -1;
					for (int i = 0; i < wavelet.length; i++) {
						if(wavelet[i] != 0){
							if(iszeroindex_old != -1){
								int duration = i - iszeroindex_old;
								if(duration_max < duration){
									duration_max = duration;									
								}								
							}
							iszeroindex_old = i;								
						}
					}
					if(duration_max != 0){
						double msper1sample = 1000.0 / (visualizer.getSamplingRate() / 1000);
						double ms = duration_max * Math.pow(2, repetition) * msper1sample;
						bpm = (int)(60000 / ms);
						if(bpm > 500){
							bpm /= 2;
						}else if(bpm < 50){
							bpm *= 2;
						}						
					}else{
						bpm = 0;
					}
					updateWavelet(wavelet);					
				}
			}
			
			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
				// fft[0]:			直流成分の値（――、すなわち0Hz）
				// fft[1]:			サンプリング周波数の半分の実部
				// fft[2 * i]:		交流成分の実部（sinみたいな～～）
				// fft[2 * i + 1]:	交流成分の虚部（cosみたいな～～）
				// ここでは実部と虚部を計算済みの値にしている
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
    	waveform1000ms = null;
    	waveform1000ms_index = -1;
    	wavelet = null;
    	bpm = -1;
    	fft = null;
    	isupdate = true;
    }

    // ウェーブレット変換    
    private byte[] invoke(byte[] input){
    	// Haar Wevelet
		byte[] output = new byte[input.length / 2];
		byte[] outputw = new byte[input.length / 2];
//		for (int j = 0; j < input.length / 2 - 1; j++) {
//			int average = (input[j * 2] + input[j * 2 + 1]) / 2;
//			output[j] = (byte)average;
//			int difference = (input[j * 2] - input[j * 2 + 1]);
//			outputw[j] = (byte)difference;
//		}
		// Daubechie Wavelet
		byte[] output = new byte[input.length / 2];
		byte[] outputw = new byte[input.length / 2];
//		double[] daubechiep = {0.02667005790055555358661744877,
//				  0.18817680007769148902089297368,
//				  0.52720118893172558648174482796,
//				  0.68845903945360356574187178255,
//				  0.28117234366057746074872699845,
//				  -0.24984642432731537941610189792,
//				  -0.19594627437737704350429925432,
//				  0.12736934033579326008267723320,
//				  0.09305736460357235116035228984,
//				  -0.07139414716639708714533609308,
//				  -0.02945753682187581285828323760,
//				  0.03321267405934100173976365318,
//				  0.00360655356695616965542329142,
//				  -0.01073317548333057504431811411,
//				  0.00139535174705290116578931845,
//				  0.00199240529518505611715874224,
//				  -0.00068585669495971162656137098,
//				  -0.00011646685512928545095148097,
//				  0.00009358867032006959133405013,
//				  -0.00001326420289452124481243668};
		double[] daubechiep = {0.707106781, 0.707106781};
//		double[] daubechieq = {-0.00001326420289452124481243668,
//				  -0.00009358867032006959133405013,
//				  -0.00011646685512928545095148097,
//				  0.00068585669495971162656137098,
//				  0.00199240529518505611715874224,
//				  -0.00139535174705290116578931845,
//				  -0.01073317548333057504431811411,
//				  -0.00360655356695616965542329142,
//				  0.03321267405934100173976365318,
//				  0.02945753682187581285828323760,
//				  -0.07139414716639708714533609308,
//				  -0.09305736460357235116035228984,
//				  0.12736934033579326008267723320,
//				  0.19594627437737704350429925432,
//				  -0.24984642432731537941610189792,
//				  -0.28117234366057746074872699845,
//				  0.68845903945360356574187178255,
//				  -0.52720118893172558648174482796,
//				  0.18817680007769148902089297368,
//				  -0.02667005790055555358661744877};
		double[] daubechieq = {0.707106781, -0.707106781};
		for (int i = 0; i < input.length / 2; i++) {
			output[i] = 0;
			outputw[i] = 0;
			for (int j = 0; j < daubechiep.length; j++) {
				int index = (j + 2 * i) % input.length;
				output[i] += daubechiep[j] * input[index];
				outputw[i] += daubechieq[j] * input[index];
			}
		}
    	return output;
    }

    public void onDraw(Canvas g){
		Paint paint = new Paint();
		if(!isupdate){
			g.drawText("update pausing", 0, 20, paint);
		}
    	if(waveform != null){
    		int zero_y = (int)(getHeight() * 0.25);
    		int wave_width = getWidth();
	        g.drawText("waveform " + visualizer.getCaptureSize(), 0, zero_y - 64, paint);
	        for (int i = 0; i < waveform.length - 1; i++) {
	        	// 線でつなぐ
//	            int x1 = wave_width * i / waveform.length;
//	            int y1 = zero_y + waveform[i];
//	            int x2 = wave_width * (i + 1) / waveform.length;
//	            int y2 = zero_y + waveform[i + 1];
	        	// 縦棒を並べる
	        	int x1 = wave_width * i / waveform.length;
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - waveform[i];
		        g.drawLine(x1, y1, x2, y2, paint);
	        }
	        g.drawLine(0, zero_y, wave_width, zero_y, paint);
    	}
    	if(wavelet != null && waveform1000ms != null){
	        int zero_y = (int)(getHeight() * 0.5);
	        int wave_width = getWidth();
	        g.drawText("wavelet " + waveform1000ms_index + " / " + waveform1000ms.length, 0, zero_y - 64, paint);
	        for (int i = 0; i < wavelet.length; i++) {
	        	int x1 = wave_width * i / wavelet.length;
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - wavelet[i];
		        g.drawLine(x1, y1, x2, y2, paint);				
	        }
	        g.drawLine(0, zero_y, wave_width, zero_y, paint);
	        g.drawText("BPM: " + bpm, 0, zero_y + 64, paint);
    	}
		if(fft != null){
			int zero_y = (int)(getHeight() * 0.75);
    		int wave_width = getWidth();
	        g.drawText("FFT " + visualizer.getCaptureSize() / 2, 0, zero_y - 64, paint);
    		for(int i = 1; i < fft.length / 2; i++){
    			int x1 = wave_width * i / (fft.length / 2);
	        	int y1 = zero_y;
	        	int x2 = x1;
	        	int y2 = zero_y - fft[i * 2];
		        g.drawLine(x1, y1, x2, y2, paint);
		        // 1kHzごとに目盛り
		        int samplingrate = visualizer.getSamplingRate() / 1000;
		        int capturerate = visualizer.getCaptureSize();
		        if(i % (samplingrate / capturerate / 2) == 0){
		        	g.drawLine(x1, zero_y, x2, zero_y + 5, paint);
		        }
    		}
	        g.drawLine(0, zero_y, wave_width, zero_y, paint);
		}
    
    	// 連続して描画する
		invalidate();
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
        }
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
        	isupdate = !isupdate;
        	visualizer.setEnabled(isupdate);
        }

        return true;
    }

    public void updateWaveform(byte[] waveform){
    	if(isupdate){
        	this.waveform = waveform;
        	if(waveform1000ms_index >= 0 && waveform1000ms_index < waveform1000ms.length){
            	for (int i = 0; i < waveform.length && waveform1000ms_index + i < waveform1000ms.length; i++) {
        			waveform1000ms[waveform1000ms_index + i] = waveform[i];
        		}
            	waveform1000ms_index += waveform.length;
        	}else{
        		waveform1000ms = new byte[visualizer.getSamplingRate() / 1000];
        		waveform1000ms_index = 0;
        	}
    	}
    }
    
    public void updateWavelet(byte[] wavelet){
    	if(isupdate){
        	this.wavelet = wavelet;    		
    	}
    }
    
    public void updateFFT(byte[] fft){
    	if(isupdate){
        	this.fft = fft;    		
    	}
    }
    
}


