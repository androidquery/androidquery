package com.androidquery.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class AQueryImageTest extends AbstractTest<AQueryTestActivity> {

	@Override
	protected void setUp() throws Exception {
        super.setUp();
        waitSec();
    }
	
	public AQueryImageTest() {		
		super(AQueryTestActivity.class);
    }
	
	
	
	@UiThreadTest
	public void testImageResourceId() {
		
		aq.id(R.id.image).image(R.drawable.icon);
        
		assertLoaded(aq.getImageView(), true);
		
    }
	
	
	@UiThreadTest
	public void testImageDrawable() {
		
		Drawable d = getActivity().getResources().getDrawable(R.drawable.icon);
		
		assertNotNull(d);
		
		aq.id(R.id.image).image(d);
        
		assertLoaded(aq.getImageView(), true);
		
    }
	
	@UiThreadTest
	public void testImageBitmap() {
		
		Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.icon);
		
		assertNotNull(bm);
		
		aq.id(R.id.image).image(bm);
        
		assertLoaded(aq.getImageView(), true);
		
    }
	
	
	public void testImageBasicUrl() {
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(ICON_URL);
			}
		});
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		Bitmap bm = aq.getCachedImage(ICON_URL);
		
		assertNotNull(bm);
		
		File file = aq.getCachedFile(ICON_URL);
		assertNotNull(file);
		
    }
	
	public void testImageNotSetBeforeNetwork() {
		
		clearCache();
		aq.id(R.id.image);
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.image(ICON_URL);
			}
		});
		
		assertLoaded(aq.getImageView(), false);
		
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNull(bm);
		
		waitAsync(2000);
    }
	
	public void testImageNoCache() {
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(ICON_URL, false, false);
			}
		});
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNull(bm);
		
		File file = aq.getCachedFile(ICON_URL);
		assertNull(file);
		
    }
	
	//Test: public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId)
	public void testImageDownSample() {
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(LAND_URL, true, true, 200, 0);
			}
		});
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		Bitmap bm = aq.getCachedImage(LAND_URL, 200);		
		assertNotNull(bm);
		
		assertTrue(bm.getWidth() < 400);
		
    }
	
	public void testImageFallback() {
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(INVALID_URL, true, true, 200, R.drawable.icon);
			}
		});
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
    }
	
	private void assertLoaded(ImageView imageview, boolean loaded){
		
		Drawable d = imageview.getDrawable();
		
		int width = 0;
		
		if(d != null){
			width = d.getIntrinsicWidth();
		}
		
		
		if(loaded){
			assertTrue(width > 0);
		}else{
			assertTrue(width <= 0);
		}
		
	}
	
	public void testImageFetchFailed() {
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(INVALID_URL, true, true, 200, 0);
			}
		});
		
		waitAsync(2000);
		
		
		assertLoaded(aq.getImageView(), false);
		
    }	
	
	//Test: public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId, Bitmap preset, int animId)
	public void testImagePreset() {
		
		clearCache();
		
		final Bitmap thumb = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.icon);	
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				
				aq.id(R.id.image).image(ICON_URL, true, true, 0, 0, thumb, 0);
				assertLoaded(aq.getImageView(), true);
			}
		});
		
		
		waitAsync(2000);
		
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNotNull(bm);
		
    }	
	
	public void testImageAnimation() {
		
		clearCache();
		
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
							
				aq.id(R.id.image).image(ICON_URL, true, true, 0, 0, null, AQuery.FADE_IN);
			}
		});
		
		
		waitAsync(2000);
		
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNotNull(bm);
		
    }	
	
	public void testImageByCallback() {
		
		clearCache();
		
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
							
				//aq.id(R.id.image).image(ICON_URL, true, true, 0, 0, null, AQuery.FADE_IN);
				BitmapAjaxCallback cb = new BitmapAjaxCallback();
				cb.url(ICON_URL);
				
				aq.id(R.id.image).image(cb);
			}
		});
		
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNotNull(bm);
		
    }	
	
	public void testImageByCallbackAsync() {
		
		clearCache();
		
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
							
				//aq.id(R.id.image).image(ICON_URL, true, true, 0, 0, null, AQuery.FADE_IN);
				BitmapAjaxCallback cb = new BitmapAjaxCallback();
				cb.url(ICON_URL);
				
				//aq.id(R.id.image).image(cb);
				ImageView iv = aq.id(R.id.image).getImageView();
				cb.imageView(iv).async(getActivity());
				
			}
		});
		
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNotNull(bm);
		
    }	
	
	public void testImageByCallback2() {
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
							
				BitmapAjaxCallback cb = new BitmapAjaxCallback(){
				
					protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
					
						iv.setImageBitmap(bm);
						
					}
					
				};
				
				aq.id(R.id.image).image(ICON_URL, true, true, 0, 0, cb);
			}
		});
		
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNotNull(bm);
		
    }		
	
	private void prefetchFile(){
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(LAND_URL, true, true, 200, 0);
			}
		});
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		waitSec();
		
		File file = aq.getCachedFile(LAND_URL);
		
		assertNotNull(file);
	}
	
	public void testImageFileDownsample() {
		
		clearCache();
		
		prefetchFile();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				File file = aq.getCachedFile(LAND_URL);
				aq.id(R.id.image2).image(file, 200);
			}
		});
		
		assertLoaded(aq.getImageView(), true);
	
		assertTrue(aq.getImageView().getDrawable().getIntrinsicWidth() < 400);
		
    }	
	
	public void testImageFileWithCallback() {
		
		clearCache();
		
		prefetchFile();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				File file = aq.getCachedFile(LAND_URL);
				aq.id(R.id.image2).image(file, true, 200, new BitmapAjaxCallback(){
					@Override
					protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
						iv.setImageBitmap(bm);
						assertNotNull(bm);
					}
				});
			}
		});
		
		assertLoaded(aq.getImageView(), true);
		
    }	
	
	
	public void testImageRatio() {
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				
				aq.id(R.id.image).image(ICON_URL, true, true, 0, 0, null, 0, AQuery.RATIO_PRESERVE);
			}
		});
		
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		Bitmap bm = aq.getCachedImage(ICON_URL);		
		assertNotNull(bm);
		
    }	

	public void testImageFileUrl() {
		
		clearCache();
		
		AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(ICON_URL).type(File.class);		
		
        aq.sync(cb);
		
        final File file = cb.getResult();
        
        assertNotNull(file);   
		
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(file.getAbsolutePath());
			}
		});
		
		waitAsync(2000);
		
		assertLoaded(aq.getImageView(), true);
		
		Bitmap bm = aq.getCachedImage(file.getAbsolutePath());
		
		assertNotNull(bm);
		
		AQUtility.debug(bm.getWidth());
		
    }
	
	public void testCachedImageWithRecycle() {
		
		clearCache();
		
		final AQuery listAq = new AQuery(this.getActivity());
		final View view = aq.id(R.id.image).getView();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				
				AQuery aq = listAq.recycle(view);
				
				Bitmap bm = aq.getCachedImage(R.drawable.icon);
				assertNotNull(bm);
				
				
			}
		});
		
		
		waitAsync(2000);
		
		
    }	
	
	/*
	public void testIfModified() {
		
		String url = ICON_URL;
		
		AjaxCallback<Bitmap> cb = new AjaxCallback<Bitmap>();
		cb.type(Bitmap.class).url(url);
		
		aq.sync(cb);
		
		Bitmap bm = cb.getResult();
		AjaxStatus status = cb.getStatus();
		
		assertNotNull(bm);
		
		assertEquals(304, status.getCode());
		
		File file;
		
		
	}
	*/
	
	public void testAutoRotate() throws IOException{
		
		
		String imageUrl = "http://res.dbkon.co.kr/resource/201302091360376386575001.jpg";            
		
		BitmapAjaxCallback cb = new BitmapAjaxCallback();
		cb.url(imageUrl).targetWidth(300).rotate(true);
		
		aq.id(R.id.image).image(cb);
		
		cb.block();
		
		Bitmap bm = cb.getResult();
		AjaxStatus status = cb.getStatus();
		
		assertNotNull(bm);
		
	}
	
	public void testMalformedImage() {
        
        clearCache();
        
        final String badUrl = "http://www.google.com?test=hello";
        
        AQUtility.post(new Runnable() {
            
            @Override
            public void run() {
                aq.id(R.id.image).image(badUrl);
            }
        });
        
        waitAsync(2000);
        
        //assertLoaded(aq.getImageView(), true);
        
        Bitmap bm = aq.getCachedImage(badUrl);
        
        assertNull(bm);
        
        File file = aq.getCachedFile(badUrl);
        assertNull(file);
        
    }
	
	private Bitmap bmResult;
	
	public void testImageIOError() {
        
        clearCache();
        
        AQUtility.cleanCache(AQUtility.getCacheDir(getActivity()), 0, 0);
             
        File file = aq.getCachedFile(LAND_URL);
        
        assertNull(file);
        
        AQUtility.TEST_IO_EXCEPTION = true;
        
        AQUtility.post(new Runnable() {
            
            @Override
            public void run() {
                            
                BitmapAjaxCallback cb = new BitmapAjaxCallback(){
                
                    protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    
                        AQUtility.debug("bm", bm);
                        
                        //iv.setImageBitmap(bm);
                        bmResult = bm;
                    }
                    
                };
                
                aq.id(R.id.image).image(LAND_URL, true, true, 0, 0, cb);
            }
        });
        
        
        waitAsync(2000);
        
        assertNull(bmResult);
        
        File file2 = aq.getCachedFile(LAND_URL);
        
        if(file2 != null){
            AQUtility.debug("file length", file2.length());
        }
        
        assertNull(file2);
        
    }   
	
}
