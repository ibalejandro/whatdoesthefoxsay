/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package practice1;

/**
 *
 * @author Alejandro Sánchez Aristizábal y Santiago Vanegas Gil.
 */
public class Matrix2D {
    
    private double [][] matrix2D;
   
    public Matrix2D(double delX, double delY) {
        double [][] translationMatrix = new double[3][3];
        translationMatrix = getIdentityMatrix();
        translationMatrix[0][2] = delX;
        translationMatrix[1][2] = delY;
        this.matrix2D = translationMatrix;
    }
    
    public Matrix2D(double sFactor) {
        double [][] scaleMatrix = new double[3][3];
        scaleMatrix = getIdentityMatrix();
        scaleMatrix[2][2] = sFactor;
        this.matrix2D = scaleMatrix;
    }
    
    public Matrix2D(double rAngle, boolean isRotaing) {
        double [][] rotationMatrix = new double[3][3];
        rotationMatrix = getIdentityMatrix();
        rotationMatrix[0][0] = Math.cos(Math.toRadians(rAngle));
        rotationMatrix[0][1] = -Math.sin(Math.toRadians(rAngle));
        rotationMatrix[1][0] = Math.sin(Math.toRadians(rAngle));
        rotationMatrix[1][1] = Math.cos(Math.toRadians(rAngle));
        this.matrix2D = rotationMatrix;
    }
    
     public static double[][] getIdentityMatrix() {
        return new double [][] {{1, 0, 0},
                                {0, 1, 0},
                                {0, 0, 1}};
    }
     
    public double[][] getMatrix2D() {
        return matrix2D;
    }

    public void setMatrix2D(double[][] matrix2D) {
        this.matrix2D = matrix2D;
    } 
}
