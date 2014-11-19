package org.dttimelapse.math;

/*
Copyright 2014 Rudolf Martin
 
This file is part of DTTimelapse.

DTTimelapse is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

DTTimelapse is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DTTimelapse.  If not, see <http://www.gnu.org/licenses/>.
*/



public class MovingAverage {
	// we get only y-values
	// assuming that x is a row of natural numbers starting at 0

	private double[] y;
	private int dimension; // number of values

	
	public MovingAverage(double[] yy) { // constructor
		// input = y-values
		this.dimension = yy.length;
		this.y = yy;
	}

	
	public Double calculate(int xValue, int range) {
		
		// Simple moving average
		// the hotspot is in the middle of the moving window

		if ( range < 0 ) return 0.0;
		
		double sum = 0;
		int count = 0;
		
		int start = xValue - range;
		if (start < 0) start = 0;  // begin of row
		
		int end = xValue + range;
		if (end > dimension-1) end = dimension-1;  // end of row 		
				
		for (int i = start; i <= end; i++) {
			sum = sum + y[i];
			count++ ;			
		}			
		
		double average = sum / (double) count;

		//System.out.println("count= " + count + " sum= " + sum + " average= " + average);
		
		return  average;
	}

	
	public Double calculateWeighted(int xValue, int range) {		
		// Weighted moving average
		// the hotspot is in the middle of the moving window		
		// weights are binomial	coefficients, divided by 2^n 
		//     sum of weights = 1		
		// c = n! / ( k! (n-k)! )
		//
		// n = row of pascal triangle
		// k = columns in triangle
		//
		// n is range * 2, k = 0 .. n
			
		
		//TODO correct settings at begin and end

		if ( range < 0 ) return 0.0;
		
		double sum = 0;
		int count = 0;
		
		int n = range * 2;				
		
		int start = xValue - range;
		if (start < 0) start = 0;  // begin of row
		
		int end = xValue + range;
		if (end > dimension-1) end = dimension-1;  // end of row 		
				
		for (int i = start; i <= end; i++) {
			
			double coeff = factorial(n) / factorial(count) / factorial(n-count);
			double factor = coeff / potenz( 2, n);	
			
			sum = sum + factor * y[i];
			
			//System.out.println("i= " + i + "coeff= " + coeff + " factor= " + factor );
			
			count++ ;			
		}			
		
		double average = sum ;

		//System.out.println("count= " + count + " sum= " + sum + " average= " + average);
		
		return  average;
	}
	
	
	
    public static int factorial(int n) {
        int fact = 1; // this  will be the result
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
	
    public static int potenz(int p_basis, int p_exponent) {    	 
    	int potenz = 1 ;    	 
    	 
    	for (int i =1 ; i<=p_exponent ; ++i) {    		
    		potenz = potenz * p_basis ;    	 
    	}
    	return potenz;    	 
    }
    
	
}







