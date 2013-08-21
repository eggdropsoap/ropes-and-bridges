package eggdropsoap.ropesandbridges;

import java.lang.Math;

public class FreeHangingRopeCurve {

	private class _Coord {
		public int x = 0;
		public int y = 0;
		public int z = 0;
		public _Coord(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public _Coord(){}
	};
	private _Coord lowside;
	private _Coord highside;
	private int ropeLength;
	private double horizontalDistance;
	private double droop;
	private class _y_func {
		double mu;
		double k;
		double x1;
		double L1;
		
		public _y_func() {
			L1 = _Find_L1(droop, ropeLength, highside.y - lowside.y);
			mu = (2*droop) / (Math.pow(L1, 2) - Math.pow(droop, 2) );
			k = lowside.y - droop - Math.pow(mu, -1);
			x1 = _asinh(mu * L1) / mu;
			System.out.printf("y = %3.3fcosh(%3.3f x - %3.3f) + %3.3f\n", Math.pow(mu, -1), mu, mu*x1, k);
		}
		
		public double get(double x) {
			return Math.pow(mu, -1) * Math.cosh(mu * x - mu * x1) + k;
		}
	}
	private _y_func yfunc;

	private double _getDistance(_Coord a, _Coord b) {
		return Utils.getDistance(a.x, a.y, a.z, b.x, b.y, b.z);
	}
	
	private double _getHorizontalDistance(_Coord a, _Coord b) {
		return Utils.getHorizontalDistance(a.x, a.y, a.z, b.x, b.y, b.z);
	}
	
	protected FreeHangingRopeCurve(int ropeLength,
			int side1x, int side1y, int side1z,
			int side2x, int side2y, int side2z)
	{
		this.ropeLength = ropeLength;
		if ( side1y <= side2y ) {
			this.lowside = new _Coord(side1x, side1y, side1z);
			this.highside = new _Coord(side2x, side2y, side2z);
		}
		else {
			this.highside = new _Coord(side1x, side1y, side1z);
			this.lowside = new _Coord(side2x, side2y, side2z);
		}
		this.horizontalDistance = _getHorizontalDistance(this.lowside, this.highside);
		System.out.printf("[Ropes and Bridges] Horizontal Distance is %3.3f.\n",
				this.horizontalDistance);
		this.droop = _getDroop();
		System.out.printf("[Ropes and Bridges] Droop is %3.3f (%3.3fm above void) on a %dm rope from %d, %d, %d to %d, %d, %d.\n",
    			droop, lowside.y - droop, ropeLength,
    			side1x, side1y, side1z,
    			side2x, side2y, side2z);
		double L1 = _Find_L1(droop, ropeLength, highside.y - lowside.y);
		System.out.printf("[Ropes and Bridges] L1 is %3.3f.\n", L1);
		this.yfunc = new _y_func();
		for (int i = 0; i <= horizontalDistance; i++) {
			System.out.printf("[Ropes and Bridges] %d,%3.2f\n", i, yfunc.get(i));
		}
	}
	
	
	private double _getDroop() {
		return _Solve_h(highside.y - lowside.y, ropeLength, horizontalDistance);
	}
	
	public double getDroop() {
		return droop;
	}
	
	public int getBlockHeight(int x, int z) {
		return 0; //FIXME
	}
	
	public double getHeight(double x, double z) {
		return 0.0; //FIXME
	}
	
	// implements equation (10) to find horizontal length L1 from bottom of the curve to the lowest side 
	private double _Find_L1(double h, double L, double a) {
		return -( ( h * L - Math.sqrt( h * (a + h) * (Math.pow(L, 2) - Math.pow(a, 2)) ) ) / a );
	}
	
	// asinh isn't defined in java.lang.Math
	private double _asinh(double x)
	{
		return Math.log(x + Math.sqrt(x*x + 1.0));
	}
	
	private static double MAXERR = 1e-10;			// Absolute precision of calculation
	private static double MAXIT = 100;				// Maximum iterations (will never reach 100 unless an error has occurred)
	
	// a very direct adaptation of Ruud v Gessel's C++ atanh() to Java 
	private double _atanh(double x) {
		return 0.5 * Math.log((1+x)/(1-x));
	}
	
	// a very direct adaptation of Ruud v Gessel's C++ Calc_D() to Java 
	private double _Calc_D(double a, double L, double h, double sgn)	// Calculates d from equation 11
	{
		double q=2*sgn*Math.sqrt(h*(a+h)*(L*L-a*a));							// + or - 2* the root used in (11)
		return ((L*L-a*a)*(a+2*h)-L*q)/(a*a)*_atanh(a*a/(L*(a+2*h)-q));	// return calculated d from eq (11)
	}
	
	// a very direct adaptation of Ruud v Gessel's C++ Solve_h() to Java 
	private double _Solve_h(double a, double L, double d)	// Routine to solve h from a, L and d
	{
		int n=0;												// Iteration counter (quit if >MAXIT)
		double s=((L*L-a*a)/(2*a)*Math.log((L+a)/(L-a))<d) ?-1:1;	// Left or right of Y axis ?
		double lower=0, upper=(L-a)/2;							// h must be within this range
		
		while((upper-lower) > MAXERR && (++n)<MAXIT)			// Repeat until range narrow enough or MAXIT
			if(_Calc_D(a,L,(upper+lower)/2,s)*s<d*s)
				upper=(upper+lower)/2;
			else lower=(upper+lower)/2;	// Narrows the range of possible h

		System.out.printf("Found h=%3.10f after %d iterations.\n",(upper+lower)/2,n);	// If you see 100 iterations assume an error
		return s*((upper+lower)/2);											// Returns h (- signals right of Y axis)
	}
	
	// a very direct adaptation of Ruud v Gessel's C++ Solve_L() to Java 
	private double _Solve_L(double a, double h, double d)	// Routine to solve L from a, h and d
	{
		int n=0;												// Iteration counter (quit if >MAXIT)
		double lower=Math.sqrt((d*d+a*a)), upper=2*h+d+a;			// L must be within this range
		
		while((upper-lower) > MAXERR && (++n)<MAXIT)			// Repeat until range narrow enough or MAXIT
			if(_Calc_D(a,(upper+lower)/2,h,1)>d)
				upper=(upper+lower)/2;
			else lower=(upper+lower)/2;		// Narrows the range of possible L

		System.out.printf("Found L=%3.10f after %d iterations.\n",(upper+lower)/2,n); // If you see 100 iterations assume an error
		return (upper+lower)/2;												// Returns L
	}
}


/* Reference C code from
 * http://members.chello.nl/j.beentjes3/Ruud/catfiles/catenary.pdf
 */

/**********************************************************************
*
*       Writen by Ruud v Gessel october 2007 for fun
*
* An example of how to handle numerical solutions for the examples
* used in catenary.pdf which is located on my site.
* 
* The methode used to solve the atanh equations is fairly easy, 
* it looks a bit like a binary search methode. Just have a look
* at the functions and you will understand.
*
* The program was created from scratch in a pretty short time and
* may (or will) therefore contain errors.
*
* HAVE FUN
* Ruud.
*
*********************************************************************/

/*
#include "stdafx.h"
#include "math.h"

#define MAXERR 1e-10			// Absolute precision of calculation
#define MAXIT 100				// Maximum iterations (will never reach 100 unless an error has occurred)
#define TV ((upper+lower)/2)	// Test value for our iteration routines, gives the middle of the range of the solution



double atanh(double x)									// Not defined in math library
{
	return 0.5*log((1+x)/(1-x));						// Return atanh(x)
}

double Calc_D(double a, double L, double h, double sgn)	// Calculates d from equation 11
{
	double q=2*sgn*sqrt(h*(a+h)*(L*L-a*a));							// + or - 2* the root used in (11)
   return ((L*L-a*a)*(a+2*h)-L*q)/(a*a)*atanh(a*a/(L*(a+2*h)-q));	// return calculated d from eq (11)
}

double Solve_h(double a, double L, double d)	// Routine to solve h from a, L and d
{
	int n=0;												// Iteration counter (quit if >MAXIT)
	double s=((L*L-a*a)/(2*a)*log((L+a)/(L-a))<d) ?-1:1;	// Left or right of Y axis ?
	double lower=0, upper=(L-a)/2;							// h must be within this range
	
	while((upper-lower) > MAXERR && (++n)<MAXIT)			// Repeat until range narrow enough or MAXIT
		if(Calc_D(a,L,TV,s)*s<d*s) upper=TV; else lower=TV;	// Narrows the range of possible h

	printf("Found h=%3.10f after %d iterations.\n\r",TV,n);	// If you see 100 iterations assume an error
	return s*TV;											// Returns h (- signals right of Y axis)
}

double Solve_L(double a, double h, double d)	// Routine to solve L from a, h and d
{
	int n=0;												// Iteration counter (quit if >MAXIT)
	double lower=sqrt((d*d+a*a)), upper=2*h+d+a;			// L must be within this range
	
	while((upper-lower) > MAXERR && (++n)<MAXIT)			// Repeat until range narrow enough or MAXIT
		if(Calc_D(a,TV,h,1)>d) upper=TV; else lower=TV;		// Narrows the range of possible L

	printf("Found L=%3.10f after %d iterations.\n\r",TV,n); // If you see 100 iterations assume an error
	return TV;												// Returns L
}

double asinh(double x)										// Not defined in math library
{
	return log(x+sqrt(x*x+1));								// Return asinh(x)
}

void ShowAll(double h, double L, double h1, double h2, double d)
{
	double sgn=(h<0) ? -1: 1;								// sgn has the sign of h (- ==> right of Y axis)
	h=h*sgn;												// h always positive
	double a=h2-h1;											// a= height difference
	double L1=-(h*L-sgn*sqrt(h*(a+h)*(L*L-a*a)))/a;			// L1 from equation (10)
	double u=2*h/(L1*L1-h*h);								// u fro L1 and h
	double x1=asinh(u*L1)/u;								// x1 from u and L1
	double k=h1-h-1/u;										// k from h1, h and u
	
	// Show all values, note that h2 is calculated using all calculated values in this sub
	// it is shown only to verify the result, h2 shown her should be very close to actual h2
	
	printf("u=%1.9f, phi=%3.9f, k=%3.9f, h2=%3.9f, DP=%3.9f \n\r\n\r",u,-u*x1,k,cosh(u*d-u*x1)/u+k,h1-h);
}

int main(int argc, char* argv[])
{
	double h,L,d;
	printf("y=cosh(u.x+phi)/u+k\n\n\r");
	d=Calc_D(5,30,2,1);			// From example 1
	ShowAll(2,30,10,15,d);
	h=Solve_h(5,28,20);			// From example 2
	ShowAll(h,28,10,15,20);
	h=Solve_h(10,15,11);		// From example 3
	ShowAll(h,15,10,20,11);
	L=Solve_L(6,4,30);			// From example 4
	ShowAll(4,L,12,18,30);
	L=Solve_L(1000,999,10);		// For fun
	ShowAll(999,L,1000,2000,10);
	L=Solve_L(0.0001,0.01,10);	// For fun
	ShowAll(0.01,L,10,10.0001,10);
	return 0;
}

*/
