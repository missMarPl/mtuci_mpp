import java.io.*;
import java.util.Scanner;

public class Lab1
{
	public static void main(String args[ ])
	{
		double[] coords = new double[10]; ;
        Scanner in = new Scanner(System.in);
        System.out.println("Input x,y,z coordinates of 1 point: ");
        for (int i=0;i<3;i++)
        coords[i] = in.nextDouble();
        
        Point3d one = new Point3d(coords[0],coords[1],coords[2]); 
        
        System.out.println("Input x,y,z coordinates of 2 point: ");
        for (int i=0;i<3;i++)
        coords[i] = in.nextDouble();
        
        Point3d two = new Point3d(coords[0],coords[1],coords[2]);
		
        System.out.println("Input x,y,z coordinates of 3 point: ");
        for (int i=0;i<3;i++)
        coords[i] = in.nextDouble();
        
        Point3d three = new Point3d(coords[0],coords[1],coords[2]);        
        
        in.close();
        
        System.out.printf("Your points: \n 1: %f %f %f \n 2: %f %f %f \n 3: %f %f %f \n", one.getX(), one.getY(), one.getZ(), two.getX(), two.getY(), two.getZ(), three.getX(), three.getY(), three.getZ());
        
        System.out.printf("Distances: \n 1-2: %f \n 2-3: %f \n 3-1: %f \n", one.distanceTo(two), two.distanceTo(three), three.distanceTo(one));
        
		System.out.printf("Triangle Area: %f \n", computeArea(one,two,three));
	}
	
	public static double computeArea(Point3d first, Point3d second, Point3d third)
	{
		double area;
		if (!first.isEqual(second) && !second.isEqual(third) && !third.isEqual(first)) {
			double[] sides = {first.distanceTo(second), second.distanceTo(third), third.distanceTo(first)}; 
			double p = (sides[0]+sides[1]+sides[2])/2;
			area = Math.sqrt(p*(p-sides[0])*(p-sides[1])*(p-sides[2]));
			} else {
			System.out.println("ERROR: this is NOT a trianlge! \n");
			area=0;
		} 
		return area;
	}
}