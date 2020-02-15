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
    public boolean equals(Location otherLoc) {
        
            if (xCoord == otherLoc.xCoord && yCoord == otherLoc.yCoord) {
                return true;
            }  else    
        /** Если не равны - возвращаем false **/
        return false;
    }
    
	/**  Обеспечить реализацию метода hashcode  
    метод hashCode() возвращает для любого объекта 32-битное число типа int
    Если в нашей программе будут сравниваться объекты, гораздо проще сделать это по хэш-коду, и только если они равны по hashCode() — переходить к сравнению по equals().**/
	public int hashCode() {
        int result = 7;
        result = 31 * result + xCoord;
        result = 31 * result + yCoord;
        return result;
    }
    
}
