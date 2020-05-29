import java.util.*;
/**
	* This class stores the basic state necessary for the A* algorithm to compute a
	* path across a map.  This state includes a collection of "open waypoints" and
	* another collection of "closed waypoints."  In addition, this class provides
	* the basic operations that the A* pathfinding algorithm needs to perform its
	* processing.
**/
public class AStarState
{
	/**Создание карт открытых и закрытых точек  **/
    private Map<Location, Waypoint> openWaypoints = new HashMap<Location, Waypoint> ();
    private Map<Location, Waypoint> closedWaypoints = new HashMap<Location, Waypoint> ();
    
    /** This is a reference to the map that the A* algorithm is navigating. **/
    private Map2D map;
	
    /**
		* Initialize a new state object for the A* pathfinding algorithm to use.
	**/
    public AStarState(Map2D map)
    {
        if (map == null)
		throw new NullPointerException("map cannot be null");
		
        this.map = map;
	}
	
    /** Returns the map that the A* pathfinder is navigating. **/
    public Map2D getMap()
    {
        return map;
	}
	
    /**
		* Эта функция должна проверить все вершины в наборе открытых вершин, 
		* и  после  этого она должна  вернуть  ссылку  на  вершину  с  наименьшей  общей 
		*стоимостью.  Если  в  "открытом"  наборе  нет  вершин,  функция  возвращает NULL. 
	**/
    public Waypoint getMinOpenWaypoint()
    {
		/** Если  в  "открытом"  наборе  нет  вершин,  функция  возвращает NULL.**/
        if (openWaypoints.size() == 0) {
			return null; 		
			} else {
			/**Самая подходящая вершина и её стоимость**/
			Waypoint best = null;
			float bestСost = Float.MAX_VALUE;  
			
			/** Перебираем все вершины в наборе**/
			for (Waypoint openWay : openWaypoints.values()) {
				//берём общую стоимость вершины
				float wpTotalCost = openWay.getTotalCost();
				/** если стоимость меньше текущей лучшей - заменяем лучшую вершину текущей**/
				if (wpTotalCost < bestСost)
				{
					best = openWay;
					bestСost = wpTotalCost;
					System.out.printf("Best cost : %f  \n", bestСost);
				}       
			}
			
			return best;
		}
	}
	
	/**
		Вот что должен делать этот метод: 
		  Если в наборе «открытых вершин» в настоящее время нет вершины 
		для данного местоположения, то необходимо просто добавить новую вершину. 
		  Если  в  наборе  «открытых  вершин»  уже  есть  вершина  для  этой 
		локации, добавьте новую вершину только в том случае, если стоимость пути до 
		новой  вершины  меньше  стоимости  пути  до  текущей. 
		Пусть данный метод вернет значение true, если новая вершина была успешно добавлена в набор, 
		и false в противном случае. 
	**/
	public boolean addOpenWaypoint(Waypoint newWP)
	{
		/**получаем положение указанной точки**/
		Location lc = newWP.getLocation();        
		/** В  наборе  «открытых  вершин»  уже  есть  вершина  для  этой локации?**/
		if (openWaypoints.containsKey(lc))
		{
			/** Если  в  наборе  «открытых  вершин»  уже  есть  вершина  для  этой локации, 
				добавьте новую вершину только в том случае, если 
				стоимость пути до новой  вершины  меньше  стоимости  пути до  текущей. 
				Путь ДО вершины - это previous, а не total !!! 
			(actual cost of getting to this point from the startinglocation) **/
			
			Waypoint currentWP = openWaypoints.get(lc);
			if (newWP.getPreviousCost() < currentWP.getPreviousCost())
			{
				/**
					Данный шаг довольно прост - замените предыдущую точку на новую, используя метод 
					HashMap.put(), который заменит старое значение на новое.
				**/
				openWaypoints.put(lc, newWP);
				return true;
			}
			} else {
			/** Если в наборе «открытых вершин» в настоящее время нет вершины 
			для данного местоположения, то необходимо просто добавить новую вершину. **/
			openWaypoints.put(lc, newWP);
			return true;
		}
		return false;
	}
	
	/** Возвращает число открытых вершин **/    
	public int numOpenWaypoints()
	{
		System.out.printf("Open WPs : %d  \n", openWaypoints.size());
		return openWaypoints.size();
	}
	/**
		Эта  функция  должна  возвращать  значение  true,  если  указанное 
		местоположение встречается в наборе закрытых вершин, и false в противном 
		случае. Так как закрытые вершины хранятся в хэш-карте с расположениями в 
		качестве ключевых значений, данный метод достаточно просто в реализации.
	**/
	public boolean isLocationClosed(Location lc)
	{
		return closedWaypoints.containsKey(lc);
	}
	
	/**
		Эта функция перемещает вершину из набора «открытых вершин» в набор 
		«закрытых  вершин».  Так  как  вершины  обозначены  местоположением,  метод 
		принимает местоположение вершины. 
		Ничего не возвращает.
	**/
	public void closeWaypoint(Location lc)
	{
		/**
			Удалите вершину, соответствующую указанному местоположению, 
			из набора «открытых вершин». 
		**/
		System.out.printf("BEFORE DELETE from open - %d \n",openWaypoints.size());
		//Waypoint currentWP = openWaypoints.remove(lc);
		Waypoint currentWP = openWaypoints.get(lc);
		openWaypoints.remove(lc);
		System.out.printf("AFTER DELETE from open - %d \n",openWaypoints.size());
		if (openWaypoints.containsKey(lc))
		System.out.println("NOT DELETED");
		/**
			Добавьте вершину, которую вы удалили, в набор закрытых вершин. 
			Ключом должно являться местоположение точки.  
		**/       
		closedWaypoints.put(lc, currentWP);
		if (closedWaypoints.containsKey(lc))
		System.out.printf("Added to CLOSED, size of closed %d \n",closedWaypoints.size());
	}
	
	
}

