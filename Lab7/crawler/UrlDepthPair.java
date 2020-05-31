
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;


public class UrlDepthPair {
    private URL url;
    private int depth;
    public static final String URL_PREFIX = "http://";
	
	//конструктор из URL	
    public UrlDepthPair(URL url, int depth) {
        this.url = url;
        this.depth = depth;
	}
	
	//конструктор из String
	public UrlDepthPair(String url, int depth) {
		try{
			this.url = new URL(url);
			} catch(MalformedURLException ex){
			System.out.println("ERROR in URL");
		}
        this.depth = depth;
	}
	
	//получить глубину пары
    public int getDepth() {
        return depth;
	}
	
	//получить адрес пары (преобразовать в выдачу toString, чтобы не тащить URL формат во внешние функции???)
    public URL getUrl() {
        return url;
	}
	
	//Выдать инфу о паре в одну красивую строчку
    @Override
    public String toString() {
        return "url: " + url + " search depth: " + depth;
	}
	
    public String getDocPath() {
	System.out.println("path "+url.getPath());
        return url.getPath();
	}
	
	public String getWebHost() {
	System.out.println("host "+url.getHost());
        return url.getHost();
	}
	/*   public String getWebHost() {
		
        int idx = URL_PREFIX.length();
        int idx2 = url.indexOf("/", idx);
		
        try {
		if (isUrlValid()) {
		return url.substring(idx, idx2);
		}
        } catch (IndexOutOfBoundsException iob) {
		System.out.println("wrapped url");
        }
        return null;
	} */
	
	
	
	/*    public boolean isUrlValid() {
        return url.startsWith(URL_PREFIX);
	}*/
	
}