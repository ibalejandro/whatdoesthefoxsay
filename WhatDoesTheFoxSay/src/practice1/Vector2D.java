/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package practice1;

/**
 *
 * @author Alejandro Sánchez Aristizábal y Santiago Vanegas Gil.
 */
public class Vector2D {
    
    private double [] vector2D;
    
    public Vector2D(double xCenter, double yCenter, double radius) {
        double [] circleVector = new double[3];
        circleVector[0] = xCenter;
        circleVector[1] = yCenter;
        circleVector[2] = radius;
        this.vector2D = circleVector;
    }
    
    public static Vector2D multMat2DVect2D(Matrix2D m2D, Vector2D v2D){
        Vector2D resultVect2D = new Vector2D(0.0, 0.0, 0.0);
        //Los valores deben iniciar todos en 0.
        resultVect2D.setVector2D(getEmptyVector2D());
        for(int i = 0; i < m2D.getMatrix2D().length; i++){
            for(int j = 0, k = 0; j < m2D.getMatrix2D()[0].length; j++, k++){
                resultVect2D.getVector2D()[i] += m2D.getMatrix2D()[i][j] * 
                                                 v2D.getVector2D()[k];
            }
            
        }
        return resultVect2D;
    }
    
    public static double [] getEmptyVector2D(){
         return new double [] {0.0, 0.0, 0.0};
    }
    
    public double getCX() {
        return vector2D[0];
    }

    public void setCX(double x) {
        this.vector2D[0] = x;
    }

    public double getCY() {
        return vector2D[1];
    }

    public void setCY(double y) {
        this.vector2D[1] = y;
    }

    public double getR() {
        return vector2D[2];
    }

    public void setR(double r) {
        this.vector2D[2] = r;
    }

    public double[] getVector2D() {
        return vector2D;
    }

    public void setVector2D(double [] vector2D) {
        this.vector2D = vector2D;
    }
    
    
}
