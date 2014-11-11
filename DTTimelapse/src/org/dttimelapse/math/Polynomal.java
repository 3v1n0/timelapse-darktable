package org.dttimelapse.math;
/************************************************************************
 *                           Polynomal v1.01                            *
 ************************************************************************
 * Copyright (C) 2007 by Michael Loesler, http://derletztekick.com      *
 *                                                                      *
 * This program is free software; you can redistribute it and/or modify *
 * it under the terms of the GNU General Public License as published by *
 * the Free Software Foundation; either version 2 of the License, or    *
 * (at your option) any later version.                                  *
 *                                                                      *
 * This program is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of       *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        *
 * GNU General Public License for more details.                         *
 *                                                                      *
 * You should have received a copy of the GNU General Public License    *
 * along with this program; if not, write to the                        *
 * Free Software Foundation, Inc.,                                      *
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.            *
 ************************************************************************/

import java.util.Vector;
import java.util.Locale;





public class Polynomal extends MatrixOperation {
	
	
	
	private double[] corx, cory;
	
	
	private int order = 0;
	
	private double X[];
	private boolean isComplete = false;



	public Polynomal(double[] xx, double[] yy, int o ) {  // constructor
		// input = x-values, y-values, order of polynom
		
		if (o < 0 || o >= xx.length)
			this.order = xx.length - 1;
		else
			this.order = o;

		
		corx = xx;
		cory = yy;
		
		this.isComplete = this.fittingPolynomal();  // calculation
		
		
		if (!this.isComplete) {
			
			System.out.println("Error in calculation");
			
			return;
		}

	}
	
	public Polynomal( double[] yy, int o ) {  // constructor
		// input = y-values, order of polynom
		
		int dimension = yy.length;
		
		// create x-values itself
		double[] xx = new double[dimension];		
  		for (int i = 0; i < dimension; i++) {
	  		xx[i] = i;
			//System.out.println (picTable.getValueAt(i, 3));
			//System.out.println("x= " + x[i] + " y= " + y[i]);
		}
		
		if (o < 0 || o >= yy.length)
			this.order = yy.length - 1;
		else
			this.order = o;

		
		corx = xx;
		cory = yy;
		
		this.isComplete = this.fittingPolynomal();  // calculation
		
		
		if (!this.isComplete) {
			
			System.out.println("Error in calculation");
			
			return;
		}

	}


	private boolean fittingPolynomal() {
		int n = this.corx.length;
		double A[][] = new double[n][this.order + 1];  // matrix
		double l[] = zeros(A.length);
		this.X = ones(this.order + 1);
		int maxIteration = 1;
		double max_dx = 2147483647.0;
		
		while (max_dx > 1.0E-13 && maxIteration-- > 0) {
			for (int i = 0; i < this.corx.length; i++) {
				for (int j = 0; j < A[i].length; j++) {
					A[i][j] = Math.pow(this.corx[i], A[i].length
							- 1 - j);
					l[i] += X[j]
							* Math.pow(this.corx[i], X.length - 1
									- j);
				}
				l[i] = this.cory[i] - l[i];
			}

			try {
				double nn[] = multi(trans(A), l); // n=A'*l
				double N[][] = multi(trans(A), A); // N=A'*A
				double Q[][] = inv(N); // Q=inv(N)
				double dx[] = multi(Q, nn); // dx = Q*n
				this.X = sum(this.X, dx);
			} catch (Exception err) {
				System.out.println("Error in matrix calculation");
				return false;
			}
		}
		return true;
	}

	public Double calculate(double xValue) {
		if (!this.isComplete)
			return null;
		double y = 0;
		for (int i = 0; i < this.X.length; i++)
			y += Math.pow(xValue, this.X.length - 1 - i) * this.X[i];
		return new Double(y);
	}

	public String toString() {
		
		if (!this.isComplete){
			return "no solution";
			
		}
			

		
//		for (int i = 0; i < this.X.length; i++)
//			strP += String.format(Locale.ENGLISH,
//					"  a<sub>%1d</sub> = %+.15f\n", (this.X.length - i - 1),
//					this.X[i]);
		System.out.println("order= " + this.order);
		
		for (int i = 0; i < corx.length; i++){
			
			System.out.println( corx[i] + " " + cory[i] + " " + this.calculate(corx[i]) );
		}

		return " ";

	}

}
