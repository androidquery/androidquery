package com.androidquery.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;

import com.androidquery.util.AQUtility;


public class PatternUtility {

	public static String makeTagPattern(String tagName, String attName, String attValue){
		//<meta [^/>]*name=\"viewport\"[^/>]*/>
		//TODO hacking now, only look at value
		return "<" + tagName + " [^/>]*" + attValue + "\"[^/>]*>";
	}
	
	public static Map<String, String> toAttributes(String tag){
		
		Map<String, String> result = new HashMap<String, String>();
		
		if(tag == null) return result;
		
		String reg = "[\\w]+[\\s]*=[\\s]*\"[^\"]+\"";
		
		Pattern p = Pattern.compile(reg);
		
		Matcher m = p.matcher(tag);
	
		
		while(m.find()){
			String g = m.group();
			int index = g.indexOf('=');
			if(index > 0){
				String key = g.substring(0, index).trim();
				String value = g.substring(index + 1, g.length()).replace("\"", "").trim();
				result.put(key, value);
			}
		}
		
		return result;
		
	}
	
	public static Map<String, String> splitQuery(String query, String delim){
		
		Map<String, String> result = new HashMap<String, String>();
		
		String[] pairs = query.split(delim);
		
		for(String g: pairs){
			
			int index = g.indexOf('=');
			if(index > 0){
				String key = g.substring(0, index).trim();
				String value = g.substring(index + 1, g.length()).trim();
				result.put(key, value);
			}
			
		}
		
		return result;
		
	}
		
	public static String transform(String html, String match, Transformer tran){
		
		Pattern p = Pattern.compile(match);
		
		Matcher m = p.matcher(html);
	
		StringBuffer sb = new StringBuffer();
		
		boolean found = false;
		
		while(m.find()){
			
			found = true;
			
			String g = m.group();	
			
			try{
				g = tran.transform(g);
			}catch(Exception e){	
				AQUtility.debug(e);
			}
			
			m.appendReplacement(sb, g);
			
		}
		
		if(!found){
			return html;
		}
		
		m.appendTail(sb);		
		return sb.toString();
		
	}
	
	public static String match(String html, String match){
		Pattern p = Pattern.compile(match);		
		Matcher m = p.matcher(html);
		if(m.find()){
			return m.group();
		}
		return null;
	}
	
	
	
	private static final String YT_IMG = "<div style=\"position:relative;width:480px;height:360px;\"><a href=\"http://www.youtube.com/watch?v=@key\"><div style=\"position:absolute;top:50%;left:50%;opacity:0.5;height:55px;width:78px;margin-left:-39px;margin-top:-27px;background: url(http://s.ytimg.com/yt/m/cssbin/mobile-blazer-sprite-low-vflu9v2ct.png) no-repeat 0 0;background-position: -102px -51px;\"></div><img src=\"http://i.ytimg.com/vi/@key/hqdefault.jpg?w=480\" /></a></div>";
	//private static final String YT_IMG = "<div style=\"position:relative;display:block;\"><a href=\"http://www.youtube.com/watch?v=@key\"><div style=\"position:absolute;top:50%;left:50%;opacity:0.5;height:55px;width:78px;margin-left:-39px;margin-top:-27px;background: url(http://s.ytimg.com/yt/m/cssbin/mobile-blazer-sprite-low-vflu9v2ct.png) no-repeat 0 0;background-position: -102px -51px;\"></div><img src=\"http://i.ytimg.com/vi/@key/hqdefault.jpg?w=480\" /></a></div>";
	
	public static String replaceYoutube(String html){
		
		
		String match = "<iframe[^>].*src=\"http://www.youtube.com/embed/[^>].*/iframe>";
		
		String result = transform(html, match, new Transformer() {
			
			@Override
			public String transform(String match) {
				
				String key = extractYoutubeKey(match);
				
				//String img = makeYoutubeImg(key);
				//return "<a href=\"http://www.youtube.com/watch?v=" + key + "\"><img src=\""+ img +"\"/></a>";
			
				String result = YT_IMG.replaceAll("@key", key);
				
				return result;
			}
		});
		
		
		return result;
	}
	
	
	public static String makeYoutubeImg(String key){
		if(key == null) return null;
		return "http://i4.ytimg.com/vi/" + key +"/hqdefault.jpg";
	}
	
	private static String extractYoutubeKey(String tag){
		String src = extractAtt(tag, "src");
		int index = src.indexOf("embed/");
		
		String key = src.substring(index + 6);
		
		key = key.split("\\?")[0];
		
		return key;
	}
	
	//12-14 01:51:58.370: W/AQuery(17423): 247236:http://www.youtube.com/watch?v=YgSPaXgAdzE&feature=youtube_gdata_player

	public static String extractYoutubeUrlKey(String url){
		
		String key = null;
		
		try{
			if(!url.contains("youtube.com")){
				return null;
			}
			Uri uri = Uri.parse(url);
			
			key = uri.getQueryParameter("v");
			if(key == null){	
				List<String> paths = uri.getPathSegments();			
				key = paths.get(paths.size() - 1);
			}
			
		}catch(Exception e){
			AQUtility.report(e);
		}
		
		return key;
	}
	
	public static String extractAtt(String tag, String name){
		Map<String, String> atts = toAttributes(tag);
		return atts.get(name);
	}
	
	public interface Transformer{
		
		public String transform(String match);
		
	}
}
