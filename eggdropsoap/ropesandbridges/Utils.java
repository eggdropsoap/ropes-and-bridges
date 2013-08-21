package eggdropsoap.ropesandbridges;

import net.minecraft.util.MathHelper;

public class Utils {

	public static double getDistance(int x, int y, int z, int x2, int y2, int z2) {
		double d1 = x - x2;
		double d2 = y - y2;
		double d3 = z - z2;
		return (double)MathHelper.sqrt_double(d1 * d1 + d2 * d2 + d3 * d3);
	}
	
	public static double getHorizontalDistance(int x, int y, int z, int x2, int y2, int z2) {
		return getDistance(x, y, z, x2, y, z2);	// use the same y, doesn't matter which
	}

}
