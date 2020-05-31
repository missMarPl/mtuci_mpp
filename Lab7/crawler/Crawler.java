import java.io.*;
import java.net.*;
import java.util.*;


public class Crawler {
	//начало ссылки
	public static final String LINK_PREFIX = "a href=\"";
	//конец ссылки
	public static final String END_URL = "\"";
    public static final int WEBPORT = 80;
	
	public static void main(String[] args) {
		int maxDepth = 0;
		int maxTimeout = 0;
		//создаём списки для ожидающих и для посещённых URLов
		LinkedList<UrlDepthPair> pendingURLs = new LinkedList<UrlDepthPair>();
		LinkedList<UrlDepthPair> visitedURLs = new LinkedList<UrlDepthPair>();
		
		//проверяем входные параметры
		if (args.length != 3) {
            System.out.println("Usage: java Crawler <URL> <depth> <timeout(ms)>");
            System.exit(1);
		}
		
        //проверяем, что глубина ОК
        try {
            maxDepth = Integer.parseInt(args[1]);
		}
        catch (NumberFormatException ex) {
            System.out.println("Depth must be an integer.");
            System.exit(1);
		}
		
		//проверяем, что таймаут ОК
		try {
            maxTimeout = Integer.parseInt(args[2]);
		}
        catch (NumberFormatException ex) {
            System.out.println("Timeout must be an integer.");
            System.exit(1);
		}
		
		// Создаём пару из начального адреса (конструктор принимает string на входе)
        UrlDepthPair currentUDP = new UrlDepthPair(args[0], 0);
		
		//Закидываем в лист ожидания
		pendingURLs.add(currentUDP);
		
		// Дальше крутим цикл, пока не дойдём до максимальной глубины
		for (int currDepth = 0; currDepth <= maxDepth; currDepth++) {
			//DEBUG
			//System.out.println("currDepth = "+currDepth);
			
			//сколько в листе ожидания
			int pendingLeft = pendingURLs.size();
			
			//DEBUG
			//System.out.println("At Depth "+currDepth+" pendingLeft = "+pendingLeft);
			
			// Перебираем все урлы в листе ожидания 
			for (int i = 0; i < pendingLeft; i++) {
				
				//DEBUG
				/*System.out.println("visitedURLs = "+visitedURLs.size());	
					try {
					java.util.concurrent.TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				
				//берём урл из листа ожидания
				currentUDP = pendingURLs.pop();
				
				//открываем сокет
				Socket mySocket = null;
				try {
					mySocket = new Socket(currentUDP.getWebHost(), WEBPORT);
				}
				catch (UnknownHostException e) {
					System.out.println("UnknownHostException: " + e.getMessage());
					visitedURLs.add(new UrlDepthPair(currentUDP.getWebHost()+"UNKNOWN", currentUDP.getDepth()));
					continue;
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
				
				//отправляем HTTP запрос с  DocPath и WebHost из currentUDP
				
				//Ругается BadRequest'ом на getDocPath на нулевом уровне? Ок, вроде починила.				
				PrintWriter myWriter = new PrintWriter(out, true);
				String docPath = currentUDP.getDepth() == 0 ? "/" : currentUDP.getDocPath();
				
				//DEBUG
				//System.out.println("docPath: " + docPath);
				
				myWriter.println("GET " + docPath + " HTTP/1.1");
				myWriter.println("Host: " + currentUDP.getWebHost());
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
				
				//DEBUG
				//int debugNum = 0;
				
				while (true) {
					String line = null;
					
					//DEBUG
					//debugNum++;
					//System.out.println("debugNum = "+debugNum);
					//	System.out.println("visitedURLs = "+visitedURLs.size());
					
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
					//индексы для начала и конца ссылки
					//int Idx = 0;
					int beginIdx = 0;
					int endIdx = 0;
					
					while (true) {					
						beginIdx = line.indexOf(LINK_PREFIX, beginIdx);
						//	System.out.println("line in = "+line);
						//бежим по строке, пока она не кончится
						//	System.out.println("Idx = "+Idx);
						if (beginIdx == -1) {
							//System.out.println("Idx -1 = "+Idx);
							break;
						}
						
						beginIdx = beginIdx+LINK_PREFIX.length();
						//beginIdx = Idx;
						endIdx = line.indexOf(END_URL, beginIdx);
						//Idx = endIdx;
						
						//DEBUG
						//System.out.println("Indexes = "+Idx+" : "+beginIdx+": "+endIdx);
						
						//нашли ссылку - кладём в лист ожидания с глубиной +1
						String newURL ="";
						
						//если конец строки наступил раньше, чем кончилась ссылка
						try {
							newURL = line.substring(beginIdx, endIdx);
							} catch (IndexOutOfBoundsException iob) {
							System.out.println("OutOfBounds");
							break;
						}
						//DEBUG
						//System.out.println("newURL = "+newURL);
						
						if (newURL.startsWith(UrlDepthPair.URL_PREFIX)) {
							UrlDepthPair newUDP = new UrlDepthPair(newURL, currDepth + 1);
							pendingURLs.add(newUDP);
						}
					}	
				}
				
				// отработанную страницу кладём в список посещённых
				visitedURLs.add(currentUDP);
				
				// закрываем сокет!
				try {
					mySocket.close();
				}
				catch (IOException ioe) {
					System.err.println("IOException:" + ioe.getMessage());
				}
			}
		}
		
		Crawler cr = new Crawler();
		cr.getSites(visitedURLs);
		
	}
	
	public void getSites(LinkedList<UrlDepthPair> visited) {
		//когда закончили с перебором - вывод всех посещённых адресов и запись их в файл
		
		try(FileWriter writer = new FileWriter("sites.txt", false))
        {
			// запись всей строки
			
			while (!visited.isEmpty()) {
				String resultURL = visited.pop().toString();
				System.out.println(resultURL);
				writer.write(resultURL);
			}             
            writer.flush();
		}
        catch(IOException ex){
            System.out.println(ex.getMessage());
		} 
		
	}	
}