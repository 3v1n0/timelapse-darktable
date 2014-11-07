package org.dttimelapse.math;
/************************************************************************
 *                       MatrixOperation v3.1                           *
 ************************************************************************
 * Copyright (C) 2005-07 by Michael Loesler, http://derletztekick.com   *
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

class MatrixOperation {
  public MatrixOperation() {  }
  
  private static double mashEPS(){
    double eps=1,x=2,y=1;
    while (y<x) {
      eps *= 0.5;
      x = 1+eps;
    }
    return eps;
  }

  private static final double eps = mashEPS();

  public static double[] zeros(int o){
    double[] M = new double[o];
    for (int i=0; i<o; i++)
      M[i]=0.0;
    return M;
  }

  public static double[][] zeros(int o, int p){
    double[][] M = new double[o][p];
    for (int i=0; i<o; i++)
      for (int j=0; j<p; j++)
        M[i][j]=0.0;
    return M;
  }
  
  public static double[] ones(int o){
    double[] M = new double[o];
    for (int i=0; i<o; i++)
      M[i]=1.0;
    return M;
  }
  
  public static double[][] ones(int o, int p){
    double[][] M = new double[o][p];
    for (int i=0; i<o; i++)
      for (int j=0; j<p; j++)
        M[i][j]=1.0;
    return M;
  }
  
  public static double[][] diag(int o, double d){
    double[][] M = new double[o][o];
    for (int i=0; i<o; i++)
      M[i][i]=d;
    return M;
  }
  
  public static double[][] diag(double[] v){
    double[][] M = new double[v.length][v.length];
    for (int i=0; i<v.length; i++)
      M[i][i]=v[i];
    return M;
  }
  
  public static double det(double M[][]) {
    if (M.length != M[0].length)
      return 0;
    int n = M.length;
    double d[] = new double[n];
    double det = 1;
    double perm[] = new double[n];
    double LU[][] = new double[n][n];
    for (int i=0; i<n; i++){
      for (int j=0; j<n; j++)
        LU[i][j] = M[i][j];
    }
    for (int i=0; i<n; i++){
      perm[i] = i;
      double zmax = 0.0;
      for (int j=0; j<n; j++)
        zmax = Math.max(zmax, Math.abs(LU[i][j]));
      if (zmax == 0)
        return 0;
      d[i] = zmax;
    }

    for (int i=0; i<n-1; i++){
      double piv = Math.abs(LU[i][i]) / d[i];
      int j0 = i;
      for (int j=i+1; j<n; j++) {
        double tmp = Math.abs( LU[j][i] ) / d[j];
        if (piv < tmp){
          piv = tmp;
          j0 = j;
        }
      }
      if (piv < eps)
        return 0;

      if (j0 != i){
        det *= -1;
        double tmp = perm[j0];
        perm[j0] = perm[i];
        perm[i] = tmp;

        tmp = d[j0];
        d[j0] = d[i];
        d[i] = tmp;

        for (int k=0; k<n; k++){
          tmp = LU[j0][k];
          LU[j0][k] = LU[i][k];
          LU[i][k] = tmp;
        }

      }
      for (int j=i+1; j<n; j++)
        if (LU[j][i] != 0){
          LU[j][i] /= LU[i][i];
          double tmp = LU[j][i];
          for (int k=i+1; k<n; k++)
            LU[j][k] -= tmp*LU[i][k];
        }
    }

    if (Math.abs( LU[n-1][n-1] ) < eps){
      return 0;
    }

    for (int i=0; i<M.length; i++)
      det *= LU[i][i];
    return det;
  }
  
  public static double abs (double M[]) {
    double R = 0;
    for (int i=0; i<M.length; i++)
      R += M[i]*M[i];
    return Math.sqrt(R);
  }
  
  public static double[] sum (double d, double M[]) {
    for (int i=0; i<M.length; i++)
      M[i] += d;
    return M;
  }

  public static double[] diff (double d, double M[]) {
    for (int i=0; i<M.length; i++)
      M[i] -= d;
    return M;
  }
  
  public static double[][] sum (double d, double M[][]) {
    for (int i=0; i<M.length; i++)
      for (int j=0; j<M[i].length; j++)
        M[i][j] += d;
    return M;
  }

  public static double[][] diff (double d, double M[][]) {
    for (int i=0; i<M.length; i++)
      for (int j=0; j<M[i].length; j++)
        M[i][j] -= d;
    return M;
  }

  public static double[][] sum (double M1[][], double M2[][]) {
    return getSumMatrix(M1,M2,'+');
  }
  
  public static double[][] diff (double M1[][], double M2[][]) {
    return getSumMatrix(M1,M2,'-');
  }
  
  public static double[] sum (double M1[], double M2[]) {
    return getSumMatrix(M1,M2,'+');
  }

  public static double[] diff (double M1[], double M2[]) {
    return getSumMatrix(M1,M2,'-');
  }
  
  private static double[] getSumMatrix (double M1[], double M2[], char OperationType){
    if (M1.length != M2.length)
      return null;
    int op = (OperationType == '+'?1:-1);
    double R[] = new double [M1.length];
    for (int i=0; i<M1.length; i++)
      R[i] = M1[i] + op * M2[i];
    return R;
  }

  private static double[][] getSumMatrix (double M1[][], double M2[][], char OperationType){
    if ((M1.length != M2.length) || (M1[0].length != M2[0].length))
      return null;
    int op = (OperationType == '+'?1:-1);
    double R[][] = new double [M1.length][M1[0].length];
    for (int i=0; i<M1.length; i++)
      for (int j=0; j<M2[i].length; j++)
        R[i][j] = M1[i][j] + op * M2[i][j];
    return R;
  }
  
  public static double[] multi (double d, double[] M){
    for (int i=0; i<M.length; i++)
      M[i] *= d;
    return M;
  }
  
  public static double[][] multi (double d, double[][] M){
    for (int i=0; i<M.length; i++)
      for (int j=0; j<M[i].length; j++)
        M[i][j] *= d;
    return M;
  }
  
  public static double[][] multi (double M1[], double M2[], boolean returnScalar){
    if (M1.length != M2.length)
      return null;
    if (returnScalar){
      double R[][] = new double[1][1];
      for (int i=0; i<M1.length; i++)
        R[0][0] += M1[i] * M2[i];
      return R;
    }
    else {
      double M1V[][] = new double[M1.length][1];
      double M2H[][] = new double[1][M2.length];
      for (int i=0; i<M1.length; i++) {
        M1V[i][0] = M1[i];
        M2H[0][i] = M2[i];
      }
      return multi(M1V,M2H);
    }

  }
  
  public static double[] multi (double M1[][], double M2[]){
    if (M1[0].length != M2.length)
      return null;
    double R[] = new double[M1.length];
    for (int i=0; i<M1.length; i++)
      for (int j=0; j<M2.length; j++)
        R[i] += M1[i][j] * M2[j];
    return R;
  }
  
  public static double[] multi (double M1[], double M2[][]){
    if (M1.length != M2.length)
      return null;
    double R[] = new double[M2[0].length];
    for (int i=0; i<M2[0].length; i++)
      for (int j=0; j<M1.length; j++)
        R[i] += M1[j] * M2[j][i];
    return R;
  }

  public static double[][] multi (double M1[][], double M2[][]){
    if (M1[0].length != M2.length)
      return null;
    double R[][] = new double[M1.length][M2[0].length];
    for (int i=0; i<M1.length; i++)
      for (int j=0; j<M2[0].length; j++)
        for (int k=0; k<M1[0].length; k++)
          R[i][j] += M1[i][k] * M2[k][j];
    return R;
  }

  public static double[][] trans(double M[][]){
    double MT[][] = new double [M[0].length][M.length];
    for (int i=0; i<M.length; i++)
      for (int j=0; j<M[i].length; j++)
        MT[j][i] = M[i][j];
    return MT;
  }

  public static double[][] inv(double M[][]) {
    if (M.length != M[0].length)
      return null;
    int n = M.length;
    double spur=0.0, normA=0.0, normB=0.0;
    int permx[] = new int[n];
    int permy[] = new int[n];
    double invM[][] = new double[n][n];
    for (int i=0; i<n; i++) {
      permx[i] = -1;
      permy[i] = -1;
      for (int j=0; j<n; j++)
        invM[i][j] = M[i][j];
    }

    for (int i=0; i<n; i++){
      double piv = 0.0;
      int nx=0, ny=0;
      for (int ix=0; ix<n; ix++)
        if (permx[ix] == -1) {
          for (int iy=0; iy<n; iy++)
            if (permy[iy] == -1 && Math.abs(piv) < Math.abs(invM[ix][iy])) {
              piv = invM[ix][iy];
              nx = ix;
              ny = iy;
            }
        }
      if (Math.abs(piv) < eps) {
        System.out.println("Fehler, Matrix ist singulaer... "+ Math.abs(piv));
        return null;
      }
      permx[nx] = ny;
      permy[ny] = nx;
      double tmp = 1.0 / piv;
      for (int j=0; j<n; j++)
        if (j!=nx) {
          double faktor = invM[j][ny] * tmp;
          for (int k=0; k<n; k++)
            invM[j][k] -= invM[nx][k] * faktor;
          invM[j][ny] = faktor;
        }
      for (int j=0; j<n; j++)
        invM[nx][j] *= -tmp;
      invM[nx][ny] = tmp;
    }

    for (int i=0; i<n; i++){
      for (int j=i; j<n; j++){
        if (permx[j]==i){
          for (int k=0; k<n; k++){
            double tmp = invM[i][k];
            invM[i][k] = invM[j][k];
            invM[j][k] = tmp;
          }
          permx[j] = permx[i];
          permx[i] = i;
          break;
        }
      }

      for (int j=i; j<n; j++){
        if (permy[j]==i){
          for (int k=0; k<n; k++){
            double tmp = invM[k][i];
            invM[k][i] = invM[k][j];
            invM[k][j] = tmp;
          }
          permy[j] = permy[i];
          permy[i] = i;
          break;
        }
      }
    }

//    for (int i=0; i<n; i++){
//      double h1=0, h2=0;
//      for (int j=0; j<n; j++){
//        double tmp = 0;
//        for (int k=0; k<n; k++)
//          tmp += M[i][k]*invM[k][j];
//        spur += Math.abs(tmp);
//        h1 += Math.abs(M[i][j]);
//        h2 += Math.abs(invM[i][j]);
//      }
//      normA = Math.max(h1, normA);
//      normB = Math.max(h2, normB);
//      double cond = normA*normB;
//    }
//    spur -= n;
    return invM;
  }
  
  public static double[] solve(double[][] A, double[] b){
    if (A.length != A[0].length)
      return null;
    double [][] invA = inv(A);
    if (invA == null)
      return null;
    return multi(invA,b);
  }
  
  public static double[] cross(double a[], double b[]){
    if (a.length != 3 || b.length != 3)
      return null;
    double v[] = new double[3];
    v[0] = a[1]*b[2] - a[2]*b[1];
    v[1] = a[2]*b[0] - a[0]*b[2];
    v[2] = a[0]*b[1] - a[1]*b[0];
    return v;
  }
  
  public static double dot(double a[], double b[]){
    return multi(a, b, true)[0][0];
  }
  
  public static double vectorAngle(double a[], double b[]) {
    double abs_a = abs(a);
    double abs_b = abs(b);
    if (abs_a == 0 || abs_b == 0)
      return 0;
    return Math.acos(dot(a,b)/(abs_a*abs_b));
  }
  
  public static double[] unit(double v[]){
    double abs = abs(v);
    if (abs == 0)
      return null;
    for (int i=0; i<v.length; i++)
      v[i] /= abs;
    return v;
  }

  public static void print_r(double M[]){
    for (int i=0; i<M.length; i++)
      System.out.print(M[i]+"\n");
  }

  public static void print_r(double M[][]){
    for (int i=0; i<M.length; i++){
      for (int j=0; j<M[i].length; j++)
        System.out.print(M[i][j]+"  ");
      System.out.println();
    }
  }

}

