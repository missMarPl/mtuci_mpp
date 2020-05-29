/**
	* This class represents a specific location in a 2D map.  Coordinates are
	* integer values.
**/
public class Location
{
    /** X coordinate of this location. **/
    public int xCoord;
	
    /** Y coordinate of this location. **/
    public int yCoord;
	
	
    /** Creates a new location with the specified integer coordinates. **/
    public Location(int x, int y)
    {
        xCoord = x;
        yCoord = y;
	}
	
    /** Creates a new location with coordinates (0, 0). **/
    public Location()
    {
        this(0, 0);
	}
    
    /** Обеспечить реализацию метода equals  **/
    @Override public boolean equals(Object obj) {
        
		if (obj instanceof Location) {
			Location otherLoc = (Location) obj;
			if (xCoord == otherLoc.xCoord && yCoord == otherLoc.yCoord) {
                return true;
			}  else    
			return false;
		} else    
        return false;
	}
    
	/**  Обеспечить реализацию метода hashcode  
		метод hashCode() возвращает для любого объекта 32-битное число типа int
	Если в нашей программе будут сравниваться объекты, гораздо проще сделать это по хэш-коду, и только если они равны по hashCode() — переходить к сравнению по equals().**/
    @Override	public int hashCode() {
        int result = 19;  // random prime number
		
        // Use another prime to combine
        result = 53 * result + ((Integer)xCoord).hashCode();
        result = 53 * result + ((Integer)yCoord).hashCode();
		
        return result;
	}
    
}
