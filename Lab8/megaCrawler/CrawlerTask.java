import java.io.*;
import java.net.*;
import java.util.regex.*;

public class CrawlerTask implements Runnable {
	/* Для взаимодействия между потоками
		Объявление переменной с ключевым словом volatile отключает для неё такое кэширование и все запросы к переменной будут направляться непосредственно в память
	*/
	private volatile boolean running = true;
	
    private static final String LINK_REGEX = "href=\"(.*?)\"";		
    private static final int WEBPORT = 80;
    private int maxTimeout;
	private static URLPool urlPool;
	
	//конструктор - из пула и таймаута
    public CrawlerTask(URLPool urlPool, int maxTimeout) {
        this.urlPool = urlPool;
        this.maxTimeout = maxTimeout;
	}
	
	//проверка на корректность урла
    public boolean isUrlValid(String url) {
        return url.startsWith(UrlDepthPair.URL_PREFIX);
	}
	
	/*интерфейс Runnable содержит метод run(), который будет выполняться в новом потоке. Поток закончит выполнение, когда завершится его метод run()*/
    public void run() {
        // крутить бесконечно, ожидая поступления урлов в лист ожидания
        while (running) {
            try {
				//получение+удаление пары из пула;
                UrlDepthPair currentUDP = urlPool.get(); 
				//Получение веб-страницы по URL-адресу
                scanPage(currentUDP);
				} catch (IOException e) {
                System.out.println(e);
				//ловим прерывание, меняем running на не выполняется
				} catch (InterruptedException ie) {
                running = false;
			}
		}
	}
	
    //Получение веб-страницы по URL-адресу
	private void scanPage(UrlDepthPair currUDP) throws IOException {
		
		//открываем сокет
		Socket mySocket = null;
		try {
			mySocket = new Socket(currUDP.getWebHost(), WEBPORT);
		}
		catch (UnknownHostException e) {
			System.out.println("UnknownHostException: " + e.getMessage());
			//visitedURLs.add(new UrlDepthPair(currentUDP.getWebHost()+"UNKNOWN", currentUDP.getDepth()));
			return;
		}
		catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());	
		}
		
		// Выставляем таймаут подключения
		try {
			mySocket.setSoTimeout(maxTimeout);
		}
		catch (SocketException ex) {
			System.err.println("SocketException: " + ex.getMessage());
		}
		
		//открываем поток отправки через сокет
		OutputStream out = null;
		try {
			out = mySocket.getOutputStream();
		}
		catch (IOException ioe) {
			System.err.println("IOException:" + ioe.getMessage());
		}
		
		//отправляем HTTP запрос с  DocPath и WebHost из currUDP			
		PrintWriter myWriter = new PrintWriter(out, true);
		String docPath = currUDP.getDepth() == 0 ? "/" : currUDP.getDocPath();
		
        myWriter.println("GET " + docPath + " HTTP/1.1");
        myWriter.println("Host: " + currUDP.getWebHost());
        myWriter.println("Connection: close");
        myWriter.println();
		
		//открываем поток на приём через сокет
		InputStream in = null;
		try {
			in = mySocket.getInputStream();
		}
		catch (IOException ioe) {
			System.err.println("IOException:" + ioe.getMessage());
		}
		
		InputStreamReader myStreamReader = new InputStreamReader(in);
		BufferedReader myReader = new BufferedReader(myStreamReader);
		
        while (true) {
            String line = null;
			//каждую считанную строку кладём в line 
			try {
				line = myReader.readLine();
				//DEBUG
				//	System.out.println("line = "+line);
			}
			catch (IOException ioe) {
				System.err.println("IOException:" + ioe.getMessage());	
			}
			
			if (line == null) break;
			
			//ищем в строке по паттерну из LINK_REGEX
            Pattern p = Pattern.compile(LINK_REGEX);
            Matcher m = p.matcher(line);
			//для каждого найденного вхождения паттерна
            while (m.find()) {
				/* Метод group () класса Matcher используется для получения входной подпоследовательности, сопоставленной с предыдущим результатом сопоставления. */
                String url = m.group(1);
				
                if (isUrlValid(url)) {
                    UrlDepthPair newUDP = new UrlDepthPair(new URL(url), currUDP.getDepth() + 1);
					
					// Проверяем: если урла нет в списке посещённых, то добавляем в пул.
                    if (!urlPool.visited(newUDP)) {
                        int depth = currUDP.getDepth();
                        if (depth < urlPool.getMaxDepth()) {
                            urlPool.put(newUDP);
						}
					}
				}
			}
		}
		// закрываем сокет!
		try {
			mySocket.close();
		}
		catch (IOException ioe) {
			System.err.println("IOException:" + ioe.getMessage());
		}
	}
}
