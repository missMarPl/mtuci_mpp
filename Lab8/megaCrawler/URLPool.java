import java.util.LinkedList;
import java.io.*;

public class URLPool {
	//глубина
    private int maxDepth;	
	//списки ожидающих и посещённых урлов
    private LinkedList<UrlDepthPair> pendingURLs;
    private LinkedList<UrlDepthPair> visitedURLs;
// еще одно поле, о котором будет написано ниже.
    private int waiting;

    public URLPool(int maxDepth) {
        pendingURLs = new LinkedList<>();
        visitedURLs = new LinkedList<>();
        this.maxDepth = maxDepth;
        waiting = 0;
    }

//получение+удаление пары из пула; поточно-ориентировано
    public synchronized  UrlDepthPair get() throws InterruptedException {
        while (pendingURLs.size() == 0) {
            waiting++;
            wait();
            waiting--;
        }
        return pendingURLs.removeFirst();
    }

	//добавление пары в пул; поточно-ориентировано
    public synchronized void put(UrlDepthPair url) {
        if (url.getDepth() < maxDepth) {
            pendingURLs.addLast(url);
        }
        visitedURLs.add(url);
        notify();
    }

	//проверить, посещён ли адрес
    public synchronized boolean visited(UrlDepthPair url) {
        return visitedURLs.contains(url);
    }

	//получить посещённые
    public synchronized LinkedList<UrlDepthPair> visited() {
        return visitedURLs;
    }

	//получить максимальную глубину
    public synchronized int getMaxDepth() {
        return maxDepth;
    }

	//сколько тредов сейчас юзают пул
    public synchronized int getWaitingThreads() {
        return waiting;
    }

	//вывод посещённых адресов в пуле
		public void getSites() {
		//когда закончили с перебором - вывод всех посещённых адресов и запись их в файл
		
		try(FileWriter writer = new FileWriter("sites.txt", false))
        {
			// запись всей строки
			
			while (!visitedURLs.isEmpty()) {
				String resultURL = visitedURLs.pop().toString();
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
