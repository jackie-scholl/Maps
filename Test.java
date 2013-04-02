import java.io.*;
import java.net.*;
import java.util.*;

public class Test {
	public static void main(String[] args) 
	{
		String data = "12345";
		List<Integer> points = new ArrayList<Integer>();
		for (int i=0; i<data.length(); i++) {
			int pt = Character.getNumericValue(data.charAt(i));
			System.out.println(pt);
			points.add(new Integer(pt));
		}
		System.out.println(points);

	}
}
