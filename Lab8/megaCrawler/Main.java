import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Функция main должна выполнять следующие задачи
public class Main {

	//список потоков
    private static List<Thread> threadList = new ArrayList<>();

    public static void main(String[] args) {
        int maxDepth = 0;
		        int maxTimeout = 0;
        int numberOfThreads = 0;

	// Обработать аргументы командной строки. Сообщить пользователю олюбых ошибках ввода.

//проверяем входные параметры
		if (args.length != 4) {
            System.out.println("Usage: java Main <URL> <depth> <timeout(ms)> <threads>");
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
		
				//проверяем, что число потоков ОК
		try {
            numberOfThreads = Integer.parseInt(args[3]);
		}
        catch (NumberFormatException ex) {
            System.out.println("numberOfThreads must be an integer.");
            System.exit(1);
		}

		// Создать экземпляр пула URL-адресов
        URLPool urlPool = new URLPool(maxDepth);

		// поместить указанный пользователем URL-адрес в пул с глубиной 0
            urlPool.put(new UrlDepthPair(args[0], 0));

        //  Создать указанное пользователем количество задач (и потоков для их выполнения) для веб-сканера. 
        for (int i = 0; i < numberOfThreads; i++) {
		//Каждой задаче поискового робота нужно датьссылку на созданный пул URL-адресов.
            CrawlerTask c = new CrawlerTask(urlPool, maxTimeout);
            Thread t = new Thread(c);
            threadList.add(t);
            t.start();
        }

        while (urlPool.getWaitingThreads() != numberOfThreads) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                System.out.println("ignore Interrupted Exception");
            }
        }


		//вывести на экран и в файл посещённые ссылки
        urlPool.getSites();

        //System.exit(0);
        // Прервать подвисшие потоки
        threadList.stream().forEach(Thread::interrupt);
    }
}
